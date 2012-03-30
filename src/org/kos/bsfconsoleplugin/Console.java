package org.kos.bsfconsoleplugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;
import org.kos.bsfconsoleplugin.languages.CompletionManager;
import org.kos.bsfconsoleplugin.actions.console.NopAction;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.InputEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Console component.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class Console extends JScrollPane implements KeyListener {
	public static final String CONSOLE_GROUP = "BSFConsole.ConsoleGroup";
	/**
	 * End Of Command character.
	 */
	public static final char EOC = '\u0001';
	/**
	 * System.out color.
	 */
	public static final Color OUT_COLOR = new Color(0, 128, 0); //todo: make customizable
	/**
	 * System.err color.
	 */
	public static final Color ERR_COLOR = Color.RED; //todo: make customizable
	/**
	 * Prompt color.
	 */
	public static final Color PROMPT_COLOR = Color.BLUE; //todo: make customizable
	/**
	 * Completions color.
	 */
	public static final Color COMPLETIONS_COLOR = Color.GRAY; //todo: make customizable
	public static final Color INPUT_COLOR = Color.BLACK;

	private static final Icon ERROR_ICON = IconLoader.getIcon("/compiler/error.png");

	private OutputStream outPipe;
	private InputStream inPipe;
	private InputStream errPipe;
	private InputStream in;
	private PrintStream out;
	private PrintStream err;

	private CompletionManager completionManager;
	private String prompt;
	private volatile boolean busy;

	private int cmdStart;
	private List<String> history = new LinkedList<String>();
	private String startedLine;
	private int histLine;

	private volatile String suspendedCommand;

	private final AttributeSet defaultStyle;
	private JTextPane text;
	//private DefaultStyledDocument doc;

	final int SHOW_AMBIG_MAX = 10;

	// hack to prevent key repeat for some reason?
	private final BSFConsolePlugin plugin;
	private boolean constructed;

	public Console(final BSFConsolePlugin plugin) {
		this(plugin, null, null, null);
	}

	public Console(final BSFConsolePlugin plugin, @Nullable final InputStream cin, @Nullable final InputStream cerr, @Nullable final OutputStream cout) {
		this.plugin = plugin;
		constructed = false;

		// Special TextPane which catches for cut and paste, both L&F keys and
		// programmatic	behaviour
		text = new JTextPane(/*doc = */new DefaultStyledDocument()) {
			@Override
			public void cut() {
				if (text.getCaretPosition() < cmdStart) {
					super.copy();
				} else {
					super.cut();
				}
			}

			@Override
			public void paste() {
				prepareForInput();
				super.paste();
			}

			@Override
			public boolean isFocusOwner() {
				return isEnabled() && super.isFocusOwner(); //dirty hack
			}
		};

		text.setCaret(new ConsoleCaret());
		text.setText("");
//		final Font font = new Font("Monospaced", Font.PLAIN, 14);
//		text.setFont(font);
		text.setMargin(new Insets(7, 5, 7, 5));

		text.addKeyListener(this);
		setViewportView(text);

		final SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attributeSet, INPUT_COLOR);
		defaultStyle = attributeSet;

		outPipe = cout;
		if (outPipe == null) {
			outPipe = new PipedOutputStream();
			try {
				in = new PipedInputStream((PipedOutputStream) outPipe);
			} catch (IOException e) {
				print("Console internal error (1)...", ERR_COLOR);
			}
		}

		inPipe = cin;
		if (inPipe == null) {
			final PipedOutputStream pout = new PipedOutputStream();
			out = new PrintStream(pout);
			try {
				inPipe = new BlockingPipedInputStream(pout);
			} catch (IOException e) {
				print("Console internal error: " + e);
			}
		}

		errPipe = cerr;
		if (errPipe == null) {
			final PipedOutputStream perr = new PipedOutputStream();
			err = new PrintStream(perr);
			try {
				errPipe = new BlockingPipedInputStream(perr);
			} catch (IOException e) {
				print("Console internal error: " + e);
			}
		}

		// Start the inpipe watcher
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							inPipeWatcher();
						} catch (IOException e) {
							print("Console: I/O Error: " + e + "\n", ERR_COLOR);
						}
					}
				}, "inPipeWatcher"
		).start();

		new Thread(
				new Runnable() {
					@Override
					public void run() {
						try {
							errPipeWatcher();
						} catch (IOException e) {
							print("Console: I/O Error: " + e + "\n", ERR_COLOR);
						}
					}
				}, "errPipeWatcher"
		).start();

		registerActions();
		focus();
		constructed = true;
	}

	void prepareForInput() {
		assertEDT();
		forceCaretMoveToInput();
		setDefaultStyle();
	}

	private void registerActions() {
		final ActionManager mgr;
		try {
			mgr = ActionManager.getInstance();
		} catch (NullPointerException e) {
			//we're in tests, ignore
			return;
		}
		final ActionGroup ag = (ActionGroup) mgr.getAction(CONSOLE_GROUP);
		final AnAction[] actions = ag.getChildren(null);
		for (final AnAction action : actions) {
			action.registerCustomShortcutSet(action.getShortcutSet(), this);
		}

		new NopAction().registerCustomShortcutSet(new CustomShortcutSet(
				new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK), null),
				new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK), null)
		), this);
	}

	public Reader getIn() {
		return new InputStreamReader(in);
	}

	public PrintStream getOut() {
		return out;
	}

	public PrintStream getErr() {
		return err;
	}

	public void setPrompt(final String prompt) {
		this.prompt = prompt;
	}

	public void setCompletionManager(final CompletionManager completionManager) {
		this.completionManager = completionManager;
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		type(e);
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		type(e);
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		type(e);
	}

	public void simulateInput(final String input) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (busy)
					return;

				//remove typed command
				final String lastLine = getLastTextLine();
				final String cmd = lastLine.substring(prompt.length() + 1);
				final int cmdLength = cmd.length();
				if (cmdLength > 0) {
					final int textLength = getTextLength();
					text.setCaretPosition(textLength - cmdLength);
					text.moveCaretPosition(textLength);
					text.replaceSelection("");
				}

				append(input + "\n");
				_setWaitFeedback(true);
				try {
					if (cmdLength > 0)
						suspendedCommand = cmd;
					acceptLine(input + EOC + "\n");
					resetCommandStart();
					moveCaretToCmdStart();
				} finally {
					_setWaitFeedback(false);
				}
			}
		});
	}

	private void type(final KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_BACK_SPACE:
				if (text.getCaretPosition() <= cmdStart) {
					// This doesn't work for backspace.
					// See default case for workaround
					e.consume();
				}
				break;

			case KeyEvent.VK_RIGHT:
				forceCaretMoveToStart();
				break;

			default:
				if (!e.isActionKey()) {
					if (!(e.isAltDown() || e.isAltGraphDown() ||
					      e.isControlDown() || e.isMetaDown() || e.isShiftDown())) {
						// plain character
						prepareForInput();
					}
				}

				/*
					The getKeyCode function always returns VK_UNDEFINED for
					keyTyped events, so backspace is not fully consumed.
				*/
				if (e.paramString().contains("Backspace")) {
					if (text.getCaretPosition() <= cmdStart) {
						e.consume();
						break;
					}
				}

				break;
		}
	}

	public void completeInput() {
		assertEDT();
		final String part = text.getText().substring(cmdStart);
		doCommandCompletion(part);
	}

	public void clearLine() {
		assertEDT();
		replaceRange("", cmdStart, getTextLength());
		histLine = 0;
	}

	private void setDefaultStyle() {
		assertEDT();
		setStyleNoCaretMove(defaultStyle, false);
	}

	public void newLine() {
		assertEDT();
		append("\n");
	}

	public void moveCaretToCmdStart() {
		assertEDT();
		text.setCaretPosition(cmdStart);
	}

	public void selectTillCmdStart() {
		assertEDT();
		if (text.getCaretPosition() < cmdStart)
			moveCaretToCmdStart();
		else
			text.moveCaretPosition(cmdStart);
	}

	public void moveCaretToCmdEnd() {
		assertEDT();
		text.setCaretPosition(getTextLength());
	}

	public void selectTillCmdEnd() {
		assertEDT();
		if (text.getCaretPosition() < cmdStart)
			moveCaretToCmdStart();
		text.moveCaretPosition(getTextLength());
	}

	private void doCommandCompletion(final String input) {
		if (completionManager == null)
			return;

		_setWaitFeedback(true);
		final org.jdesktop.swingworker.SwingWorker<String[], Void> w = new org.jdesktop.swingworker.SwingWorker<String[], Void>() {
			@Override
			protected String[] doInBackground() throws Exception {
				return completionManager.complete(input);
			}

			@Override
			protected void done() {
				try {
					completionsComputed(input, get());
				} catch (InterruptedException ignored) {
				} catch (ExecutionException ignored) {
				} finally {
					_setWaitFeedback(false);
				}
			}
		};
		w.run();
	}

	private void completionsComputed(final String input, final String[] completions) {
		if (completions.length == 0) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}

		// Found one completion
		if (completions.length == 1) {
			append(completions[0]);
			return;
		}

		//Try to complete as much as possible
		final StringBuilder commonPrefix = new StringBuilder();
		boolean finishedOneOfTheVaiants = false;
		outerLoop:
		for (int index = 0; ; index++) {
			if (completions[0].length() <= index) {
				finishedOneOfTheVaiants = true;
				break;
			}
			final char c = completions[0].charAt(index);
			for (int j = 1; j < completions.length; j++) {
				final String completion = completions[j];
				if (completion.length() <= index) {
					finishedOneOfTheVaiants = true;
					break outerLoop;
				}
				if (completion.charAt(index) != c)
					break outerLoop;
			}
			commonPrefix.append(c);
		}


		if (finishedOneOfTheVaiants && commonPrefix.length() > 0) {
			append(commonPrefix.toString());
			return;
		}

		// Found ambiguous, show (some of) them

		// Show ambiguous
		final String lastLine = getLastLine(input);
		final StringBuffer sb = new StringBuffer("\n");
		int i;
		for (i = 0; i < completions.length && i < SHOW_AMBIG_MAX; i++)
			sb.append(lastLine).append(completions[i]).append("\n");
		if (i == SHOW_AMBIG_MAX)
			sb.append("...\n");

		print(sb, COMPLETIONS_COLOR);

		//waitAndPrintPrompt(); //can cause a deadlock
		printPrompt();
		append(input + commonPrefix); // append does not resetLanguages command start
	}

	private String getLastLine(final String line) {
		final int lineLength = line.length();

		if (lineLength == 0)
			return line;

		for (int i = lineLength - 1; i >= 0; i--) {
			final char c = line.charAt(i);
			if (c == '\n' || c == '\r') {
				if (i == lineLength - 1)
					return "";
				return line.substring(i + 1);
			}
		}

		return line;
	}

	private void resetCommandStart() {
		cmdStart = getTextLength();
	}

	private void append(final String string) {
		assertEDT();
		final int slen = getTextLength();
		text.select(slen, slen);
		text.replaceSelection(string);
	}

	private String replaceRange(final String s, final int start, final int end) {
		assertEDT();
		text.select(start, end);
		text.replaceSelection(s);
		//text.repaint();
		return s;
	}

	private void forceCaretMoveToInput() {
		assertEDT();
		if (text.getCaretPosition() < cmdStart) {
			// move caret first!
			text.setCaretPosition(getTextLength());
		}
		text.repaint();
	}

	private void forceCaretMoveToStart() {
		assertEDT();
		if (text.getCaretPosition() < cmdStart) {
			text.setCaretPosition(cmdStart);
		}
		text.repaint();
	}

	public void runCurrentCmd() {
		assertEDT();
		String s = getCmd();

		if (s.length() != 0) {
			history.add(s);
			s += EOC;
			s += "\n";
		} else {	// special hack	for empty return!
			return;
//			s = ";\n";
		}

		newLine();
		histLine = 0;
		acceptLine(s);
		text.repaint();
		resetCommandStart();
		moveCaretToCmdStart();
	}

	private String getCmd() {
		assertEDT();
		String s = "";
		try {
			s = text.getText(cmdStart, getTextLength() - cmdStart);
		} catch (BadLocationException e) {
			// should not happen
		}
		return s;
	}

	public void historyUp() {
		assertEDT();
		if (history.size() == 0)
			return;
		if (histLine == 0)  // save current line
			startedLine = getCmd();
		if (histLine < history.size()) {
			histLine++;
			showHistoryLine();
		}
	}

	public void historyDown() {
		assertEDT();
		if (histLine == 0)
			return;

		histLine--;
		showHistoryLine();
	}

	private void showHistoryLine() {
		assertEDT();
		final String showline;
		if (histLine == 0)
			showline = startedLine;
		else
			showline = history.get(history.size() - histLine);

		replaceRange(showline, cmdStart, getTextLength());
		text.setCaretPosition(getTextLength());
		text.repaint();
	}

	//String ZEROS = "000";

	private void acceptLine(final String line) {
/*
		// Patch to handle Unicode characters
		// Submitted by Daniel Leuck
		final StringBuffer buf = new StringBuffer();
		final int lineLength = line.length();
		for (int i = 0; i < lineLength; i++) {
			String val = Integer.toString(line.charAt(i), 16);
			val = ZEROS.substring(0, 4 - val.length()) + val;
			buf.append("\\u" + val);
		}
		line = buf.toString();
		// End unicode patch
*/

		if (outPipe == null)
			print("Console internal error: cannot output ...", ERR_COLOR);
		else
			try {
				outPipe.write(line.getBytes());
				outPipe.flush();
			} catch (IOException e) {
				outPipe = null;
				throw new RuntimeException("Console pipe broken...");
			}
		//text.repaint();
	}

	private void println(final String string) {
		assertEDT();
		print(string + "\n");
		text.repaint();
	}

	public void asyncWaitAndPrintln(final String string) {
		waitForInputAndRunWithoutPrompt(new Runnable() {
			@Override
			public void run() {
				println(string);
			}
		});
	}

	private void println(final String string, final Color color) {
		assertEDT();
		print(string + "\n", color);
		text.repaint();
	}

	private void print(final String string) {
		assertEDT();
		append(string == null ? "null" : string);
		resetCommandStart();
		moveCaretToCmdStart();
	}

	private void removePromptWithTyping() {
		assertEDT();
		if (prompt != null) {
			final String lastLine = getLastTextLine();
			final int promptStart = lastLine.lastIndexOf(prompt);
			if (promptStart != -1) {
				final String cmd = lastLine.substring(promptStart + prompt.length() + 1);
				final int textLength = getTextLength();
				final int lastLineLength = lastLine.length();
				final int offset = textLength - lastLineLength + promptStart;
				try {
					text.getDocument().remove(offset, textLength - offset);
				} catch (BadLocationException e) {
					assert false : e;
				}
				/*text.setCaretPosition(textLength - lastLineLength);
				text.moveCaretPosition(textLength);
				text.replaceSelection("");*/

				suspendedCommand = cmd;
			}
		}
	}

	private void error(final String s) {
		assertEDT();
		if (ERROR_ICON != null)
			print(ERROR_ICON);
		print(new StringBuffer(String.valueOf(s)).append("\n"), ERR_COLOR);
	}

	private void print(final Serializable object) {
		assertEDT();
		append(String.valueOf(object));
		resetCommandStart();
		moveCaretToCmdStart();
	}

	private void print(final Icon icon) {
		assertEDT();
		if (icon == null)
			return;

		//waitForInputEnd();
		text.insertIcon(icon);
		resetCommandStart();
		moveCaretToCmdStart();
	}

	private void print(final StringBuffer s, final Color color) {
		assertEDT();
		print(s, null, color);
	}

	private void print(final String s, final Color color) {
		assertEDT();
		print(s, null, color);
	}

	private void print(final Serializable s, @Nullable final Font font, final Color color) {
		assertEDT();
		final AttributeSet old = getStyle();

		setStyle(font, color);
		print(s);
		setStyle(old, true);
	}

	private AttributeSet setStyle(final Font font, final Color color) {
		assertEDT();
		if (font != null)
			return setStyle(
					font.getFamily(), font.getSize(), color,
					font.isBold(), font.isItalic(),
					StyleConstants.isUnderline(getStyle())
			);
		else
			return setStyle(null, -1, color);
	}

	private AttributeSet setStyle(@Nullable final String fontFamilyName, final int size, final Color color) {
		assertEDT();
		final MutableAttributeSet attr = new SimpleAttributeSet();
		if (color != null)
			StyleConstants.setForeground(attr, color);
		if (fontFamilyName != null)
			StyleConstants.setFontFamily(attr, fontFamilyName);
		if (size != -1)
			StyleConstants.setFontSize(attr, size);

		setStyle(attr);

		return getStyle();
	}

	private AttributeSet setStyle(final String fontFamilyName,
	                              final int size,
	                              final Color color,
	                              final boolean bold,
	                              final boolean italic,
	                              final boolean underline) {
		assertEDT();
		final MutableAttributeSet attr = new SimpleAttributeSet();
		if (color != null)
			StyleConstants.setForeground(attr, color);
		if (fontFamilyName != null)
			StyleConstants.setFontFamily(attr, fontFamilyName);
		if (size != -1)
			StyleConstants.setFontSize(attr, size);
		StyleConstants.setBold(attr, bold);
		StyleConstants.setItalic(attr, italic);
		StyleConstants.setUnderline(attr, underline);

		setStyle(attr);

		return getStyle();
	}

	private void setStyle(final AttributeSet attributes) {
		assertEDT();
		setStyle(attributes, false);
	}

	private void setStyle(final AttributeSet attributes, final boolean overWrite) {
		assertEDT();
		text.setCaretPosition(getTextLength());
		setStyleNoCaretMove(attributes, overWrite);
	}

	private void setStyleNoCaretMove(final AttributeSet attributes, final boolean overWrite) {
		assertEDT();
		text.setCharacterAttributes(attributes, overWrite);
	}

	private AttributeSet getStyle() {
		return text.getCharacterAttributes();
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);

		if (text != null)
			text.setFont(font);
	}

	private void inPipeWatcher() throws IOException {
		final byte[] ba = new byte[256]; //	arbitrary blocking factor
		int read;
		boolean inProgress = false;
		while ((read = inPipe.read(ba)) != -1) {
			if (plugin == null || plugin.getConfig().isOutWaitsForErr())
				waitForErrPipeEnd();
			inProgress = asyncSendPipeOutput(new String(ba, 0, read), inPipe.available() > 0, inProgress, OUT_COLOR);
		}

		println("Console: Input closed...");
	}

	private void errPipeWatcher() throws IOException {
		final byte[] ba = new byte[256]; //	arbitrary blocking factor
		int read;
		boolean inProgress = false;
		while ((read = errPipe.read(ba)) != -1) {
			if (plugin != null && plugin.getConfig().isErrWaitsForOut())
				waitForInPipeEnd();
			inProgress = asyncSendPipeOutput(new String(ba, 0, read), errPipe.available() > 0, inProgress, ERR_COLOR);
		}

		println("Console: Err closed...");
	}

	private boolean asyncSendPipeOutput(final String output, final boolean moreAvailable, final boolean alreadyInProgress, final Color color) {
		if (moreAvailable) {
			if (alreadyInProgress)
				asyncContinueSendingPipeOutput(output, color);
			else
				asyncStartSendingPipeOutput(output, color);
		} else {
			if (alreadyInProgress)
				asyncStopSendingPipeOutput(output, color);
			else
				asyncSendShortPipeOutput(output, color);
		}
		return moreAvailable;
	}

	private void asyncStartSendingPipeOutput(final String output, final Color color) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				removePromptWithTyping();
				_setWaitFeedback(true);
				print(output, color);
			}
		});
	}

	private void asyncContinueSendingPipeOutput(final String output, final Color color) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				print(output, color);
			}
		});
	}

	private void asyncStopSendingPipeOutput(final String output, final Color color) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				print(output, color);
				printPromptAndSuspendedCommand();
				_setWaitFeedback(false);
			}
		});
	}

	private void asyncSendShortPipeOutput(final String output, final Color color) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				removePromptWithTyping();
				print(output, color);
				printPromptAndSuspendedCommand();
			}
		});
	}

	@Override
	public String toString() {
		return "BSFConsole";
	}

	public void setWaitFeedback(final boolean on) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_setWaitFeedback(on);
			}
		});
		busy = on;
	}

	private void _setWaitFeedback(final boolean on) {
		assertEDT();
		if (on) {
			text.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			text.setEnabled(false);
		} else {
			text.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			text.setEnabled(true);
			text.requestFocusInWindow();
		}
//		shakeMouse();
	}

	//0.7.2: this breaks xinerama & follow focus WMs
