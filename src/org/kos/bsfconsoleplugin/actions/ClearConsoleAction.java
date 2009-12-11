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
import org.kos.bsfconsoleplugin.ConsoleTab;
import org.kos.bsfconsoleplugin.BSFConsolePanel;

/**
 * Clear transcript action.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class ClearConsoleAction extends CurrentConsoleAction {
	/**
	 * Implement this method to provide your action handler.
	 *
	 * @param e Carries information on the invocation place
	 */
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsoleTab consoleTab = getConsolePanel(e);
		if (consoleTab != null) {
			final BSFConsolePanel currentConsolePanel = consoleTab.getConsolePanel();
			if (currentConsolePanel != null) {
				currentConsolePanel.getConsole().clear();
				currentConsolePanel.focus();
			}
		}
	}
}