package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.kos.bsfconsoleplugin.ConsolesManager;

/**
 * Action for closing console.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class CloseConsoleAction extends CurrentConsoleAction {
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsolesManager consolesManager = getConsolesManager(e);
		if (consolesManager != null)
			consolesManager.closeCurrentConsole();
	}
}