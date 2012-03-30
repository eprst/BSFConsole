package org.kos.bsfconsoleplugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.zip.ZipFile;

/**
 * Factory for creating classloader for module.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class ModuleClassLoaderFactory {
	private static final Logger LOG = Logger.getInstance("org.kos.bsfconsoleplugin.ModuleClassLoaderFactory");

	/**
	 * Gets classloader for the passed module.
	 *
	 * @param module            module to get classloader for.
	 * @param parentClassLoader parent class loader.
	 * @param includeOutputPath if output path must be included.
	 * @param includeTestsOutputPath if tests path must be included.
	 * @param additionalPaths   additional paths.
	 *
	 * @return classloader for the module.
	 */
	public static ClassLoader getClassLoaderForModule(final Module module, final ClassLoader parentClassLoader,
	                                                  final boolean includeOutputPath, final boolean includeTestsOutputPath,
	                                                  final URL[] additionalPaths) {

		final ArrayList<File> paths = getModulePaths(module, includeOutputPath, includeTestsOutputPath);

		final URL[] urls = new URL[paths.size() + additionalPaths.length];
		int i = 0;
		for (final File path : paths) {
			try {
				urls[i++] = path.toURI().toURL();
			} catch (MalformedURLException e) {
				LOG.error("Error converting file '" + path.getAbsolutePath() + "' to URL", e);
			}
		}

		for (final URL additionalPath : additionalPaths)
			urls[i++] = additionalPath;

		if (parentClassLoader == null)
			return new URLClassLoader(urls);
		else
			return new URLClassLoader(urls, parentClassLoader);
	}

	public static String getModuleClasspath(final Module module, final boolean includeOutputPath,
	                                        final boolean includeTestsOutputPath) {
		final ArrayList<File> paths = getModulePaths(module, includeOutputPath, includeTestsOutputPath);
		final StringBuffer res = new StringBuffer();
		final String pathSep = System.getProperty("path.separator");
		boolean first = true;

		for (final File path : paths) {
			if (!first)
				res.append(pathSep);
			else
				first = false;
			res.append(path.getAbsolutePath());
		}

		return res.toString();
	}

	private static ArrayList<File> getModulePaths(final Module module, final boolean includeOutputPath,
	                                              final boolean includeTestsOutputPath) {
		final ModuleRootManager mrm = ModuleRootManager.getInstance(module);
		final VirtualFile[] files = mrm.getFiles(OrderRootType.CLASSES_AND_OUTPUT);
		final FileTypeManager ftmgr = FileTypeManager.getInstance();

		final ArrayList<File> paths = new ArrayList<File>(files.length);

		for (final VirtualFile virtualFile : files) {
			final FileType fileType = ftmgr.getFileTypeByFile(virtualFile);

			if (!virtualFile.isValid())
				continue;

			if (!virtualFile.isDirectory() && !fileType.equals(StdFileTypes.ARCHIVE))
				continue;

			final File file;

			final VirtualFileSystem fileSystem = virtualFile.getFileSystem();
			if (fileSystem instanceof LocalFileSystem)
				file = new File(virtualFile.getPath().replace('/', File.separatorChar));
			else if (fileSystem instanceof JarFileSystem) {
				final JarFileSystem jarFileSystem = (JarFileSystem) fileSystem;
				try {
					final ZipFile jarFile = jarFileSystem.getJarFile(virtualFile);
					if (jarFile == null) continue;
					file = new File(jarFile.getName());
				} catch (IOException e) {
					LOG.info("error getting jar file: " + e);
					continue;
				}
			} else
				continue;

			if (fileIsValid(file))
				paths.add(file);
		}

		if (includeOutputPath) {
			final File file = new File(CompilerOutputPaths.getModuleOutputPath(module, false));
			if (fileIsValid(file))
				paths.add(file);
		}
		if (includeTestsOutputPath) {
			final File file = new File(CompilerOutputPaths.getModuleOutputPath(module, true));
			if (fileIsValid(file))
				paths.add(file);
		}

		return paths;
	}

	private static boolean fileIsValid(final File file) {
		return file.exists() && file.canRead();
	}
}