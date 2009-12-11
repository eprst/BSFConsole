/*
 * EnterpriseWizard
 *
 * Copyright (C) 2007 EnterpriseWizard, Inc. All Rights Reserved.
 *
 * $Id$
 * Created by Konstantin Sobolev (kos@supportwizard.com) on 09.11.2008$
 * Last modification $Date$
 */

package org.kos.bsfconsoleplugin;

public class ClassLoaderInfo {
	public final ClassLoader classLoader;
	public final String classPath;

	public ClassLoaderInfo(final ClassLoader classLoader, final String classPath) {
		this.classLoader = classLoader;
		this.classPath = classPath;
	}
}
