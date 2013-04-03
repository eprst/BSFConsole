package org.kos.bsfconsoleplugin;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.vfs.JarFile;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.graph.Graph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
	 * @param module                 module to get classloader for.
	 * @param parentClassLoaderInfo  parent class loader info.
	 * @param includeOutputPath      if output path must be included.
	 * @param includeTestsOutputPath if tests path must be included.
	 *
	 * @return classloader for the module.
	 */
	@NotNull
	public static ClassLoaderInfo getClassLoaderInfoForModule(@NotNull final Module module, @Nullable final ClassLoaderInfo parentClassLoaderInfo,
	                                                          final boolean includeOutputPath, final boolean includeTestsOutputPath) {

//		final ArrayList<File> paths = getModulePaths(module, includeOutputPath, includeTestsOutputPath);
		final ArrayList<File> moduleLibraryFiles = getModulePaths(module, false, false);

		final URL[] librariesURLs = ClassLoaderInfo.filesToUrls(moduleLibraryFiles.toArray(new File[moduleLibraryFiles.size()]));

		final ClassLoader librariesAndParentClassLoader;
		final List<URL> librariesAndParentURLs = new ArrayList<URL>();

		if (parentClassLoaderInfo == null) {
			librariesAndParentClassLoader = new URLClassLoader(librariesURLs);
			librariesAndParentURLs.addAll(Arrays.asList(librariesURLs));
		}
		else {
			librariesAndParentClassLoader = new URLClassLoader(librariesURLs, parentClassLoaderInfo.classLoader);
			librariesAndParentURLs.addAll(Arrays.asList(parentClassLoaderInfo.classPathURLs()));
			librariesAndParentURLs.addAll(Arrays.asList(librariesURLs));
		}

		final URL[] outputAndTestPaths = getOutputAndTestPaths(module, includeOutputPath, includeTestsOutputPath);

		if (outputAndTestPaths.length == 0) {
			return new ClassLoaderInfo(librariesAndParentClassLoader, librariesAndParentURLs.toArray(new URL[librariesAndParentURLs.size()]));
		}

		final List<URL> outputAndLibrariesAndParentURLs = new ArrayList<URL>();
		outputAndLibrariesAndParentURLs.addAll(Arrays.asList(outputAndTestPaths));
		outputAndLibrariesAndParentURLs.addAll(librariesAndParentURLs);

		final ClassLoader reloadingCL = new ReloadingClassLoader(outputAndTestPaths, librariesAndParentClassLoader);
		return new ClassLoaderInfo(reloadingCL, outputAndLibrariesAndParentURLs.toArray(new URL[outputAndLibrariesAndParentURLs.size()]));
	}

	private static URL[] getOutputAndTestPaths(final Module module, final boolean includeOutputPath, final boolean includeTestsOutputPath) {
		if (!includeOutputPath && !includeTestsOutputPath)
			return new URL[0];

		final ArrayList<File> paths = new ArrayList<File>(2);

		if (includeOutputPath) {
			final File file = new File(CompilerOutputPaths.getModuleOutputPath(module));
			if (fileIsValid(file))
				paths.add(file);
		}
		if (includeTestsOutputPath) {
			final File file = new File(CompilerOutputPaths.getModuleTestOutputPath(module));
			if (fileIsValid(file))
				paths.add(file);
		}

		return ClassLoaderInfo.filesToUrls(paths.toArray(new File[paths.size()]));
	}

	private static ArrayList<File> getModulePaths(final Module module, final boolean includeOutputPath, final boolean includeTestsOutputPath) {
		final Module[] modules = getModuleWithTransitiveDependencies(module);
		final VirtualFile[] libraryRoots = LibraryUtil.getLibraryRoots(modules, false, false);

		final FileTypeManager ftmgr = FileTypeManager.getInstance();

		final ArrayList<File> paths = new ArrayList<File>(libraryRoots.length);

		for (final VirtualFile virtualFile : libraryRoots) {
			if (virtualFile == null)
				continue;

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
					final JarFile jarFile = jarFileSystem.getJarFile(virtualFile);
					if (jarFile == null) continue;
					final ZipFile zipFile = jarFile.getZipFile();
					if (zipFile == null) continue;
					file = new File(zipFile.getName());
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
			final File file = new File(CompilerOutputPaths.getModuleOutputPath(module));
			if (fileIsValid(file))
				paths.add(file);
		}
		if (includeTestsOutputPath) {
			final File file = new File(CompilerOutputPaths.getModuleTestOutputPath(module));
			if (fileIsValid(file))
				paths.add(file);
		}

		return paths;
	}

	private static boolean fileIsValid(final File file) {
		return file.exists() && file.canRead();
	}

	@NotNull
	private static Module[] getModuleWithTransitiveDependencies(@NotNull final Module m) {
		final ModuleManager moduleManager = ModuleManager.getInstance(m.getProject());
		final Graph<Module> graph = moduleManager.moduleGraph(true);
		final List<Module> res = new ArrayList<Module>();
		addModuleWithTransitiveDependencies(m, graph, res, new HashSet<String>());
		return res.toArray(new Module[res.size()]);
	}

	private static void addModuleWithTransitiveDependencies(@NotNull final Module m, @NotNull final Graph<Module> g,
	                                                        @NotNull final List<Module> res, @NotNull final Set<String> visitedModuleNames) {
		res.add(m);
		visitedModuleNames.add(m.getName());

		final Iterator<Module> out = g.getOut(m);
		while (out.hasNext()) {
			final Module module = out.next();
			if (!visitedModuleNames.contains(module.getName()))
				addModuleWithTransitiveDependencies(module, g, res, visitedModuleNames);
		}
	}
}