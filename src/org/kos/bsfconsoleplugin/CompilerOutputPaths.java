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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;

import java.io.File;

/**
 * Special class that allows to get compiler output path.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public final class CompilerOutputPaths {
	private static final Logger LOG = Logger.getInstance("org.kos.bsfconsoleplugin.CompilerOutputPaths");
	private static final String NULL_PATH = "";

	public static String getModuleOutputPath(final Module module, final boolean testsOutputPath) {
		final CompilerModuleExtension cm = CompilerModuleExtension.getInstance(module);
		if (cm == null) return NULL_PATH;
		final VirtualFile vf;
		if (testsOutputPath)
			vf = cm.getCompilerOutputPathForTests();
		else
			vf = cm.getCompilerOutputPath();
		if (vf == null)
			return NULL_PATH;
		final VirtualFileSystem fileSystem = vf.getFileSystem();
		if (fileSystem instanceof LocalFileSystem)
			return new File(vf.getPath().replace('/', File.separatorChar)).getAbsolutePath();
		else {
			LOG.error("Output path doesn't belong to local filesystem: " + vf.getUrl());
			return NULL_PATH;
		}
	}
}