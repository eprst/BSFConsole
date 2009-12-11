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

import bsh.Interpreter;
import org.kos.bsfconsoleplugin.languages.BeanShellCompletionManager;

import java.util.Arrays;

/**
 * A test for {@link BeanShellCompletionManager}.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class BeanShellCompletionManagerTest extends AbstractCompletionManagerTest {
	@SuppressWarnings({"FieldCanBeLocal"})
	private Interpreter interpreter;

	public BeanShellCompletionManagerTest(final String s) throws Exception {
		super(s);

		interpreter = new Interpreter();
		completionManager = new BeanShellCompletionManager(interpreter);
	}

	public void testBeanShellCompletionManager() {
		checkCompletion("Intege", "r");
		checkCompletion("new Intege", "r");
		checkCompletionExists("new Integer(1).toS", "tring(");
		checkCompletionExists("System.out.println(new Integer(1).toS", "tring(");
		checkCompletionExists("\"1\" + new Integer(1).toS", "tring(");
		checkCompletionExists("1 + new Integer(1).intVal", "ue(");
		checkCompletionExists("print", "Banner");

		checkCompletionDoesntExist("new Integer(1).to","OctalString(");
		checkCompletionExists("Integer.to","OctalString(");
	}

	private void checkCompletion(final String line, final String expected) {
		final String[] res = completionManager.complete(line);
		assertEquals(1, res.length);
		assertEquals(expected, res[0]);
	}

	private void checkCompletionExists(final String line, final String completion) {
		final String[] res = completionManager.complete(line);
		assertTrue("Expected to get " + completion + " among possible variants", Arrays.asList(res).contains(completion));
	}

	private void checkCompletionDoesntExist(final String line, final String completion) {
		final String[] res = completionManager.complete(line);
		assertTrue("Expected NOT to get " + completion + " among possible variants", !Arrays.asList(res).contains(completion));
	}
}