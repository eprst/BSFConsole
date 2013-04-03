package org.kos.bsfconsoleplugin;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;


public class ClassLoaderInfo {
	private static final Logger LOG = Logger.getInstance(ClassLoaderInfo.class);
	public final ClassLoader classLoader;
	public final String[] classPath;

	public ClassLoaderInfo(@NotNull final ClassLoader classLoader, @NotNull final String[] classPath) {
		this.classLoader = classLoader;
		this.classPath = classPath;
	}

	public ClassLoaderInfo(@NotNull final ClassLoader classLoader, @NotNull final URL[] urls) {
		this(classLoader, urlsToStrings(urls));
	}

	@NotNull
	public static String[] urlsToStrings(@NotNull final URL[] urls) {
		final String[] res = new String[urls.length];
		int i = 0;
		for (final URL url : urls)
			res[i++] = url.getFile();
		return res;
	}

	@NotNull
	public static URL[] stringsToUrls(@NotNull final String[] strings) {
		final List<URL> urls = new ArrayList<URL>();
		for (final String s : strings) {
			try {
				urls.add(new File(s).toURI().toURL());
			} catch (MalformedURLException e) {
				LOG.error("Error converting path '" + s + "' to URL", e);
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	@NotNull
	public static URL[] filesToUrls(@NotNull final File[] files) {
		final List<URL> urls = new ArrayList<URL>();
		for (final File f : files) {
			try {
				urls.add(f.toURI().toURL());
			} catch (MalformedURLException e) {
				LOG.error("Error converting file '" + f + "' to URL", e);
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}

	public String joinedClassPath() {
		String sep = "";
		final StringBuilder sb = new StringBuilder();
		for (final String cp : classPath) {
			sb.append(sep);
			sb.append(cp);
			sep = File.separator;
		}
		return sb.toString();
	}

	public URL[] classPathURLs() {
		return stringsToUrls(classPath);
	}
}
