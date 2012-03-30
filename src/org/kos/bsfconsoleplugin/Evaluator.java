package org.kos.bsfconsoleplugin;

/**
 * Abstract evaluator.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public interface Evaluator {
	Object eval (String expr) throws EvaluationException;
}
