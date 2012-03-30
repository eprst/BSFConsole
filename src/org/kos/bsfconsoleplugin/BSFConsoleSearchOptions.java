package org.kos.bsfconsoleplugin;

import java.util.LinkedList;
import java.util.List;

/**
 * Options for 'search transcript' action.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFConsoleSearchOptions {
	public List<String> recentSearches = new LinkedList<String>();

	public boolean searchFromCursor;

	public BSFConsoleSearchOptions() {
	}

	public BSFConsoleSearchOptions(final BSFConsoleSearchOptions other) {
		recentSearches.clear();
		recentSearches.addAll(other.recentSearches);
		searchFromCursor = other.searchFromCursor;
	}

	public void addRecentSearch(final String s) {
		recentSearches.add(s);
		removeTail();
	}

	private void removeTail() {
		while (recentSearches.size() > 8)
			recentSearches.remove(0);
	}

//	@Nullable
//	public String getTextToFind() {
//		return recentSearches.size() == 0 ? null : recentSearches.get(recentSearches.size() - 1);
//	}
}