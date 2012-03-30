package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.kos.bsfconsoleplugin.ConsoleTab;
import org.kos.bsfconsoleplugin.Console;
import org.kos.bsfconsoleplugin.BSFConsolePanel;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract action on current console.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public abstract class CurrentConsoleAction extends BSFConsoleAction {
	@Override
	public void update(final AnActionEvent e) {
		super.update(e);

		final ConsoleTab consoleTab = getConsolePanel(e);
		if (consoleTab == null)
			e.getPresentation().setEnabled(false);
		else
			e.getPresentation().setEnabled(!consoleTab.isEmpty());
	}

	@Nullable
	public Console getCurrentConsole(final AnActionEvent e) {
		final ConsoleTab consoleTab = getConsolePanel(e);
		if (consoleTab != null) {
			final BSFConsolePanel panel = consoleTab.getConsolePanel();
			if (panel != null)
				return panel.getConsole();
		}
		return null;
	}
}