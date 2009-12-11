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
import org.kos.bsfconsoleplugin.ConsoleTab;
import org.kos.bsfconsoleplugin.Console;
import org.kos.bsfconsoleplugin.BSFConsolePanel;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract action on current console.
 *
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
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