package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.kos.bsfconsoleplugin.BSFConsolePlugin;
import org.kos.bsfconsoleplugin.ConsolesManager;
import org.kos.bsfconsoleplugin.Language;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Action for creating new console.
 *
 * @author <a href="konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class NewConsoleAction extends BSFConsoleAction {
	/**
	 * Implement this method to provide your action handler.
	 *
	 * @param e Carries information on the invocation place
	 */
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsolesManager consolesManager = getConsolesManager(e);
		if (consolesManager == null) return;

		final BSFConsolePlugin plugin = consolesManager.getPlugin();
		final LanguageChooserDialog languageChooserDialog =
				new LanguageChooserDialog(plugin.getProject(), false, plugin);
		languageChooserDialog.show();
		if (languageChooserDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
			final Language selectedLanguage = languageChooserDialog.getSelectedLanguage();
			if (selectedLanguage != null)
				consolesManager.newLanguageConsoleTab(selectedLanguage);
		}
	}

	@Override
	public void update(final AnActionEvent e) {
		super.update(e);
		final BSFConsolePlugin plugin = getPlugin(e);
		if (plugin != null)
			e.getPresentation().setEnabled(plugin.getLanguageManager().getAvailableLanguages().size() > 0);
	}

	private class LanguageChooserDialog extends DialogWrapper {
		private JList availableLanguagesList;

		public LanguageChooserDialog(final Project project, final boolean canBeParent, final BSFConsolePlugin plugin) {
			super(project, canBeParent);

			final List<Language> availableLanguages = plugin.getLanguageManager().getAvailableLanguages();
			final int n = availableLanguages.size();
			availableLanguagesList = new JBList(availableLanguages.toArray(new Object[n]));
			if (n > 0)
				availableLanguagesList.setSelectedIndex(0);

			availableLanguagesList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() != 2) return;
					final int index = availableLanguagesList.locationToIndex(e.getPoint());
					if (index == -1) return;
					availableLanguagesList.setSelectedIndex(index);
					doOKAction();
				}
			});

			setTitle("Choose Language");
			init();
		}

		/**
		 * Factory method. It creates panel with dialog options. Options panel is located at the
		 * center of the dialog's content pane. The implementation can return <code>null</code>
		 * value. In this case there will be no options panel.
		 */
		@Override
		protected JComponent createCenterPanel() {
			return new JBScrollPane(availableLanguagesList);
		}

		/**
		 * @return component which should be focused when the the dialog appears
		 *         on the screen.
		 */
		@Override
		public JComponent getPreferredFocusedComponent() {
			return availableLanguagesList;
		}

		public Language getSelectedLanguage() {
			return (Language) availableLanguagesList.getSelectedValue();
		}
	}
}