package org.kos.bsfconsoleplugin;

import org.apache.bsf.BSFException;
import org.jetbrains.annotations.NotNull;

/**
 * Description of the language supported by BSF.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFLanguage implements Language {
	public String languageName;
	public String engineClassName;

	private Boolean available;
	private String whyNotAvailable;

	public BSFLanguage() {
	}

	public BSFLanguage(final String languageName, final String engineClassName) {
		this.engineClassName = engineClassName;
		this.languageName = languageName;
	}

	@Override
	public boolean isAvailable() {
		if (available != null)
			return available;

		whyNotAvailable = null;
		try {
			Class.forName(engineClassName);
		} catch (Throwable e) {
			whyNotAvailable = e.toString();
			available = Boolean.FALSE;
			return false;
		}

		//causes an error pop-up due to BSFEngine logging
//		if (plugin != null) {
//			try {
//				final Triple<BSFManager, ClassLoader, String> triple = plugin.loadWithScriptClassLoader(BSFManager.class);
//				final BSFManager bsfManager = triple.getFst();
//				bsfManager.setClassLoader(triple.getSnd());
//				if (triple.getTrd() != null)
//					bsfManager.setClassPath(triple.getTrd());
//				bsfManager.loadScriptingEngine(languageName);
//			} catch (Throwable e) {
//				System.out.println("caught = " + e);
//				available = Boolean.FALSE;
//				return false;
//			}
//		}
		available = Boolean.TRUE;
		return true;
	}

	@Override
	public String whyNotAvailable() {
		return whyNotAvailable;
	}

	@Override
	public String toString() {
		if (isAvailable())
			return getLabel();
		else
			return getLabel() + " [not available]";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final BSFLanguage bsfLanguage = (BSFLanguage) o;

		//noinspection NonFinalFieldReferenceInEquals
		return !(languageName != null ? !languageName.equals(bsfLanguage.languageName) : bsfLanguage.languageName != null);
	}

	@Override
	public int hashCode() {
		return languageName != null ? languageName.hashCode() : 0;
	}

	@NotNull
	@Override
	public String getLanguageName() {
		return languageName;
	}

	@NotNull
	@Override
	public String getLabel() {
		return "[BSF] "+languageName;
	}

	@NotNull
	@Override
	public Interpreter createInterpreter(final BSFConsolePlugin plugin, final Console console) throws InterpreterInstantiationException {
		try {
			return new BSFInterpreter(plugin, console, languageName);
		} catch (BSFException e) {
			throw new InterpreterInstantiationException(e);
		} catch (NoClassDefFoundError e) {
			throw new InterpreterInstantiationException(e);
		}
	}

	@Override
	public int compareTo(final Object o) {
		final Language language = (Language) o;

		//available languages first
		if (!isAvailable() && language.isAvailable())
			return 1;
		if (isAvailable() && !language.isAvailable())
			return -1;

		return languageName.compareTo(language.getLanguageName());
	}
}