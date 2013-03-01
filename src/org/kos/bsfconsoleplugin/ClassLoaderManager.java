package org.kos.bsfconsoleplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.application.PathManager;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

public class ClassLoaderManager {
	private static final Logger LOG = Logger.getInstance(ClassLoaderManager.class);
	private static URL[] antAndPluginLibs;
	private static String pureClassPath; //java.class.path without module paths

	private final BSFConsolePlugin plugin;

	public ClassLoaderManager(final BSFConsolePlugin BSFConsolePlugin) {
		this.plugin = BSFConsolePlugin;

		final ArrayList<URL> urls = new ArrayList<URL>();
		final StringBuilder path = new StringBuilder();

		final String libPath = PathManager.getLibPath();
		if (libPath != null)
			addUrlsFromDir(new File(libPath, "ant"), urls, new String[0], path);

		final StringBuilder sb = new StringBuilder(PathManager.getPluginsPath());
		//noinspection AccessStaticViaInstance
		sb.append(File.separatorChar).append(BSFConsolePlugin.PLUGIN_NAME);
		sb.append(File.separatorChar).append("lib");
		addUrlsFromDir(new File(sb.toString()), urls, new String[0], path);
		//addUrlsFromDir(new File(sb.toString()), urls, new String[]{"bsf.jar"}); //causes ClassNotFoundException: org.apache.bsf.BSFManager
		//at org.kos.bsfconsoleplugin.BSFConsolePlugin.loadBSFManagerUsingClassLoader(BSFConsolePlugin.java:267)

		antAndPluginLibs = urls.toArray(new URL[urls.size()]);
		pureClassPath = path.toString();
	}


	public ClassLoaderInfo getPluginLibsClassLoader() {
		return new ClassLoaderInfo(new URLClassLoader(antAndPluginLibs, plugin.getClass().getClassLoader()), pureClassPath);
	}

	public ClassLoaderInfo getModuleClassLoader() {
		final ClassLoaderInfo pluginCLI = getPluginLibsClassLoader();
		final Module module = ModuleUtils.findModuleByName(plugin.getProject(), plugin.getConfig().getModuleForClasspath());
		if (module != null) {
			final ClassLoader classLoaderForModule = ModuleClassLoaderFactory.getClassLoaderForModule(
					module, pluginCLI.classLoader,
					plugin.getConfig().isIncludeOutputPath(), plugin.getConfig().isIncludeTestsOutputPath(),
					antAndPluginLibs
			);

			if (classLoaderForModule != null) {
				return new ClassLoaderInfo(classLoaderForModule,
						pluginCLI.classPath + System.getProperty("path.separator") +
						ModuleClassLoaderFactory.getModuleClasspath(
								module, plugin.getConfig().isIncludeOutputPath(), plugin.getConfig().isIncludeTestsOutputPath()
						));
			}
		}
		return pluginCLI;
	}

	public <T> Triple<T, ClassLoader, String> loadWithScriptClassLoader(final Class<T> originalClass) {
		final ClassLoaderInfo loaderInfo = getModuleClassLoader();
		@SuppressWarnings({"unchecked"}) final T res = (T) loadUsingClassLoader(loaderInfo.classLoader, originalClass.getName());
		if (loaderInfo.classPath != null) {
			//res.setClassPath(cp);
			System.setProperty("java.class.path", loaderInfo.classPath); //exclusively for BeanShell and Groovy!
		}
		return new Triple<T, ClassLoader, String>(res, loaderInfo.classLoader, loaderInfo.classPath);
	}

	@Nullable
	private Object loadUsingClassLoader(final ClassLoader classLoader, final String className) {
		try {
			//noinspection unchecked
			final Class<?> cls = classLoader.loadClass(className);
			final Object instance = cls.newInstance();
			LOG.debug("Got       : " + instance.getClass().getName());
			return instance;
		} catch (ClassNotFoundException e) {
			LOG.error("Internal error", e);
		} catch (InstantiationException e) {
			LOG.error("Internal error", e);
		} catch (IllegalAccessException e) {
			LOG.error("Internal error", e);
		}
		return null;
	}

	private static void addUrlsFromDir(final File dir, final ArrayList<URL> urls, final String[] exclude, final StringBuilder path) {
		LOG.debug("Scanning " + dir.getAbsolutePath());
		if (!dir.exists() || !dir.canRead() || !dir.isDirectory()) {
			LOG.info("Not a directory or cant' access " + dir.getAbsolutePath());
			return;
		}

		final String pathSep = System.getProperty("path.separator");
		final List<String> excludeList = Arrays.asList(exclude);

		final File[] jarFiles = dir.listFiles(
				new FilenameFilter() {
					@Override
					public boolean accept(final File dir, final String name) {
						return name.endsWith(".jar");
					}
				}
		);

		for (final File jarFile : jarFiles)
			try {
				if (excludeList.contains(jarFile.getName())) {
					LOG.debug("Excluding " + jarFile.getName());
					continue;
				}
				urls.add(jarFile.toURI().toURL());
				if (path.length() > 0) path.append(pathSep);
				try {
					path.append(jarFile.getCanonicalPath());
				} catch (IOException e) {
					path.append(jarFile.getAbsolutePath());
				}
				LOG.debug("Added " + urls.get(urls.size() - 1));
			} catch (MalformedURLException e) {
				LOG.error("Unexpected exception: " + e, e);
			}
	}
}
