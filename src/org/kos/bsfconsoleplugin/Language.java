/*
 * EnterpriseWizard
 *
 * Copyright (C) 2007 EnterpriseWizard, Inc. All Rights Reserved.
 *
 * $Id$
 * Created by Konstantin Sobolev (kos@supportwizard.com) on 08.11.2008$
 * Last modification $Date$
 */

package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Language information.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public interface Language extends Comparable {
	boolean isAvailable();
	@Nullable
	String whyNotAvailable();
	@NotNull
	String getLanguageName();
	@NotNull
	String getLabel();
	@NotNull
	Interpreter createInterpreter(final BSFConsolePlugin plugin, final Console console) throws InterpreterInstantiationException;
}
