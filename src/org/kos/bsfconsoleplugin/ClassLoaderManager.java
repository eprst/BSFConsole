package org.kos.bsfconsoleplugin;


import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.Nullable;

public class ClassLoaderManager {
	private static final Logger LOG = Logger.getInstance(ClassLoaderManager.class);
	private static URL[] antAndPluginLibs;

	private final BSFConsolePlugin plugin;

	public ClassLoaderManager(final BSFConsolePlugin BSFConsolePlugin) {
		this.plugin = BSFConsolePlugin;

		final ArrayList<URL> urls = new ArrayList<URL>();

		final String libPath = PathManager.getLibPath();
		if (libPath != null)
			addUrlsFromDir(new File(libPath, "ant"), urls, new String[0]);

		final StringBuilder sb = new StringBuilder(PathManager.getPluginsPath());
		//noinspection AccessStaticViaInstance
		sb.append(File.separatorChar).append(BSFConsolePlugin.PLUGIN_NAME);
		sb.append(File.separatorChar).append("lib");
		addUrlsFromDir(new File(sb.toString()), urls, new String[0]);
		//addUrlsFromDir(new File(sb.toString()), urls, new String[]{"bsf.jar"}); //causes ClassNotFoundException: org.apache.bsf.BSFManager
		//at org.kos.bsfconsoleplugin.BSFConsolePlugin.loadBSFManagerUsingClassLoader(BSFConsolePlugin.java:267)

		antAndPluginLibs = urls.toArray(new URL[urls.size()]);
	}


	private ClassLoaderInfo getPluginLibsClassLoaderInfo() {
		return new ClassLoaderInfo(new URLClassLoader(antAndPluginLibs, plugin.getClass().getClassLoader()), antAndPluginLibs);
	}

	public ClassLoaderInfo getModuleClassLoaderInfo() {
		final ClassLoaderInfo pluginCLI = getPluginLibsClassLoaderInfo();
		final Module module = ModuleUtils.findModuleByName(plugin.getProject(), plugin.getConfig().getModuleForClasspath());
		if (module != null) {
			return ModuleClassLoaderFactory.getClassLoaderInfoForModule(
					module, pluginCLI,
					plugin.getConfig().isIncludeOutputPath(), plugin.getConfig().isIncludeTestsOutputPath()
			);
		}
		return pluginCLI;
	}

	public <T> Triplet<T, ClassLoader, String> loadWithScriptClassLoader(final Class<T> originalClass) {
		final ClassLoaderInfo loaderInfo = getModuleClassLoaderInfo();
		@SuppressWarnings({"unchecked"}) final T res = (T) loadUsingClassLoader(loaderInfo.classLoader, originalClass.getName());
		if (loaderInfo.classPath.length > 0) {
			//res.setClassPath(cp);
			System.setProperty("java.class.path", loaderInfo.joinedClassPath()); //exclusively for BeanShell and Groovy!
		}
		return new Triplet<T, ClassLoader, String>(res, loaderInfo.classLoader, loaderInfo.joinedClassPath());
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

	private static void addUrlsFromDir(final File dir, final ArrayList<URL> urls, final String[] exclude) {
		LOG.debug("Scanning " + dir.getAbsolutePath());
		if (!dir.exists() || !dir.canRead() || !dir.isDirectory()) {
			LOG.info("Not a directory or cant' access " + dir.getAbsolutePath());
			return;
		}

		final List<String> excludeList = Arrays.asList(exclude);

		final File[] jarFiles = dir.listFiles(
				new FileFilter() {
					@Override
					public boolean accept(final File file) {
						return FileUtils.isJarFile(file);
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
				LOG.debug("Added " + urls.get(urls.size() - 1));
			} catch (MalformedURLException e) {
				LOG.error("Unexpected exception: " + e, e);
			}
	}
}