//	// by some reason mouse cursor doesn't change in IDEA until the pointer is moved.
//	// this is an ugly hack to overcome it
//	private void shakeMouse() {
//		final Point location = MouseInfo.getPointerInfo().getLocation();
//		try {
//			new Robot().mouseMove(location.x, location.y);
//		} catch (AWTException ignored) {
//		}
//	}

//	public boolean cursorOnNewLine() {
//		final int len = getTextLength();
//		if (len == 0) return true;
//		try {
//			final char lastChar = text.getDocument().getText(len - 1, 1).charAt(0);
//			return lastChar == '\n' || lastChar == '\r';
//		} catch (BadLocationException e) {
//			return true;
//		}
//	}

	public void asyncWaitAndPrintPrompt() {
		waitForInputAndRun(new Runnable() {
			@Override
			public void run() {
				printPromptAndSuspendedCommand();
			}
		});
	}

	private void printPromptAndSuspendedCommand() {
		assertEDT();
		//if (!cursorOnNewLine())
		//	return;
		//println();
		printPrompt();
		if (suspendedCommand != null) {
			resetCommandStart();
			append(suspendedCommand);
			suspendedCommand = null;
		}
		//text.setCaretPosition(getTextLength());
		text.moveCaretPosition(getTextLength());
	}

	private void printPrompt() {
		assertEDT();
		if (getLastTextLine().contains(prompt))
			return;
		print(prompt, PROMPT_COLOR);
		print(" ");
	}

	public void asyncWaitAndPrintln(final String message, final Color color) {
		waitForInputAndRunWithoutPrompt(new Runnable() {
			@Override
			public void run() {
				println(message, color);
			}
		});
	}

	public void asyncWaitAndError(final String message) {
		waitForInputAndRunWithoutPrompt(new Runnable() {
			@Override
			public void run() {
				error(message);
			}
		});
	}

	private void waitForInputAndRun(final Runnable r) {
		new org.jdesktop.swingworker.SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				waitForInputEnd();
				return true;
			}

			@Override
			protected void done() {
				assertEDT();
				r.run();
			}
		}.run();
	}

	private void waitForInputAndRunWithoutPrompt(final Runnable r) {
		waitForInputAndRun(new Runnable() {
			@Override
			public void run() {
				removePromptWithTyping();
				r.run();
				printPromptAndSuspendedCommand();
			}
		});
	}

	private void waitForInputEnd() {
		getOut().flush();
		getErr().flush();
		try {
			while (inPipe.available() > 0 || errPipe.available() > 0)
				Thread.sleep(300);
		} catch (InterruptedException ignored) {
		} catch (IOException ignored) {
		}
	}

	private void waitForInPipeEnd() {
		getOut().flush();
		try {
			while (inPipe.available() > 0)
				Thread.sleep(300);
		} catch (InterruptedException ignored) {
		} catch (IOException ignored) {
		}
	}

	private void waitForErrPipeEnd() {
		getErr().flush();
		try {
			while (errPipe.available() > 0)
				Thread.sleep(300);
		} catch (InterruptedException ignored) {
		} catch (IOException ignored) {
		}
	}

	private int getTextLength() {
		return text.getDocument().getLength();
	}

	private String getLastTextLine() {
		return getLastLine(text.getText());
	}

	public void terminate() {
		try {
			inPipe.close();
			errPipe.close();
		} catch (IOException ignored) {
		}
	}

	public JTextPane getTextPane() {
		return text;
	}

	public String getText() {
		return text.getText();
	}

	public void clear() {
		String cmd = "";
		final String lastLine = getLastTextLine();
		if (lastLine.startsWith(prompt))
			cmd = lastLine.substring(prompt.length() + 1);

		text.setText("");
		if (!busy) {
			suspendedCommand = cmd;
			asyncWaitAndPrintPrompt();
		}
	}

	public void cut() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				text.cut();
			}
		});
	}

	public void copy() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				text.copy();
			}
		});
	}

	public void paste() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				text.paste();
			}
		});
	}

	public boolean search(final String textToFind, final BSFConsoleSearchOptions searchOptions) {
		assertEDT();
		final boolean fromCursor = searchOptions == null || searchOptions.searchFromCursor;
		final int offset = fromCursor ? text.getCaretPosition() : 0;
		final String _text = getText();

		if (textToFind == null) {
			return false;
		}

		final int position = _text.indexOf(textToFind, offset);

		if (position == -1) {
			//JOptionPane.showMessageDialog(null, "Not Found");
			return false;
		}

		text.setCaretPosition(position);
		text.moveCaretPosition(position + textToFind.length());//scroll
		return true;
	}

	public void focus() {
		text.requestFocusInWindow();
	}

	private void assertEDT() {
		if (constructed && !SwingUtilities.isEventDispatchThread())
			throw new RuntimeException("This method must only be called from EDT!");
	}
	// new Thread(new Runnable(){public void run(){for (int i=0;i<50;i++) {Thread.sleep(100);System.out.println(i);}}}).start();
}