package org.kos.bsfconsoleplugin.actions.console;

import org.jetbrains.annotations.NotNull;
import org.kos.bsfconsoleplugin.Console;

/**
 * Command completion action.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class CompletionAction extends ConsoleAction {
	@Override
	void run(@NotNull final Console console) {
		console.completeInput();
	}

}