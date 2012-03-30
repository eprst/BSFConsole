package org.kos.bsfconsoleplugin;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * JSR-223 evaluator.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
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
