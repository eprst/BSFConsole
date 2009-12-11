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

import org.kos.bsfconsoleplugin.languages.CompletionManagerUtils;
import junit.framework.TestCase;

/**
 * A test for {@link CompletionManagerUtils}.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class CompletionManagerUtilsTest extends TestCase {
	public CompletionManagerUtilsTest(final String s) {
		super(s);
	}

	public void testGetLastWord() throws Exception {
		assertEquals("three", CompletionManagerUtils.getLastWord("one two three"));
		assertEquals("three", CompletionManagerUtils.getLastWord("one two	three"));
		assertEquals("three", CompletionManagerUtils.getLastWord("one	two three"));
		assertEquals("two_three", CompletionManagerUtils.getLastWord("one two_three"));
		assertEquals("one_two_three", CompletionManagerUtils.getLastWord("one_two_three"));
		assertEquals("", CompletionManagerUtils.getLastWord(""));
		assertEquals("", CompletionManagerUtils.getLastWord("a "));
	}
}