package org.kos.bsfconsoleplugin;

import org.kos.bsfconsoleplugin.languages.CompletionManager;
import org.kos.bsfconsoleplugin.languages.CompletionManagerFactory;

import javax.script.*;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * JSR 223 languages interpreter.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class JSR223Interpreter extends AbstractInterpreter {
	private ScriptEngine engine;
	private ScriptContext ctx;

	public JSR223Interpreter(final BSFConsolePlugin plugin,
	                         final Console console,
	                         final ScriptEngineFactory engineFactory) {
		super(plugin, console);

		engine = engineFactory.getScriptEngine();
		final String languageName = engineFactory.getLanguageName();
		console.setPrompt(languageName + "%");

		ctx=new SimpleScriptContext();
		ctx.setWriter(new PrintWriter(console.getOut()));
		ctx.setErrorWriter(new PrintWriter(console.getErr()));

		System.setOut(console.getOut());
		System.setErr(console.getErr());

		final CompletionManager completionManager = CompletionManagerFactory.getInstance().
				getCompletionManager(languageName, engine);
		
		if (completionManager != null)
			console.setCompletionManager(completionManager);

		createInputThread(languageName);
	}

	@Override
	void terminateEngine() {
	}

	@Override
	public synchronized void exec(final String line) {
		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		final PrintStream out = System.out;
		final PrintStream err = System.err;

		boolean changedClassLoader = false;
		final ClassLoader interpreterClassLoader = getInterpreterClassLoader();
		if (currentClassLoader != interpreterClassLoader) {
			Thread.currentThread().setContextClassLoader(interpreterClassLoader);
			changedClassLoader = true;
		}

		ctx.setErrorWriter(new PrintWriter(console.getErr()));
		ctx.setWriter(new PrintWriter(console.getOut()));

		System.setOut(console.getOut());
		System.setErr(console.getErr());
		console.setWaitFeedback(true);
		try {
			_eval(line);
		} catch (ScriptException e) {
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
			if (changedClassLoader)
				Thread.currentThread().setContextClassLoader(currentClassLoader);
			if (plugin == null || plugin.getConfig().isRestoreSystemStreams()) {//null may occur in tests
				System.setOut(out);
				System.setErr(err);
			}
		}
	}


	private ClassLoader getInterpreterClassLoader() {
		//return engine.getClass().getClassLoader();
		return plugin.getClassLoaderManager().getModuleClassLoaderInfo().classLoader;
	}

	private void _eval(final String line) throws ScriptException {
		final Object res = engine.eval(line, ctx);
		if (res != null)
			println(res.toString(), Console.OUT_COLOR);
	}
}