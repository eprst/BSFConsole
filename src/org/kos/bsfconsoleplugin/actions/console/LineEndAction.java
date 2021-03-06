package org.kos.bsfconsoleplugin.actions.console;

import org.jetbrains.annotations.NotNull;
import org.kos.bsfconsoleplugin.Console;

/**
 * Move caret to the line end action.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class LineEndAction extends ConsoleAction {
	@Override
	void run(@NotNull final Console console) {
		console.moveCaretToCmdEnd();
	}

}