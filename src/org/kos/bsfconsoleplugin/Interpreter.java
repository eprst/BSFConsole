package org.kos.bsfconsoleplugin;

/**
 * Abstract language interpreter interface.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public interface Interpreter {
	void start();

	void exec(String line);

	void stop();

	void addInputListener(InputListener inputListener);

	void removeInputListener(InputListener inputListener);
}
