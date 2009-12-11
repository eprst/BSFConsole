/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import org.kos.bsfconsoleplugin.BSFConsolePlugin;
import org.kos.bsfconsoleplugin.BSFConsolePanel;
import org.kos.bsfconsoleplugin.ConsoleTab;
import org.kos.bsfconsoleplugin.Console;

/**
 * Sends current (selected) editor text to BSF console.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class SendToConsoleAction extends EditorAction {
	private static final String EXISTS_SELECTION_DESCRIPTION = "Send selected text to the current BSF console";
	private static final String NO_SELECTION_DESCRIPTION = "Send entire text to the current BSF console";

	public SendToConsoleAction() {
		super(new SendToConsoleActionHandler());
	}

	@Override
	public void update(final Editor editor, final Presentation presentation, final DataContext dataContext) {
		final String text = presentation.getText();
		final String pluginName = BSFConsolePlugin.PLUGIN_NAME;

		if (text.startsWith(pluginName))
			presentation.setText(text.substring(pluginName.length() + 1));

		presentation.setDescription(editor.getSelectionModel().hasSelection() ?
		                            EXISTS_SELECTION_DESCRIPTION :
		                            NO_SELECTION_DESCRIPTION);

		final Project project = DataKeys.PROJECT.getData(dataContext);
		if (project == null) {
			presentation.setEnabled(false);
			return;
		}

		final BSFConsolePlugin consolePlugin = project.getComponent(BSFConsolePlugin.class);
		if (consolePlugin == null) {
			presentation.setEnabled(false);
			return;
		}

		presentation.setEnabled(consolePlugin.getConsolesManager().getCurrentConsole() != null);
	}

	private static class SendToConsoleActionHandler extends EditorActionHandler {
		public SendToConsoleActionHandler() {
		}

		@Override
		public void execute(final Editor editor, final DataContext dataContext) {
			final SelectionModel selectionModel = editor.getSelectionModel();

			final Project project = DataKeys.PROJECT.getData(dataContext);
			if (project == null)
				return;

			final BSFConsolePlugin consolePlugin = project.getComponent(BSFConsolePlugin.class);
			if (consolePlugin == null)
				return;

			final String text = selectionModel.hasSelection() ?
			                    selectionModel.getSelectedText() :
			                    editor.getDocument().getText();

			consolePlugin.getConsolesManager().getToolWindow().show(new Runnable() {
				@Override
				public void run() {
					final ConsoleTab consoleTab = consolePlugin.getConsolesManager().getCurrentConsoleTab();
					if (consoleTab == null) return;
					final BSFConsolePanel panel = consoleTab.getConsolePanel();
					if (panel == null) return;
					final Console console = panel.getConsole();

					if (text != null && text.contains(System.getProperty("line.separator")))
						console.asyncWaitAndPrintln(text);
					else
						console.asyncWaitAndPrintln("\u00abdata from editor\u00bb");
					panel.getInterpreter().exec(text);
					console.asyncWaitAndPrintPrompt();
				}
			});
		}
	}
}