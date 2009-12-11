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

/**
 * Abstract language interpreter interface.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $
 */
public interface Interpreter {
	void start();

	void exec(String line);

	void stop();

	void addInputListener(InputListener inputListener);

	void removeInputListener(InputListener inputListener);
}
