package org.kos.bsfconsoleplugin.tests;

import junit.framework.TestCase;
import org.kos.bsfconsoleplugin.Console;

import java.lang.reflect.Method;

/**
 * A testsuite for {@link org.kos.bsfconsoleplugin.Console}.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFConsoleTest extends TestCase {
	protected Console console;

	private Method getLastLine;

	public BSFConsoleTest(final String s) {
		super(s);
	}

	@Override
	protected void setUp() throws Exception {
		console = new Console(null);
	}

	@Override
	protected void tearDown() throws Exception {
		console.terminate();
	}

	public void testGetLastLine() throws Exception {
		assertEquals("", callGetLastTextLine(""));
		assertEquals("", callGetLastTextLine("\n"));
		assertEquals("", callGetLastTextLine("\r"));
		assertEquals("", callGetLastTextLine("\n\r"));
		assertEquals("", callGetLastTextLine("\r\n"));
		assertEquals("", callGetLastTextLine("abc\n"));
		assertEquals("", callGetLastTextLine("abc\r"));
		assertEquals("", callGetLastTextLine("abc\n\r"));
		assertEquals("abc", callGetLastTextLine("abc"));
		assertEquals("abc", callGetLastTextLine("\nabc"));
		assertEquals("abc", callGetLastTextLine("\rabc"));
		assertEquals("abc", callGetLastTextLine("\n\rabc"));
		assertEquals("bc", callGetLastTextLine("a\nbc"));
		assertEquals("bc", callGetLastTextLine("a\rbc"));
		assertEquals("bc", callGetLastTextLine("a\r\nbc"));
		assertEquals("bc", callGetLastTextLine("a\n\rbc"));
		assertEquals("c", callGetLastTextLine("ab\nc"));
		assertEquals("c", callGetLastTextLine("ab\rc"));
		assertEquals("c", callGetLastTextLine("ab\r\nc"));
		assertEquals("c", callGetLastTextLine("ab\n\rc"));
	}

	private String callGetLastTextLine(final String line) throws Exception {
		if (getLastLine == null) {
			getLastLine = console.getClass().getDeclaredMethod("getLastLine", String.class);
			getLastLine.setAccessible(true);
		}
		return (String) getLastLine.invoke(console, line);
	}
}