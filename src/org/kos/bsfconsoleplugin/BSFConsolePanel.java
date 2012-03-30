package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Console tab.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFConsolePanel extends JPanel {
	private final Console console;
	private final RecentCommandsPanel recentCommandsPanel;
	private final Interpreter interpreter;
	private final String languageName;
	private final JSplitPane splitPane;

	public BSFConsolePanel(final BSFConsolePlugin plugin, final Language language, final boolean start) throws InterpreterInstantiationException {
		console = new Console(plugin);
		languageName = language.getLanguageName();
		interpreter = language.createInterpreter(plugin, console);
		if (start) interpreter.start();
		recentCommandsPanel = new RecentCommandsPanel(console, plugin);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, console, recentCommandsPanel);
		splitPane.setOneTouchExpandable(true);

		if (plugin != null) { //true in tests
			final int preferredDividerLocation = plugin.getConfig().getPreferredRecentCommandsDividerLocation(language);
			if (preferredDividerLocation == -1) {
				final int panelWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
				splitPane.setDividerLocation(panelWidth);
				splitPane.setLastDividerLocation(3 * panelWidth >> 2);

			} else
				splitPane.setDividerLocation(preferredDividerLocation);

			splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
					new PropertyChangeListener() {
						@Override
						public void propertyChange(final PropertyChangeEvent evt) {
							plugin.getConfig().setPreferredRecentCommandsDividerLocation(language,
									splitPane.getDividerLocation());
						}
					});
		} else
			splitPane.setDividerLocation(1000);

		interpreter.addInputListener(new InputListener() {
			@Override
			public void commandInputted(final String command) {
				recentCommandsPanel.appendCommandToHistory(command);
			}
		});

		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	@NotNull
	public String getLanguageName() {
		return languageName;
	}

	public void close() {
		getInterpreter().stop();
		getConsole().terminate();

//		plugin.getConfig().setPreferredRecentCommandsDividerLocation(language,
//				splitPane.getDividerLocation());
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	public Console getConsole() {
		return console;
	}

	public RecentCommandsPanel getRecentCommandsPanel() {
		return recentCommandsPanel;
	}

	public void focus() {
		console.focus();
	}
}