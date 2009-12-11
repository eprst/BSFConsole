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

import org.kos.bsfconsoleplugin.languages.KeyWordCompletionManager;

import java.util.ArrayList;

/**
 * A test for {@link KeyWordCompletionManager}.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class KeyWordCompletionManagerTest extends AbstractCompletionManagerTest {
	public KeyWordCompletionManagerTest(final String s) throws Exception {
		super(s);
	}

	public void testKeyWordCompletionManager() {
		final ArrayList<String> keyWords = new ArrayList<String>(3);
		keyWords.add("abc");
		keyWords.add("abd");
		keyWords.add("dbd");

		completionManager = new KeyWordCompletionManager(keyWords);

		checkCompletion("", new String[]{"abc", "abd", "dbd"});
		checkCompletion("a", new String[]{"bc", "bd"});
		checkCompletion("ab", new String[]{"c", "d"});
		checkCompletion("d", new String[]{"bd"});
		checkCompletion("c", new String[0]);
	}
}