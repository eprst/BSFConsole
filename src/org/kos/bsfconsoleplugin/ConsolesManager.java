package org.kos.bsfconsoleplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Manages all open consoles.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class ConsolesManager {
	private final BSFConsolePlugin plugin;
	private ToolWindow toolWindow;
	private ContentManager contentManager;

	public ConsolesManager(final BSFConsolePlugin plugin) {
		this.plugin = plugin;
	}

	public BSFConsolePlugin getPlugin() {
		return plugin;
	}

	public void initToolWindow() {
		final Project project = plugin.getProject();
		final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		toolWindow = toolWindowManager.registerToolWindow(BSFConsolePlugin.PLUGIN_NAME, true, ToolWindowAnchor.BOTTOM);
		contentManager = toolWindow.getContentManager();
		addEmptyTab();

		contentManager.addContentManagerListener(new ContentManagerAdapter() {
			@Override
			public void contentRemoved(final ContentManagerEvent event) {
				if (contentManager.getContentCount() == 0) {
					addEmptyTab();
					toolWindow.hide(null);
				}
			}
		});
	}

	private void addEmptyTab() {
		registerTab(new ConsoleTab(plugin));
	}

	private void registerTab(@NotNull final ConsoleTab tab) {
		final Content content = contentManager.getFactory().createContent(tab, tab.getLanguageName(), true);
		content.setDisposer(tab);
		final BSFConsolePanel consolePanel = tab.getConsolePanel();
		if (consolePanel != null)
			content.setPreferredFocusableComponent(consolePanel.getConsole().getTextPane());
		contentManager.addContent(content);
		contentManager.setSelectedContent(content, true, true);
	}

	public void unregisterToolWindow() {
		final Project project = plugin.getProject();
		final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
		toolWindowManager.unregisterToolWindow(BSFConsolePlugin.PLUGIN_NAME);
	}

	public ToolWindow getToolWindow() {
		return toolWindow;
	}

	public void closeAllConsoles() {
		contentManager.removeAllContents(true);
	}

	public void closeCurrentConsole() {
		final ConsoleTab consoleTab = getCurrentConsoleTab();
		if (consoleTab == null || consoleTab.isEmpty()) return;

		final Content content = contentManager.getSelectedContent();
		assert content != null : "a0";
		assert content.getComponent() == consoleTab : "a1";
		contentManager.removeContent(content, true);
	}

	@Nullable
	public ConsoleTab getCurrentConsoleTab() {
		final Content content = contentManager.getSelectedContent();
		if (content == null) return null;
		return (ConsoleTab) content.getComponent();
	}

	@Nullable
	public Console getCurrentConsole() {
		final ConsoleTab tab = getCurrentConsoleTab();
		if (tab == null) return null;
		final BSFConsolePanel panel = tab.getConsolePanel();
		if (panel == null) return null;
		return panel.getConsole();
	}

	public void newStartupScriptConsoleTab(@NotNull final StartupScript startupScript) {
		newConsoleTab(new ConsoleTabInitializer() {
			@Override
			public void initConsoleTab(@NotNull final ConsoleTab tab) {
				tab.runStartupScript(startupScript);
			}
		});
	}

	public void newLanguageConsoleTab(@NotNull final Language language) {
		newConsoleTab(new ConsoleTabInitializer() {
			@Override
			public void initConsoleTab(@NotNull final ConsoleTab tab) {
				tab.newConsole(language, true);
			}
		});
	}

	private void newConsoleTab(@NotNull final ConsoleTabInitializer tabInitializer) {
		final Content content = contentManager.getSelectedContent();
		if (content == null) return;
		final ConsoleTab tab = (ConsoleTab) content.getComponent();
		if (tab.isEmpty()) {
			tabInitializer.initConsoleTab(tab);
			final String languageName = tab.getLanguageName();
			content.setDisplayName(languageName);
			content.setTabName(languageName);
			final BSFConsolePanel consolePanel = tab.getConsolePanel();
			assert consolePanel != null : "a2";
			content.setPreferredFocusableComponent(consolePanel.getConsole().getTextPane());
			contentManager.setSelectedContent(content, true, true);
		} else {
			final ConsoleTab newTab = new ConsoleTab(plugin);
			tabInitializer.initConsoleTab(newTab);
			registerTab(newTab);
		}

	}

	private interface ConsoleTabInitializer {
		void initConsoleTab(@NotNull ConsoleTab tab);
	}
}
