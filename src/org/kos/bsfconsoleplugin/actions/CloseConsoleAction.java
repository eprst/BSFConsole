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
import org.kos.bsfconsoleplugin.ConsolesManager;

/**
 * Action for closing console.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class CloseConsoleAction extends CurrentConsoleAction {
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final ConsolesManager consolesManager = getConsolesManager(e);
		if (consolesManager != null)
			consolesManager.closeCurrentConsole();
	}
}