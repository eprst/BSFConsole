package org.kos.bsfconsoleplugin.tests;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.kos.bsfconsoleplugin.languages.CompletionManager;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Stub for completion manager test.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class AbstractCompletionManagerTest extends TestCase {
	protected CompletionManager completionManager;

	public AbstractCompletionManagerTest(final String s) {
		super(s);
	}

	public void testCheckCompletion() throws Exception { //self-test
		completionManager = new CompletionManager() {
			@Override
			public String[] complete(final String line) {
				return line.split(" ");
			}
		};

		checkCompletion("a", new String[]{"a"});
		checkCompletion("a b", new String[]{"a", "b"});
		checkCompletion("a b", new String[]{"b", "a"});

		try {
			checkCompletion("a", new String[0]);
			fail();
		} catch (AssertionFailedError e) {}

		try {
			checkCompletion("a", new String[]{"b"});
			fail();
		} catch (AssertionFailedError e) {}

		try {
			checkCompletion("a b", new String[]{"a", "b", "c"});
			fail();
		} catch (AssertionFailedError e) {}
	}

	protected void checkCompletion(final String line, final String[] expected) {
		final String[] res = completionManager.complete(line);
		assertEquals(expected.length, res.length);
		assertEquals(
				new HashSet<String>(Arrays.asList(expected)),
				new HashSet<String>(Arrays.asList(res))
		);
	}
}