package org.kos.bsfconsoleplugin.tests;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test suite for the whole project.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
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