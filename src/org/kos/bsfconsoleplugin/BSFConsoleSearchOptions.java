/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * Options for 'search transcript' action.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
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