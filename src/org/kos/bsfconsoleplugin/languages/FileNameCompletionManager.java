/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.languages;

import java.io.File;
import java.util.ArrayList;

/**
 * File name completion manager.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class FileNameCompletionManager implements CompletionManager {
	private boolean acceptDirectories;

	public FileNameCompletionManager(final boolean acceptDirectories) {
		this.acceptDirectories = acceptDirectories;
	}

	/**
	 * Finds all possible completions if the given line.
	 *
	 * @param line current line of text.
	 *
	 * @return array of all possible completions.
	 */
	@Override
	public String[] complete(final String line) {
		final String lastWord = CompletionManagerUtils.getLastWord(line);
		final String fileSep = System.getProperty("file.separator");

		final File dir;
		final String fileNamePrefix;
		if (lastWord == null || lastWord.length() == 0) {
			dir = new File(".");
			fileNamePrefix = null;
		} else {
			final int i = getLastPathElementIndex(lastWord, fileSep);
			if (i == 0) {
				dir = new File(".");
				fileNamePrefix = null;
			} else {
				dir = new File(lastWord.substring(0, i));
				fileNamePrefix = lastWord.substring(i);
			}
		}

		if (!dir.exists() || !dir.canRead())
			return new String[0];

		final File[] files = dir.listFiles();
		if (files.length == 0)
			return new String[0];

		final ArrayList<String> res = new ArrayList<String>(files.length);
		final int lastWordLength = lastWord == null ? 0 : lastWord.length();
		for (final File file : files) {
			if (!isAcceptableFile(file, fileNamePrefix))
				continue;

			final String suffix = file.getAbsolutePath().substring(lastWordLength);
			if (!acceptDirectories && file.isDirectory())
				res.add(suffix + fileSep);
			else
				res.add(suffix);
		}
		return res.toArray(new String[res.size()]);
	}

	private int getLastPathElementIndex(final String path, final String fileSep) {
		int i = path.length();
		if (fileSep.length() == 1) {
			final char pathSepChar = fileSep.charAt(0);
			while (i > 1 && path.charAt(i - 1) != pathSepChar)
				i--;
		} else {
			while (i > 1 && !path.substring(i - 1).startsWith(fileSep))
				i--;
		}
		return i;
	}

	private boolean isAcceptableFile(final File file, final String fileNamePrefix) {
		if (fileNamePrefix != null && !file.getName().startsWith(fileNamePrefix))
			return false;
		return file.canRead();
	}
}