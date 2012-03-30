package org.kos.bsfconsoleplugin.languages;

/**
 * Language completion manager.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public interface CompletionManager {
	/**
	 * Finds all possible completions if the given line. By completion we understand
	 * line suffixes. For example, if &quot;abc&quot; can be &quot;abcde&quot; and &quot;abcfg&quot;, then
	 * this method must return &quot;de&quot; and &quot;fg&quot;.
	 *
	 * @param line current line of text.
	 *
	 * @return array of all possible completions.
	 */
	String[] complete(final String line);

	//Java doesn't allow static abstract methods
	//static CompletionManager getInstance(String languageName, BSFEngine engine);
}