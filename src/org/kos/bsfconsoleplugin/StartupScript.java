/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   k_o_s@mail.ru                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin;

import java.io.File;

/**
 * Startup script description.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class StartupScript {
	public String languageLabel;
	public String scriptFileName;

	public StartupScript() {
	}

	public StartupScript(final StartupScript other) {
		this (other.languageLabel, other.scriptFileName);
	}

	public StartupScript(final String languageLabel, final String scriptFileName) {
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