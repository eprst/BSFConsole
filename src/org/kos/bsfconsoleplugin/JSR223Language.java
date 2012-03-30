package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.NotNull;

import javax.script.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * JSR-223 language.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class JSR223Language implements Language {
	private Boolean available;
	private ScriptEngineFactory engineFactory;
	private String whyNotAvailable;

	public JSR223Language(final ScriptEngineFactory engineFactory) {
		this.engineFactory = engineFactory;
	}

	@Override
	public boolean isAvailable() {
		if (available != null) return available;
		whyNotAvailable = null;
		final String languageName = engineFactory.getLanguageName();
		if ("jaxp".equals(languageName)) {
			whyNotAvailable = "not supported";
			return false;
		}

		final PrintStream out = System.out;
		final PrintStream err = System.err;

		final PrintStream nullStream = new PrintStream(new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
			}
		});
		final PrintWriter nullWriter = new PrintWriter(nullStream);

		System.setOut(nullStream);
		System.setErr(nullStream);

		try {
			final ScriptContext sc = new SimpleScriptContext();
			sc.setErrorWriter(nullWriter);
			sc.setWriter(nullWriter);

			final ScriptEngine eng = engineFactory.getScriptEngine();
			eng.eval("1", sc);
		} catch (ScriptException ignored) {
		} catch (Throwable e) {
			whyNotAvailable = e.toString();
			available = Boolean.FALSE;
			return false;
		} finally {
			System.setOut(out);
			System.setErr(err);
		}
		available = Boolean.TRUE;
		return true;
	}

	@Override
	public String whyNotAvailable() {
		return whyNotAvailable;
	}

	@NotNull
	@Override
	public String getLanguageName() {
		return engineFactory.getLanguageName();
	}

	@NotNull
	@Override
	public String getLabel() {
		return "[JSR-223] " + getLanguageName();
	}

	@NotNull
	@Override
	public Interpreter createInterpreter(final BSFConsolePlugin plugin, final Console console) throws InterpreterInstantiationException {
		return new JSR223Interpreter(plugin, console, engineFactory);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final JSR223Language that = (JSR223Language) o;

		return getLanguageName().equals(that.getLanguageName());
	}

	@Override
	public int hashCode() {
		return getLanguageName().hashCode();
	}

	@Override
	public int compareTo(final Object o) {
		final Language language = (Language) o;

		//available languages first
		if (!isAvailable() && language.isAvailable())
			return 1;
		if (isAvailable() && !language.isAvailable())
			return -1;

		return getLanguageName().compareTo(language.getLanguageName());
	}

	@Override
	public String toString() {
		if (isAvailable())
			return getLabel();
		else
			return getLabel() + " [not available]";
	}
}
