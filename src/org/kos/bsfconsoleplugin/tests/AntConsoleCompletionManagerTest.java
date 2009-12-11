/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   k_o_s@mail.ru                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.tests;

import org.kos.bsfconsoleplugin.languages.AntConsole;
import org.kos.bsfconsoleplugin.languages.AntConsoleCompletionManager;

/**
 * A test for {@link AntConsoleCompletionManager}.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class AntConsoleCompletionManagerTest extends AbstractCompletionManagerTest {

	public AntConsoleCompletionManagerTest(final String s) throws Exception {
		super(s);
	}

	public void testKeyWordCompletionManager() {
		completionManager = new AntConsoleCompletionManager(new AntConsole(new String[0]));

		checkCompletion("he", new String[]{"lp"});
		checkCompletion("time", new String[]{"r"});
		checkCompletion("timer ", new String[]{"on", "off"});
		checkCompletion("timer o", new String[]{"n", "ff"});
	}
}