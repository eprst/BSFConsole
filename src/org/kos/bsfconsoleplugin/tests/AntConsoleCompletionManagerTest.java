package org.kos.bsfconsoleplugin.tests;

import org.kos.bsfconsoleplugin.languages.AntConsole;
import org.kos.bsfconsoleplugin.languages.AntConsoleCompletionManager;

/**
 * A test for {@link AntConsoleCompletionManager}.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
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