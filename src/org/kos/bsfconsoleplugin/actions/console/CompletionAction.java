/*
 * EnterpriseWizard
 *
 * Copyright (C) 2007 EnterpriseWizard, Inc. All Rights Reserved.
 *
 * $Id$
 * Created by Konstantin Sobolev (kos@supportwizard.com) on 18.11.2008$
 * Last modification $Date$
 */

package org.kos.bsfconsoleplugin.actions.console;

import org.jetbrains.annotations.NotNull;
import org.kos.bsfconsoleplugin.Console;

/**
 * Command completion action.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public class CompletionAction extends ConsoleAction {
	@Override
	void run(@NotNull final Console console) {
		console.completeInput();
	}

}