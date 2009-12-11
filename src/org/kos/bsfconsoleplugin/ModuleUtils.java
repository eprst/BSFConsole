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
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 * Module utilities.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class ModuleUtils {
	private static final Logger LOG = Logger.getInstance("org.kos.bsfconsoleplugin.ModuleUtils");

	/**
	 * Finds module by name. If there is no such module,
	 * then <code>null</code> is returned. If <code>moduleName</code> is an
	 * empty string, then the first existing module will be returned.
	 *
	 * @param project project
	 * @param moduleName module name
	 *
	 * @return module named <code>moduleName</code>
	 */
	@Nullable
	public static Module findModuleByName(final Project project, final String moduleName) {
		if (moduleName == null || moduleName.length() == 0)
			return null;

		final ModuleManager mgr = ModuleManager.getInstance(project);
		final Module[] modules = mgr.getModules();

		for (final Module module : modules)
			if (module.getName().equals(moduleName) /*|| moduleName.length() == 0*/)
				return module;

		LOG.info("module '" + moduleName + "' not found");
		return null;
	}
}