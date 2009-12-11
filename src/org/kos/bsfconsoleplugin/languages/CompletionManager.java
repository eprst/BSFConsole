/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   k_o_s@mail.ru                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.languages;

/**
 * Language completion manager.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
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