/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package org.kos.bsfconsoleplugin.languages;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import net.cafebabe.sat.Constants;
import net.cafebabe.sat.ant.Command;
import net.cafebabe.sat.ant.ConsoleLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.jetbrains.annotations.Nullable;

/**
 * This task runs a console that reads and execute user commands. <p/>
 * Slightly modified to be used in BSFConsole IDEA plugin.
 * 
 * @author <a href="mailto:casa@sweetohm.net">Michel CASABIANCA</a>
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 */
public class AntConsole
		implements Runnable {

	/**
	 * The thread to run the main loop
	 */
	//private Thread thread;
	/**
	 * The projet build file
	 */
	private File buildFile;
	/**
	 * The project to build
	 */
	private Project project;
	/**
	 * The standard stream
	 */
	private PrintStream out;
	/**
	 * The error stream
	 */
	private PrintStream err;
	/**
	 * The input stream
	 */
	private InputStream in;
	/**
	 * Timer outpout
	 */
	private boolean timer;
	/**
	 * Blank characters
	 */
	private static final String BLANK = " \t\n\r";
	/**
	 * The welcome message
	 */
	private static final String MESSAGE =
			"Ant Console " + Constants.VERSION + " (C) Michel Casabianca 2003\n" +
			"type \"help\" to get help on console commands";
	/**
	 * The help screen
	 */
	private static final String HELP =
			"Usage: antc [-help] [-version] [-timer] [-file file] [-find file]\n" +
			"-help    Print this help screen\n" +
			"-version Print the version\n" +
			"-timer   Print build times\n" +
			"-file    To set the build file\n" +
			"-find    To search for the build file";
	/**
	 * The inline help for commands
	 */
	private static final String COMMANDHELP =
			"Commands you can run in the console are the following:\n" +
			"  help          To display this help screen\n" +
			"  exit          To quit the console\n" +
			"  desc          To describe the loaded project\n" +
			"  load file     To load the build file\n" +
			"  find file     To find the project file\n" +
			"  reload        To reload the current project\n" +
			"  timer on/off  To set timer on/off\n" +
			"  target foo    To run the target foo\n" +
			"  targetname    To run the target (can't be a console command)\n" +
			"  <empty>       To repeat the last command";

	private String lastCommand = "help";

	/**
	 * Method main.
	 * 
	 * @param args Command line parameters as a String[]
	 */
	public static void main(final String[] args) {
		final AntConsole console = new AntConsole(args);
		console.start();
	}

	public AntConsole() {
		this(new String[0]);
	}

	/**
	 * Constructor: parse the command line arguments.
	 * 
	 * @param args Command line parameters as a String[]
	 */
	public AntConsole(final String[] args) {
		out = System.out;
		err = System.err;
		in = System.in;
		out.println(MESSAGE);
		parseArgs(args);
		if (buildFile == null) buildFile = new File("build.xml");
		project = loadProject(buildFile);
	}

	public Project getProject() {
		return project;
	}

	/**
	 * The main loop of the console.
	 */
	@Override
	public void run() {
		String command;
		while (!"exit".equalsIgnoreCase(lastCommand)) {
			out.flush();
			err.flush();
//            out.print("ant % ");
//            out.flush();
			try {
				command = readCommand();
			} catch (IOException e) {
				err.println("ERROR: " + e.getMessage());
				continue;
			}
			runCommand(command);
		}
	}

	public void runCommand(String command) {
		try {
			final long start = System.currentTimeMillis();
			if (command.length() == 0) command = lastCommand;
			lastCommand = command;
			if ("help".equals(command)) {
				out.println(COMMANDHELP);
			} else if ("exit".equals(command)) {
//                    System.exit(0);
//					break;
			} else if ("desc".equals(command)) {
				Command.desc(project, out);
			} else if (command.startsWith("load")) {
				runLoad(command);
			} else if (command.startsWith("find")) {
				runFind(command);
			} else if ("reload".equals(command)) {
				runReload();
			} else if (command.startsWith("timer")) {
				runTimer(command);
			} else if (command.startsWith("target")) {
				Command.targets(project, getArgs(command), out);
			} else {
				command = "target " + command;
				Command.targets(project, getArgs(command), out);
			}
			final long end = System.currentTimeMillis();
			if (timer) out.println(end - start + " ms");
		} catch (Exception e) {
			err.println("ERROR: " + e.getMessage());
		}
	}

	private void runTimer(final String command) {
		final Vector<String> args = getArgs(command);
		if (args.size() != 1) {
			out.println("You must specify on or off");
		} else {
			final boolean state = "on".equals(args.firstElement());
			timer = state;
			out.println("Timer is " + (state ? "on" : "off"));
		}
	}

	private void runReload() {
		final Project newProject = loadProject(buildFile);
		if (newProject != null) project = newProject;
	}

	private void runFind(final String command) {
		final Vector<String> args = getArgs(command);
		if (args.size() != 1)
			out.println("You must specify on project file to find");
		else {
			final File newBuildFile = findBuildFile(args.firstElement());
			if (newBuildFile != null) {
				final Project newProject = loadProject(newBuildFile);
				if (newProject != null) {
					buildFile = newBuildFile;
					project = newProject;
				}
			}
		}
	}

	private void runLoad(final String command) {
		final Vector<String> args = getArgs(command);
		if (args.size() != 1)
			out.println("You must specify on project file to load");
		else {
			final File newBuildFile = new File(args.firstElement());
			final Project newProject = loadProject(newBuildFile);
			if (newProject != null) {
				buildFile = newBuildFile;
				project = newProject;
			}
		}
	}

	private String readCommand() throws IOException {
		final BufferedReader reader = new
				BufferedReader(new InputStreamReader(in));
		return reader.readLine().trim();
	}

	/**
	 * Start the console.
	 */
	public void start() {
//		thread = new Thread(this);
//		thread.start();
	}

	/**
	 * Parse command line arguments.
	 * 
	 * @param args The command line arguments as a String[]
	 */
	private void parseArgs(final String[] args) {
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			if ("-help".equals(arg)) {
				System.out.println(HELP);
				System.exit(0);
			} else if ("-version".equals(arg)) {
				System.out.println(Constants.VERSION);
				System.exit(0);
			} else if ("-file".equals(arg)) {
				try {
					buildFile = new
							File(args[i + 1].replace('/', File.separatorChar));
					i++;
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a buildfile when " +
							"using the -file argument";
					System.out.println(msg);
					System.exit(-1);
				}
			} else if ("-find".equals(arg)) {
				try {
					buildFile = findBuildFile(args[i + 1]);
					i++;
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a buildfile when " +
							"using the -find argument";
					System.out.println(msg);
					System.exit(-1);
				}
			} else if ("-timer".equals(arg)) {
				timer = true;
			} else {
				System.out.println("Unknown argument " + arg);
				System.exit(-1);
			}
		}
	}

	/**
	 * Return the build file by searching recursively in the file system.
	 * 
	 * @param file The file name of the build file to search as a String
	 * @return The found build file as a File
	 */
	@Nullable
	private File findBuildFile(final String file) {
		out.println("Searching for " + file + " ...");
		File dir = new File(System.getProperty("user.dir"));
		buildFile = new File(dir, file);
		while (!buildFile.exists()) {
			dir = dir.getParentFile();
			if (dir == null) {
				out.println("Build file not found");
				return null;
			}
			buildFile = new File(dir, file);
		}
		return buildFile;
	}

	/**
	 * Return the project.
	 * 
	 * @param buildFile The project's build file as a File
	 * @return The built Project
	 */
	@Nullable
	private Project loadProject(final File buildFile) {
		if (!buildFile.exists()) {
			out.println(buildFile + " not found");
			return null;
		} else if (buildFile.isDirectory()) {
			out.println(buildFile + " is a directory");
			return null;
		} else
			out.println("Loading project " + buildFile);
		final Project project = new Project();
		final ConsoleLogger logger = new ConsoleLogger(out, err, Project.MSG_INFO);
		project.addBuildListener(logger);
		project.init();
		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		ProjectHelper.configureProject(project, buildFile);
		return project;
	}

	/**
	 * Return the arguments for a gien command as a Vector.
	 * 
	 * @param command The command as a String
	 * @return The arguments as a Vector
	 */
	private Vector<String> getArgs(final String command) {
		final StringTokenizer st = new StringTokenizer(command, BLANK);
		st.nextToken();
		final Vector<String> args = new Vector<String>();
		while (st.hasMoreTokens()) args.addElement(st.nextToken());
		return args;
	}
}

