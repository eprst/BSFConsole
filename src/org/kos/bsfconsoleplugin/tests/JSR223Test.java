package org.kos.bsfconsoleplugin.tests;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngineFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;

public class JSR223Test {
	public static void main(final String[] args) {
		try {
			final File root = new File(".");
			final File lib = new File(root, "lib");

			final ArrayList<URL> jars = new ArrayList<URL>();

			final File[] fs = lib.listFiles();
			assert fs != null;
			for (final File f : fs) {
				if (f.isFile() && f.getName().endsWith(".jar"))
					jars.add(f.toURI().toURL());
			}

			//final ClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]), JSR223Test.class.getClassLoader());
			final ClassLoader cl = new URLClassLoader(jars.toArray(new URL[jars.size()]));

			final List<ScriptEngineFactory> factories = new ScriptEngineManager(cl).getEngineFactories();
			System.out.println(factories.size());
			for (final ScriptEngineFactory factory : factories) {
				System.out.println(factory.getLanguageName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
