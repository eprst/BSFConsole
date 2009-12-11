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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.kos.bsfconsoleplugin.*;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Action for searching thru console.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class SearchTranscriptAction extends CurrentConsoleAction {
	private String lastSearch;

	public void registerShortcuts(final JComponent scope) {
		final Shortcut[] shortcuts = new Shortcut[]{
				new KeyboardShortcut(KeyStroke.getKeyStroke("control F"), null),
				new KeyboardShortcut(KeyStroke.getKeyStroke("F3"), null)
		};
		registerCustomShortcutSet(new CustomShortcutSet(shortcuts), scope);
	}

	/**
	 * Implement this method to provide your action handler.
	 *
	 * @param e Carries information on the invocation place
	 */
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final Console console = getCurrentConsole(e);
		if (console == null)
			return;

		final BSFConsolePlugin plugin = getPlugin(e);
		assert plugin != null : "a0";

		final BSFConsoleSearchOptions searchOptions = plugin.getSearchOptions();
		if (!(e.getInputEvent() instanceof KeyEvent) || (e.getInputEvent().getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			final SearchTranscriptDialog searchTranscriptDialog = new SearchTranscriptDialog(searchOptions, plugin.getProject(), false);
			searchTranscriptDialog.show();

			if (searchTranscriptDialog.getExitCode() != DialogWrapper.OK_EXIT_CODE)
				return;

			final String text = searchTranscriptDialog.getText();
			if (!console.search(text, searchOptions))
				Messages.showInfoMessage(plugin.getProject(), "Not Found", BSFConsolePlugin.PLUGIN_NAME);

			lastSearch = text;
		} else if (lastSearch != null) {
			final boolean origFromCursor = searchOptions.searchFromCursor;
			searchOptions.searchFromCursor = true;
			if (!console.search(lastSearch, searchOptions))
				Messages.showInfoMessage(plugin.getProject(), "Not Found", BSFConsolePlugin.PLUGIN_NAME);
			searchOptions.searchFromCursor = origFromCursor;
		}
	}
}