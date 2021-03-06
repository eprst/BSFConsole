package org.kos.bsfconsoleplugin.languages;

import java.util.*;

/**
 * Simple KeyWord completion manager.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class KeyWordCompletionManager implements CompletionManager {
	private Collection<String> keyWords;

	public KeyWordCompletionManager(final Collection<String> keyWords) {
		this.keyWords = new TreeSet<String>(keyWords);
	}

	/**
	 * Finds all possible completions if the given line.
	 *
	 * @param line     current line of text.
	 * @return array of all possible completions.
	 */
	@Override
	public String[] complete(final String line) {
		final ArrayList<String> res = new ArrayList<String>(keyWords.size()/10+5);
		final String lastWord = CompletionManagerUtils.getLastWord(line);
		final int lastWordLength = lastWord.length();

		for (final String keyWord : keyWords) {
			if (keyWord.startsWith(lastWord))
				res.add(keyWord.substring(lastWordLength));
		}

		return res.toArray(new String[res.size()]);
	}
}