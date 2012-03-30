package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import org.kos.bsfconsoleplugin.ConsoleTab;
import org.kos.bsfconsoleplugin.BSFConsolePlugin;
import org.kos.bsfconsoleplugin.ConsolesManager;
import org.jetbrains.annotations.Nullable;

/**
 * Basic BSFConsole action
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public abstract class BSFConsoleAction extends AnAction{
	@Nullable
	public ConsoleTab getConsolePanel(final AnActionEvent event) {
		final ConsolesManager consolesManager = getConsolesManager(event);
		if (consolesManager == null) return null;
		return consolesManager.getCurrentConsoleTab();
	}

	@Nullable
	public ConsolesManager getConsolesManager(final AnActionEvent event) {
		final BSFConsolePlugin consolePlugin = getPlugin(event);
		if (consolePlugin == null) {
			return null;
		}

		return consolePlugin.getConsolesManager();
	}

	@Nullable
	public BSFConsolePlugin getPlugin(final AnActionEvent event) {
		final DataContext dataContext = event.getDataContext();
		final Project project = DataKeys.PROJECT.getData(dataContext);
		if (project == null) {
			return null;
		}

		return project.getComponent(BSFConsolePlugin.class);
	}
}