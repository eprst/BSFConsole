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

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;

/**
 * BSF evaluator.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public class BSFEvaluator implements Evaluator {
	private final BSFEngine engine;

	public BSFEvaluator(final BSFEngine engine) {
		this.engine = engine;
	}

	@Override
	public Object eval(final String expr) throws EvaluationException {
		try {
			return engine.eval("",1,1,expr);
		} catch (BSFException e) {
			throw new EvaluationException(e);
		}
	}
}
