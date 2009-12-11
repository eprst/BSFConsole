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

/**
 * Abstract evaluator.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $
 */
public interface Evaluator {
	Object eval (String expr) throws EvaluationException;
}
