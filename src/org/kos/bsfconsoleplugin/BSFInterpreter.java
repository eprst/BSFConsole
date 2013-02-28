package org.kos.bsfconsoleplugin;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.kos.bsfconsoleplugin.languages.CompletionManager;
import org.kos.bsfconsoleplugin.languages.CompletionManagerFactory;

import java.io.PrintStream;
import java.util.HashSet;

/**
 * BSF languages interpreter.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFInterpreter extends AbstractInterpreter {
	private BSFEngine engine;

	private String source;
	//private String prompt;
	private boolean languageRequiresExec;

	private ClassLoader languageClassLoader;

	/**
	 * languages that doesn't support eval() calls for code blocks
	 */
	private static final HashSet<String> languagesRequiringExec = new HashSet<String>(1);

	static {
		languagesRequiringExec.add("jython");
		languagesRequiringExec.add("jpython");
		languagesRequiringExec.add("judoscript");
		languagesRequiringExec.add("netrexx");
	}

	public BSFInterpreter(final BSFConsolePlugin plugin,
	                      final Console console,
	                      final String language) throws BSFException {
		super(plugin, console);
		console.setPrompt(language + "%");
		source = language + "_console";
		languageRequiresExec = languagesRequiringExec.contains(language);

		final PrintStream out = System.out;
		final PrintStream err = System.err;

		System.setOut(console.getOut());
		System.setErr(console.getErr());

		try {
			//plugin may be null in tests
			final BSFManager bsfManager;
			if (plugin != null) {
				final Triple<BSFManager, ClassLoader, String> triple = plugin.getClassLoaderManager().loadWithScriptClassLoader(BSFManager.class);
				bsfManager = triple.getFst();
				assert bsfManager != null;
				bsfManager.setClassLoader(triple.getSnd());
				if (triple.getTrd() != null)
					bsfManager.setClassPath(triple.getTrd());
			} else {
				bsfManager = new BSFManager();
			}
			engine = bsfManager.loadScriptingEngine(language);
			languageClassLoader = bsfManager.getClassLoader();

			final CompletionManager completionManager = CompletionManagerFactory.getInstance().
					getCompletionManager(language, engine);
			if (completionManager != null)
				console.setCompletionManager(completionManager);
		} finally {
			System.setOut(out);
			System.setErr(err);
		}

		createInputThread(language);
	}


	@Override
	public synchronized void exec(final String line) {
		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		final PrintStream out = System.out;
		final PrintStream err = System.err;

		if (currentClassLoader != languageClassLoader)
			Thread.currentThread().setContextClassLoader(languageClassLoader);
		System.setOut(console.getOut());
		System.setErr(console.getErr());
		console.setWaitFeedback(true);
		try {
			_eval(line);
		} catch (BSFException e) {
			/*error(e.getMessage());
			e.printStackTrace(console.getErr());
			final Throwable targetException = e.getTargetException();
			if (targetException != null) {
				error("Caused by: " + targetException);
				targetException.printStackTrace(console.getErr());
			}*/
			printException(e);
		} catch (Throwable e) {
			/*error(e.getMessage());
			e.printStackTrace(console.getOut());*/
			printException(e);
		} finally {
			console.setWaitFeedback(false);
			if (currentClassLoader != languageClassLoader)
				Thread.currentThread().setContextClassLoader(currentClassLoader);
			if (plugin == null || plugin.getConfig().isRestoreSystemStreams()) {//null may occur in tests
				System.setOut(out);
				System.setErr(err);
			}
		}
	}

	private void _eval(final String line) throws BSFException {
		final int lineNumber = in.getLineNumber();
		if (languageRequiresExec)
			engine.exec(source, lineNumber, 0, line);
		else {
			final Object res = engine.eval(source, lineNumber, 0, line);
			if (res != null)
				println(res.toString(), Console.OUT_COLOR);
		}
	}

	@Override
	void terminateEngine() {
		engine.terminate();
	}
}