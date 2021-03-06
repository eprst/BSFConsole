package org.kos.bsfconsoleplugin;

import java.io.File;

import org.jetbrains.annotations.Nullable;


/**
 * Startup script description.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class StartupScript {
	public String languageLabel;
	public String scriptFileName;

	public StartupScript() {
	}

	public StartupScript(final StartupScript other) {
		this (other.languageLabel, other.scriptFileName);
	}

	public StartupScript(final String languageLabel, @Nullable final String scriptFileName) {
		this.languageLabel = languageLabel;
		this.scriptFileName = scriptFileName;
	}

	public boolean scriptIsValid() {
		if (scriptFileName == null)
			return false;
		final File scriptFile = new File(scriptFileName);
		return scriptFile.exists() && scriptFile.isFile() && scriptFile.canRead();
	}

	@Override
	@SuppressWarnings({"NonFinalFieldReferenceInEquals"})
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof StartupScript)) return false;

		final StartupScript startupScript = (StartupScript) o;

		if (!languageLabel.equals(startupScript.languageLabel)) return false;
		return !(scriptFileName != null ? !scriptFileName.equals(startupScript.scriptFileName) : startupScript.scriptFileName != null);
	}

	@Override
	public int hashCode() {
		int result;
		result = languageLabel.hashCode();
		result = 29 * result + (scriptFileName != null ? scriptFileName.hashCode() : 0);
		return result;
	}
}