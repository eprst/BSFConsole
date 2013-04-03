package org.kos.bsfconsoleplugin;


import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.jetbrains.annotations.NotNull;


/**
 * File utilities
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class FileUtils {
	public static boolean isJarFile(@NotNull final String fileName) {
		try {
			ZipFile zf = null;
			try {
				zf = new ZipFile(fileName);
				return zf.getEntry("META-INF/MANIFEST.MF") != null;
			} finally {
				if (zf != null)
					zf.close();
			}
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isJarFile(@NotNull final File file) {
		try {
			ZipFile zf = null;
			try {
				zf = new ZipFile(file);
				return zf.getEntry("META-INF/MANIFEST.MF") != null;
			} finally {
				if (zf != null)
					zf.close();
			}
		} catch (IOException e) {
			return false;
		}
	}
}
