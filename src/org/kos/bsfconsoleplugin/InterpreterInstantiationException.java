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
 * Exception while creating interpreter instance.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public class InterpreterInstantiationException extends Exception {
	public InterpreterInstantiationException(final Throwable cause) {
		super(cause);
	}
}
