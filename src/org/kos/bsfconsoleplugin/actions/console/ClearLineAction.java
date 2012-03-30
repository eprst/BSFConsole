package org.kos.bsfconsoleplugin.actions.console;

import org.jetbrains.annotations.NotNull;
import org.kos.bsfconsoleplugin.Console;

/**
 * Clear line action
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class ClearLineAction extends ConsoleAction {
	@Override
	void run(@NotNull final Console console) {
		console.clearLine();
	}

}