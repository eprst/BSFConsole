package org.kos.bsfconsoleplugin.languages;

/**
 * Various completion manager utils.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class CompletionManagerUtils {
	/**
	 * Gets last word from line. Words are considered to be separated by spaces
	 * (characters that qualify as <code>Character.isWhitespace(...)</code>.
	 *
	 * @param line line of text.
	 * @return last word from <code>line</code>. If it doesn't contain spaces, then <code>line</code>
	 * is returned.
	 */
	public static String getLastWord(final String line) {
		int i;
		for (i=line.length()-1; i >= 0 && !Character.isWhitespace(line.charAt(i)); i--);
		return line.substring(i + 1);
	}
}