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
 * Various completion manager utils.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
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