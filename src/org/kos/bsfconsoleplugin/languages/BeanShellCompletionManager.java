/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.languages;

import bsh.ClassIdentifier;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.engine.BshScriptEngine;
import bsh.classpath.ClassManagerImpl;
import bsh.util.NameCompletionTable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * BeanShell completion manager.<p/>
 * By some reason it doesn't work as desired. Interpreter's namespace doesn't contain
 * any command until an attempt to run some predefined command. And even after that some
 * commands aren't load, e.g. 'cwd' won't be completed until you will run it once.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class BeanShellCompletionManager implements CompletionManager {
	private Interpreter interpreter;
	private NameCompletionTable nameCompletionTable;

	public BeanShellCompletionManager(final Interpreter interpreter) throws EvalError {
		this.interpreter = interpreter;

		interpreter.setOut(System.out);
		interpreter.setErr(System.err);

		interpreter.eval("printBanner()"); //inits namespace and prints nice banner ;)

		nameCompletionTable = new NameCompletionTable();

		final NameSpace nameSpace = interpreter.getNameSpace();
		nameCompletionTable.add(nameSpace);

		try {
			final ClassManagerImpl cml = (ClassManagerImpl) interpreter.getClassManager();
			nameCompletionTable.add(cml.getClassPath());
		} catch (Throwable e) {
			assert true;
		}
	}

	/**
	 * Finds all possible completions of the given line.
	 *
	 * @param line current line of text.
	 *
	 * @return array of all possible completions.
	 */
	@Override
	public String[] complete(String line) {//todo: refactor
		line = removeLineBreaks(line);

		final int currentExprStart = getCurrentExpressionStart(line);
		final int lineLength = line.length();
		if (currentExprStart > 0 && currentExprStart < lineLength)
			try {
				line = line.substring(currentExprStart);
			} catch (StringIndexOutOfBoundsException e) {
				//happens rarely by unknown reason
			}

		final int lastDotIndex = line.lastIndexOf('.');
		if (lastDotIndex != -1) {
			final String prefix;
			if (lastDotIndex + 1 < line.length())
				prefix = line.substring(lastDotIndex + 1);
			else
				prefix = "";
			final int prefixLength = prefix.length();

			final String objectName = line.substring(0, lastDotIndex);
			try {
				//final Object obj = interpreter.get(objectName);
				final Object obj = interpreter.eval(objectName);
				final Class<?> cls;
				if (obj != null) {
					boolean stat1k = false;
					if (obj instanceof ClassIdentifier) {
						cls = ((ClassIdentifier) obj).getTargetClass();
						stat1k = true;
					} else
						cls = obj.getClass();
					final Method[] methods = getClassMethods(cls);
					final Field[] fields = getClassFields(cls);
					final HashSet<String> res = new HashSet<String>(methods.length + fields.length);

					for (final Method method : methods) {
						if ((method.getModifiers() & Modifier.STATIC) != 0 != stat1k)
							continue;

						final String name = method.getName();
						if (name.startsWith(prefix))
							res.add(name.substring(prefixLength) + '(');
						//res.add(linePrefix + objectName + '.' + name + '(');
					}

					for (final Field field : fields) {
						if ((field.getModifiers() & Modifier.STATIC) != 0 != stat1k)
							continue;

						final String name = field.getName();
						if (name.startsWith(prefix))
							res.add(name.substring(prefixLength));
						//res.add(linePrefix + objectName + '.' + name);
					}

					return res.toArray(new String[res.size()]);
				}
			} catch (EvalError evalError) {
				assert true;
			}
		}

		//just complete last word
		final String lastWord = CompletionManagerUtils.getLastWord(line);
		final int lastWordLength = lastWord.length();
		final String[] nameCompletionsArray = nameCompletionTable.completeName(lastWord);
		final String[] res = new String[nameCompletionsArray.length];
		for (int i = 0; i < nameCompletionsArray.length; i++)
			res[i] = nameCompletionsArray[i].substring(lastWordLength);
		return res;
	}

	private Method[] getClassMethods(final Class<?> cls) {
		return cls.getMethods();
//		final Method[] declaredMethods = cls.getDeclaredMethods();
//		final ArrayList res = new ArrayList(Arrays.asList(declaredMethods));
//
//		Class parent = cls.getSuperclass();
//		while(parent!=null) {
//			res.addAll(Arrays.asList(parent.getDeclaredMethods()));
//			parent = parent.getSuperclass();
//		}
//
//		return (Method[]) res.toArray(new Method[res.size()]);
	}

	private Field[] getClassFields(final Class<?> cls) {
		return cls.getFields();
//		final Field[] fields = cls.getDeclaredFields();
//		return fields;
	}

	private String removeLineBreaks(final String line) {
		final int lineLength = line.length();
		final StringBuffer res = new StringBuffer(lineLength);

		for (int i = 0; i < lineLength; i++) {
			final char c = line.charAt(i);
			if (c != '\n' && c != '\r')
				res.append(c);
		}

		return res.toString();
	}

	private int getCurrentExpressionStart(final String line) {
		final List<String> openingBrackets = Arrays.asList("(", "[", "{");
		final List<String> closingBrackets = Arrays.asList(")", "]", "}");

		int nClosingBrackets = 0;
		boolean inQuote = false;
		//go left untill first unmatched opening bracket
		int res;
		for (res = line.length() - 2; res > -1; res--) {
			final char c = line.charAt(res);
			if (isQuote(line, res)) {
				inQuote = !inQuote;
				continue;
			}

			if (inQuote)
				continue;

			final String s = "" + c;
			if (openingBrackets.contains(s)) {
				nClosingBrackets--;
				if (nClosingBrackets < 0) {
					res++;
					break;
				}
			} else if (closingBrackets.contains(s))
				nClosingBrackets++;
//			else if (Character.isSpaceChar(c) && nClosingBrackets > 0)
//				continue;
			else if (!(c == '.' || Character.isJavaIdentifierPart(c) || Character.isSpaceChar(c)))
				return res + 1;
		}
		return res;
	}

	private boolean isQuote(final String line, final int index) {
		final char c = line.charAt(index);
		if (c == '\'' || c == '"')
			if (index == 0 || line.charAt(index - 1) != '\\')
				return true;
		return false;
	}

	/**
	 * Creates completion manager instance.
	 *
	 * @param languageName language name.
	 * @param engine       language engine.
	 *
	 * @return constructed manager or <code>null</code> if manager cannot be constructed.
	 */
	@Nullable
	public static CompletionManager getInstance(final String languageName, final Object engine) {
		final Class<?> engineClass = engine.getClass();
		if ("beanshell".equalsIgnoreCase(languageName)) {
			if (engineClass.getName().endsWith("BSFEngine")) {
				try {
					final Field interpreterField = engineClass.getDeclaredField("interpreter");
					interpreterField.setAccessible(true);
					final Interpreter _interpreter = (Interpreter) interpreterField.get(engine);
					return new BeanShellCompletionManager(_interpreter);
				} catch (Throwable ignored) {
				}
			} else if (engineClass.getName().endsWith("BshScriptEngine")) {
				try {
					((BshScriptEngine) engine).eval("1"); //init context
					final Method m = engineClass.getDeclaredMethod("getInterpreter");
					m.setAccessible(true);
					final Interpreter _interpreter = (Interpreter) m.invoke(engine);
					return new BeanShellCompletionManager(_interpreter);
				} catch (Throwable ignored) {
					ignored.printStackTrace();
				}
			}
		}
		return null;
	}
}