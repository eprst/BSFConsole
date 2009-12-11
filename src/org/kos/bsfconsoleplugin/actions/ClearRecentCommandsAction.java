/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   k_o_s@mail.ru                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.kos.bsfconsoleplugin.BSFConsolePanel;
import org.kos.bsfconsoleplugin.ConsoleTab;

/**
 * Clears recent commands list.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
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