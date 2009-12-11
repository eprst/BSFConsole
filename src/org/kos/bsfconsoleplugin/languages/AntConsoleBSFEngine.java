/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   k_o_s@mail.ru                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.languages;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.CodeBuffer;
import org.kos.bsfconsoleplugin.BSFConsolePlugin;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

/**
 * Simple BSF Engine for <a href="http://www.sdv.fr/pages/casa/html/sat.en.html">Ant Console</a>.
 *
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class AntConsoleBSFEngine implements BSFEngine {
	private static final Logger LOG = Logger.getInstance("org.kos.bsfconsoleplugin.languages.AntConsoleBSFEngine");
	private AntConsole console;

	/**
	 * This is used by an application to invoke an anonymous function. An
	 * anonymous function is a multi-line script which when evaluated will
	 * produce a value. These are separated from expressions and scripts
	 * because the prior are spsed to be good 'ol expressions and scripts
	 * are not value returning. We allow anonymous functions to have parameters
	 * as well for completeness.
	 *
	 * @param source     (context info) the source of this expression
	 *                   (e.g., filename)
	 * @param lineNo     (context info) the line number in source for expr
	 * @param columnNo   (context info) the column number in source for expr
	 * @param funcBody   the multi-line, value returning script to evaluate
	 * @param paramNames the names of the parameters above assumes
	 * @param arguments  values of the above parameters
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while doin' it.
	 */
	@Nullable
	@Override
	public Object apply(final String source, final int lineNo, final int columnNo, final Object funcBody,
						final Vector paramNames, final Vector arguments) throws BSFException {
		run(funcBody.toString());
		return null;
	}

	@Override
	public void iexec(final String source, final int lineNo, final int columnNo, final Object script) throws BSFException {
		exec(source, lineNo, columnNo, script);
	}

	/**
	 * This is used by an application to call into the scripting engine
	 * to make a function/method call. The "object" argument is the object
	 * whose method is to be called, if that applies. For non-OO languages,
	 * this is typically ignored and should be given as null. For pretend-OO
	 * languages such as VB, this would be the (String) name of the object.
	 * The arguments are given in the args array.
	 *
	 * @param object object on which to make the call
	 * @param name   name of the method / procedure to call
	 * @param args   the arguments to be given to the procedure
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while eval'ing a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Override
	public Object call(final Object object, final String name, final Object[] args) throws BSFException {
		throw new BSFException("call is not supported");
	}

	/**
	 * This is used by an application to compile an anonymous function. See
	 * comments in apply for more hdetails.
	 *
	 * @param source     (context info) the source of this expression
	 *                   (e.g., filename)
	 * @param lineNo     (context info) the line number in source for expr
	 * @param columnNo   (context info) the column number in source for expr
	 * @param funcBody   the multi-line, value returning script to evaluate
	 * @param paramNames the names of the parameters above assumes
	 * @param arguments  values of the above parameters
	 * @param cb         the CodeBuffer to compile into
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while doin' it.
	 */
	@Override
	public void compileApply(final String source, final int lineNo, final int columnNo, final Object funcBody,
							 final Vector paramNames, final Vector arguments, final CodeBuffer cb) throws BSFException {
		throw new BSFException("compileApply is not supported");
	}

	/**
	 * This is used by an application to compile a value-returning expression.
	 * The expr may be string or some other type, depending on the language.
	 * The generated code is dumped into the <tt>CodeBuffer</tt>.
	 *
	 * @param source   (context info) the source of this expression
	 *                 (e.g., filename)
	 * @param lineNo   (context info) the line number in source for expr
	 * @param columnNo (context info) the column number in source for expr
	 * @param expr     the expression to compile
	 * @param cb       the CodeBuffer to compile into
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while compiling a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Override
	public void compileExpr(final String source, final int lineNo, final int columnNo, final Object expr,
							final CodeBuffer cb) throws BSFException {
		throw new BSFException("compileExpr is not supported");
	}

	/**
	 * This is used by an application to compile some script. The
	 * script may be string or some other type, depending on the
	 * language. The generated code is dumped into the <tt>CodeBuffer</tt>.
	 *
	 * @param source   (context info) the source of this script
	 *                 (e.g., filename)
	 * @param lineNo   (context info) the line number in source for script
	 * @param columnNo (context info) the column number in source for script
	 * @param script   the script to compile
	 * @param cb       the CodeBuffer to compile into
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while compiling a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Override
	public void compileScript(final String source, final int lineNo, final int columnNo, final Object script,
							  final CodeBuffer cb) throws BSFException {
		throw new BSFException("compileScript is not supported");
	}

	/**
	 * Declare a bean after the engine has been started. Declared beans
	 * are beans that are named and which the engine must make available
	 * to the scripts it runs in the most first class way possible.
	 *
	 * @param bean the bean to declare
	 *
	 * @throws org.apache.bsf.BSFException if the engine cannot do this operation
	 */
	@Override
	public void declareBean(final BSFDeclaredBean bean) throws BSFException {
		throw new BSFException("declareBean is not supported");
	}

	/**
	 * This is used by an application to evaluate an expression. The
	 * expression may be string or some other type, depending on the
	 * language. (For example, for BML it'll be an org.w3c.dom.Element
	 * object.)
	 *
	 * @param source   (context info) the source of this expression
	 *                 (e.g., filename)
	 * @param lineNo   (context info) the line number in source for expr
	 * @param columnNo (context info) the column number in source for expr
	 * @param expr     the expression to evaluate
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while eval'ing a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Nullable
	@Override
	public Object eval(final String source, final int lineNo, final int columnNo, final Object expr) throws BSFException {
		run(expr.toString());
		return null;
	}

	/**
	 * This is used by an application to execute some script. The
	 * expression may be string or some other type, depending on the
	 * language. Returns nothing but if something goes wrong it excepts
	 * (of course).
	 *
	 * @param source   (context info) the source of this expression
	 *                 (e.g., filename)
	 * @param lineNo   (context info) the line number in source for expr
	 * @param columnNo (context info) the column number in source for expr
	 * @param script   the script to execute
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while exec'ing a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Override
	public void exec(final String source, final int lineNo, final int columnNo, final Object script) throws BSFException {
		run(script.toString());
	}

	/**
	 * This method is used to initialize the engine right after construction.
	 * This method will be called before any calls to eval or call. At this
	 * time the engine should capture the current values of interesting
	 * properties from the manager. In the future, any changes to those
	 * will be mirrored to me by the manager via a property change event.
	 *
	 * @param mgr           The BSFManager that's hosting this engine.
	 * @param lang          Language string which this engine is handling.
	 * @param declaredBeans Vector of BSFDeclaredObject containing beans
	 *                      that should be declared into the language runtime at init
	 *                      time as best as possible.
	 *
	 * @throws org.apache.bsf.BSFException if anything goes wrong while init'ing a
	 *                                     BSFException is thrown. The reason indicates the problem.
	 */
	@Override
	public void initialize(final BSFManager mgr, final String lang, final Vector declaredBeans) throws BSFException {
//		LOG.info("0");
//		//final ClassLoader classLoader = createAntClassLoader(mgr.getClassLoader());
//		final ClassLoader classLoader = mgr.getClassLoader();
//		if (classLoader != null) {
//			try {
//				LOG.info("1");
//				final Class consoleClass = classLoader.loadClass("org.kos.bsfconsoleplugin.languages.AntConsole");
//				console = (AntConsole) consoleClass.newInstance();
//				LOG.info("2");
////				console = (AntConsole) BSFConsolePlugin.antConsoleClass.newInstance();
//				classLoader.loadClass("org.apache.tools.ant.launch.Locator");
//				//consoleClass.getClassLoader().loadClass("org.apache.tools.ant.launch.Locator");
//				LOG.info("3");
//				console.getClass().getClassLoader().loadClass("org.apache.tools.ant.launch.Locator");
//				LOG.info("Loaded ant console via BSF class loader(!!!)");
//			} catch (ClassNotFoundException e) {
//				LOG.error("Internal error: " + e, e);
//				console = new AntConsole();
//			} catch (IllegalAccessException e) {
//				LOG.error("Internal error: " + e, e);
//				console = new AntConsole();
//			} catch (InstantiationException e) {
//				LOG.error("Internal error: " + e, e);
//				console = new AntConsole();
//			}
//		} else
			console = new AntConsole();
		console.start();
	}

	@Nullable
	@SuppressWarnings({"UnusedDeclaration"})
	private ClassLoader createAntClassLoader(final ClassLoader parent) {
		final String libPath = PathManager.getLibPath();
		LOG.info("lib path=" + libPath);
		if (libPath != null) {
			final File antLibs = new File(libPath, "ant");
			if (antLibs.exists()) {
				final File[] jarFiles = antLibs.listFiles(
						new FilenameFilter() {
							@Override
							public boolean accept(final File dir, final String name) {
								return name.endsWith(".jar");
							}
						}
				);

				final URL[] urls = new URL[jarFiles.length + 1];
				try {
					for (int i = 0; i < jarFiles.length; i++) {
						final File jarFile = jarFiles[i];
						urls[i] = jarFile.toURI().toURL();
						LOG.info("added " + urls[i]);
					}

					final StringBuffer sb = new StringBuffer(PathManager.getPluginsPath());
					sb.append(File.separatorChar).append(BSFConsolePlugin.PLUGIN_NAME);
					sb.append(File.separatorChar).append("lib");
					//sb.append(File.separatorChar).append("sat.jar");
					sb.append(File.separatorChar).append("BSFConsole-aurora.jar");
					final File satFile = new File(sb.toString());
					LOG.assertTrue(satFile.exists());
					urls[jarFiles.length]=satFile.toURI().toURL();
					LOG.info("added " + urls[jarFiles.length]);

//					if (parent != null)
//						return new URLClassLoader(urls, parent);
//					else
						return new URLClassLoader(urls);
				} catch (MalformedURLException e) {
					LOG.error("Unexpected exception: " + e, e);
				}
			} else
				LOG.info("antLibs doesn't exist");
		}

		LOG.info("returning NULL");
		return null;
	}

	/**
	 * Graceful termination
	 */
	@Override
	public void terminate() {
		try {
			run("exit");
		} catch (BSFException e) {
			//log.error("Unexpected exception: " + e, e);
		}
	}

	/**
	 * Undeclare a previously declared bean.
	 *
	 * @param bean the bean to undeclare
	 *
	 * @throws org.apache.bsf.BSFException if the engine cannot do this operation
	 */
	@Override
	public void undeclareBean(final BSFDeclaredBean bean) throws BSFException {
		throw new BSFException("undeclareBean is not supported");
	}

	/**
	 * This method gets called when a bound property is changed.
	 *
	 * @param evt A PropertyChangeEvent object describing the event source
	 *            and the property that has changed.
	 */

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
	}

	public AntConsole getConsole() {
		return console;
	}

	private void run(final String commands) throws BSFException {
		final BufferedReader br = new BufferedReader(new StringReader(commands));
		String command;
		try {
			while ((command = br.readLine()) != null)
				console.runCommand(command.trim());
			br.close();
		} catch (IOException e) {
			throw new BSFException(e.getMessage());
		}
	}
}