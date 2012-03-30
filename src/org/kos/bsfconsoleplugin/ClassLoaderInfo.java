package org.kos.bsfconsoleplugin;

public class ClassLoaderInfo {
	public final ClassLoader classLoader;
	public final String classPath;

	public ClassLoaderInfo(final ClassLoader classLoader, final String classPath) {
		this.classLoader = classLoader;
		this.classPath = classPath;
	}
}
