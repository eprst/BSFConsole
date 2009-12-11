/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/
package org.kos.bsfconsoleplugin.tests;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test suite for the whole project.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class BSFConsolePluginTestSuite extends TestSuite {
	public static Test suite() {
		final TestSuite res = new TestSuite("BSFConsole plugin test suite");
		res.addTestSuite(AbstractCompletionManagerTest.class);
		res.addTestSuite(CompletionManagerUtilsTest.class);
		res.addTestSuite(KeyWordCompletionManagerTest.class);
		res.addTestSuite(AntConsoleCompletionManagerTest.class);
		res.addTestSuite(BeanShellCompletionManagerTest.class);
		res.addTestSuite(BSFConsoleTest.class);

		return res;
	}
}