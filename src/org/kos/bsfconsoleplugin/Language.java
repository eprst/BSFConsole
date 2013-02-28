package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Language information.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public interface Language extends Comparable {
	boolean isAvailable();
	@Nullable
	String whyNotAvailable();
	@NotNull
	String getLanguageName();
	@NotNull
	String getLabel();
	@NotNull
	Interpreter createInterpreter(@Nullable final BSFConsolePlugin plugin, final Console console) throws InterpreterInstantiationException;
}
