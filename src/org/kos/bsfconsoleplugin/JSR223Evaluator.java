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

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * JSR-223 evaluator.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public class JSR223Evaluator implements Evaluator {
	private final ScriptEngine engine;

	public JSR223Evaluator(final ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public Object eval(final String expr) throws EvaluationException {
		try {
			return engine.eval(expr);
		} catch (ScriptException e) {
			throw new EvaluationException(e);
		}
	}
}
