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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Completion manager factory.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class CompletionManagerFactory {
	private static final CompletionManagerFactory ourInstance = new CompletionManagerFactory();

	private static final String[] COMPLETION_MANAGER_CLASS_NAMES = new String[]{
		"org.kos.bsfconsoleplugin.languages.AntConsoleCompletionManager",
		"org.kos.bsfconsoleplugin.languages.BeanShellCompletionManager"
	};

	private static final Collection<Class<CompletionManager>> COMPLETION_MANAGER_CLASSES;

	static {
		final ArrayList<Class<CompletionManager>> lst = new ArrayList<Class<CompletionManager>>(COMPLETION_MANAGER_CLASS_NAMES.length);
		for (final String completionManagerClassName : COMPLETION_MANAGER_CLASS_NAMES) {
			try {
				//noinspection unchecked
				final Class<CompletionManager> cls = (Class<CompletionManager>) Class.forName(completionManagerClassName);
				cls.getMethod("getInstance", String.class, Object.class);
				lst.add(cls);
			} catch (Throwable e) {
				assert true;
			}
		}
		COMPLETION_MANAGER_CLASSES = lst;
	}

	private CompletionManagerFactory() {
	}

	public CompletionManager getCompletionManager(final String languageName, final Object engine) {
		for (final Class<CompletionManager> completionManagerClass : COMPLETION_MANAGER_CLASSES) {
			try {
				final Method method = completionManagerClass.getMethod("getInstance", String.class, Object.class);
				final CompletionManager completionManager = (CompletionManager) method.invoke(null, languageName, engine);
				if (completionManager != null)
					return completionManager;
			} catch (Exception e) {
				assert true;
			}
		}

		return null;
	}

	public static CompletionManagerFactory getInstance() {
		return ourInstance;
	}
}