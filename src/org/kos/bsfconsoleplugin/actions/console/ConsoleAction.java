package org.kos.bsfconsoleplugin.actions.console;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.kos.bsfconsoleplugin.Console;
import org.kos.bsfconsoleplugin.ConsolesManager;
import org.kos.bsfconsoleplugin.actions.BSFConsoleAction;

/**
 * Console editor action.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public abstract class ConsoleAction extends BSFConsoleAction {
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsolesManager consolesManager = getConsolesManager(e);
		if (consolesManager == null) return;
		final Console console = consolesManager.getCurrentConsole();
		if (console != null)
			run(console);
	}

	@Override
	public void update(final AnActionEvent e) {
		final ConsolesManager consolesManager = getConsolesManager(e);
		if (consolesManager != null) {
			final Console currentConsole = consolesManager.getCurrentConsole();
			if (currentConsole != null) {
				final boolean hasFocus = currentConsole.getTextPane().hasFocus();
				e.getPresentation().setEnabled(hasFocus);
				return;
			}
		}
		e.getPresentation().setEnabled(false);
	}

	abstract void run(@NotNull final Console console);
}
