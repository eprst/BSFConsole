package org.kos.bsfconsoleplugin;


import org.jetbrains.annotations.Nullable;


/**
 * Abstract language interpreter interface.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public interface Interpreter {
	void start();

	void exec(@Nullable String line);

	void stop();

	void addInputListener(InputListener inputListener);

	void removeInputListener(InputListener inputListener);
}
