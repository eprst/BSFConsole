package org.kos.bsfconsoleplugin;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;

/**
 * BSF evaluator.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
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
