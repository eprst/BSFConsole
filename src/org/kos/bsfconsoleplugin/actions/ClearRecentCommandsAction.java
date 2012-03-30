package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.kos.bsfconsoleplugin.BSFConsolePanel;
import org.kos.bsfconsoleplugin.ConsoleTab;

/**
 * Clears recent commands list.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class ClearRecentCommandsAction extends CurrentConsoleAction {
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsoleTab tab = getConsolePanel(e);
		if (tab == null) return;
		final BSFConsolePanel currentPanel = tab.getConsolePanel();
		if (currentPanel != null)
			currentPanel.getRecentCommandsPanel().clear();
	}

	@Override
	public void update(final AnActionEvent e) {
		super.update(e);
		e.getPresentation().setText("Clear");

		final ConsoleTab tab = getConsolePanel(e);
		if (tab == null) return;
		final BSFConsolePanel currentPanel = tab.getConsolePanel();
		if (currentPanel != null && currentPanel.getRecentCommandsPanel().isEmpty())
			e.getPresentation().setEnabled(false);
	}
}