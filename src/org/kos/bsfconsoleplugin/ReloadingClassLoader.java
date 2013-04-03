package org.kos.bsfconsoleplugin;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Reloading class loader
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class ReloadingClassLoader extends ClassLoader {
	private final Map<String, ClassWithLastModified> loadedClasses = new HashMap<String, ClassWithLastModified>();
	private final String[] classPath;
	private final URL[] urls;
	@Nullable
	private ClassLoader delegate = null;

	public ReloadingClassLoader(final URL[] urls, final ClassLoader parent) {
		super(parent);
		this.urls = urls;
		this.classPath = ClassLoaderInfo.urlsToStrings(urls);
	}

	@Override
	protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		final Class c = findClass0(name);
		if (c == null)
			return super.loadClass(name, resolve);
		if (resolve)
			resolveClass(c);
		return c;
	}

	@Override
	protected synchronized Class<?> findClass(final String name) throws ClassNotFoundException {
		final Class<?> class0 = findClass0(name);
		return class0 == null ? findSystemClass(name) : class0;
	}

	@Nullable
	private Class<?> findClass0(final String name) throws ClassNotFoundException {
		final File f = findFile(name.replace('.', File.separatorChar), ".class");
		if (f == null)
			return null;

		final long fileLastModified = f.lastModified();

		final ClassWithLastModified cwlm = loadedClasses.get(name);
		if (cwlm != null) {
			if (fileLastModified < cwlm.lastModified)
				return cwlm.cls;
			else
				delegate = null;
		}

		final Class cls;
		try {
			if (delegate == null)
				delegate = new URLClassLoader(urls, getParent());

			cls = delegate.loadClass(name);
		} catch (ClassFormatError e) {
			throw new ClassNotFoundException("Can't load class " + name, e);
		}

		loadedClasses.put(name, new ClassWithLastModified(cls, System.currentTimeMillis()));
		return cls;
	}

	@Nullable
	@Override
	protected URL findResource(final String name) {
		final File f = findFile(name, "");
		if (f == null) return super.findResource(name);
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Nullable
	private File findFile(final String name, final String ext) {
		final String sfx = File.separatorChar + name.replace('/', File.separatorChar) + ext;
		for (final String path : classPath) {
			final File f = new File(new File(path).getPath() + sfx);
			if (f.exists() && f.canRead())
				return f;
		}
		return null;
	}

	private static class ClassWithLastModified {
		@NotNull
		final Class<?> cls;
		final long lastModified;

		private ClassWithLastModified(@NotNull final Class<?> cls, final long lastModified) {
			this.cls = cls;
			this.lastModified = lastModified;
		}
	}
}
