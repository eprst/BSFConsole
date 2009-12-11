/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Plugin configuration. List of supported languages.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
@SuppressWarnings({"UnusedDeclaration"})
public class BSFConsoleConfig {
	private ArrayList<StartupScript> startupScripts = new ArrayList<StartupScript>();
	private BSFLanguagesRegistry bsfLanguagesRegistry = new BSFLanguagesRegistry(this);
	private boolean restoreSystemStreams;
	private boolean outWaitsForErr;
	private boolean errWaitsForOut;
	private boolean storeDupsInRecentCommands;
	private String moduleForClasspath = "";
	private boolean includeOutputPath;
	private boolean includeTestsOutputPath;
	private boolean hideExceptionStacktraces;
	private BSFConsoleSearchOptions searchOptions = new BSFConsoleSearchOptions();

	private Map<String, Integer> preferredRecentCommandsDividerLocations = new HashMap<String, Integer>(); //language tostring->position

	public BSFConsoleConfig() {
		getBsfLanguagesRegistry().resetBSFLanguages();
	}

	public void resetBSFLanguages() {
		getBsfLanguagesRegistry().resetBSFLanguages();

		for (Iterator<StartupScript> it = startupScripts.iterator(); it.hasNext();) {
			final StartupScript startupScript = it.next();
			final BSFLanguage lang = findBSFLanguageByLabel(startupScript.languageLabel);
			if (lang == null || !lang.isAvailable())
				it.remove();
		}

		preferredRecentCommandsDividerLocations.clear();
	}

	@Nullable
	public BSFLanguage findBSFLanguageByLabel(final String l) {
		for (final BSFLanguage language : bsfLanguagesRegistry.getBsfLanguages()) {
			if (language.getLabel().equals(l))
				return language;
		}
		return null;
	}

	/**
	 * Deep copy constructor.
	 *
	 * @param origianal config to copy.
	 */
	public BSFConsoleConfig(final BSFConsoleConfig origianal) {
		bsfLanguagesRegistry = new BSFLanguagesRegistry(origianal.getBsfLanguagesRegistry());

		startupScripts = new ArrayList<StartupScript>(origianal.startupScripts.size());
		for (final StartupScript script : origianal.startupScripts) {
			startupScripts.add(new StartupScript(script));
		}

		restoreSystemStreams = origianal.restoreSystemStreams;
		outWaitsForErr = origianal.outWaitsForErr;
		errWaitsForOut = origianal.errWaitsForOut;
		storeDupsInRecentCommands = origianal.storeDupsInRecentCommands;
		preferredRecentCommandsDividerLocations = origianal.preferredRecentCommandsDividerLocations;
		moduleForClasspath = origianal.moduleForClasspath;
		includeOutputPath = origianal.includeOutputPath;
		includeTestsOutputPath = origianal.includeTestsOutputPath;
		hideExceptionStacktraces = origianal.hideExceptionStacktraces;
		searchOptions = new BSFConsoleSearchOptions(origianal.searchOptions);
	}

	public BSFConsoleSearchOptions getSearchOptions() {
		return searchOptions;
	}

	public void setSearchOptions(final BSFConsoleSearchOptions searchOptions) {
		this.searchOptions = searchOptions;
	}

	public void addStartupScript(final StartupScript startupScript) {
		startupScripts.add(startupScript);
	}

	public void removeStartupScript(final StartupScript startupScript) {
		startupScripts.remove(startupScript);
	}

	public void removeStartupScript(final int index) {
		startupScripts.remove(index);
	}

	public ArrayList<StartupScript> getStartupScripts() {
		return startupScripts;
	}

	void removeLanguageFromStartupScripts(final Language language) {
		for (Iterator<StartupScript> it = startupScripts.iterator(); it.hasNext();) {
			final StartupScript startupScript = it.next();
			if (startupScript.languageLabel.equals(language.getLabel()))
				it.remove();
		}
	}

	public boolean isRestoreSystemStreams() {
		return restoreSystemStreams;
	}

	public void setRestoreSystemStreams(final boolean restoreSystemStreams) {
		this.restoreSystemStreams = restoreSystemStreams;
	}

	public boolean isOutWaitsForErr() {
		return outWaitsForErr;
	}

	public void setOutWaitsForErr(final boolean outWaitsForErr) {
		this.outWaitsForErr = outWaitsForErr;
	}

	public boolean isErrWaitsForOut() {
		return errWaitsForOut;
	}

	public void setErrWaitsForOut(final boolean errWaitsForOut) {
		this.errWaitsForOut = errWaitsForOut;
	}

	public boolean isStoreDupsInRecentCommands() {
		return storeDupsInRecentCommands;
	}

	public void setStoreDupsInRecentCommands(final boolean storeDupsInRecentCommands) {
		this.storeDupsInRecentCommands = storeDupsInRecentCommands;
	}

	public int getPreferredRecentCommandsDividerLocation(final Language language) {
		if (!preferredRecentCommandsDividerLocations.containsKey(language.getLabel()))
			return -1;

		return preferredRecentCommandsDividerLocations.get(language.getLabel());
	}

	public void setPreferredRecentCommandsDividerLocation(final Language language, final int position) {
		preferredRecentCommandsDividerLocations.put(language.getLabel(), position);
	}

	public String getModuleForClasspath() {
		return moduleForClasspath;
	}

	public void setModuleForClasspath(final String moduleForClasspath) {
		this.moduleForClasspath = moduleForClasspath;
	}

	public boolean isIncludeOutputPath() {
		return includeOutputPath;
	}

	public void setIncludeOutputPath(final boolean includeOutputPath) {
		this.includeOutputPath = includeOutputPath;
	}

	public boolean isIncludeTestsOutputPath() {
		return includeTestsOutputPath;
	}

	public void setIncludeTestsOutputPath(final boolean includeTestsOutputPath) {
		this.includeTestsOutputPath = includeTestsOutputPath;
	}

	public boolean isHideExceptionStacktraces() {
		return hideExceptionStacktraces;
	}

	public void setHideExceptionStacktraces(final boolean hideExceptionStacktraces) {
		this.hideExceptionStacktraces = hideExceptionStacktraces;
	}

	public BSFLanguagesRegistry getBsfLanguagesRegistry() {
		bsfLanguagesRegistry.setConfig(this);
		return bsfLanguagesRegistry;
	}

	public void setBsfLanguagesRegistry(final BSFLanguagesRegistry bsfLanguagesRegistry) {
		this.bsfLanguagesRegistry = bsfLanguagesRegistry;
	}

	public void setStartupScripts(final ArrayList<StartupScript> startupScripts) {
		this.startupScripts = startupScripts;
	}

	public void setPreferredRecentCommandsDividerLocations(final Map<String, Integer> preferredRecentCommandsDividerLocations) {
		this.preferredRecentCommandsDividerLocations = preferredRecentCommandsDividerLocations;
	}

	public Map<String, Integer> getPreferredRecentCommandsDividerLocations() {
		return preferredRecentCommandsDividerLocations;
	}

	@Override
	@SuppressWarnings({"NonFinalFieldReferenceInEquals"})
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof BSFConsoleConfig)) return false;

		final BSFConsoleConfig bsfConsoleConfig = (BSFConsoleConfig) o;

		if (!bsfLanguagesRegistry.equals(bsfConsoleConfig.bsfLanguagesRegistry)) return false;
		if (errWaitsForOut != bsfConsoleConfig.errWaitsForOut) return false;
		if (outWaitsForErr != bsfConsoleConfig.outWaitsForErr) return false;
		if (restoreSystemStreams != bsfConsoleConfig.restoreSystemStreams) return false;
		if (storeDupsInRecentCommands != bsfConsoleConfig.storeDupsInRecentCommands) return false;
		if (!startupScripts.equals(bsfConsoleConfig.startupScripts)) return false;
		if (!moduleForClasspath.equals(bsfConsoleConfig.moduleForClasspath)) return false;
		if (includeOutputPath != bsfConsoleConfig.includeOutputPath) return false;
		if (includeTestsOutputPath != bsfConsoleConfig.includeTestsOutputPath) return false;
		return hideExceptionStacktraces == bsfConsoleConfig.hideExceptionStacktraces;
	}

	@Override
	public int hashCode() {
		int result;
		result = bsfLanguagesRegistry.hashCode();
		result = 29 * result + bsfLanguagesRegistry.hashCode();
		result = 29 * result + startupScripts.hashCode();
		result = 29 * result + (restoreSystemStreams ? 1 : 0);
		result = 29 * result + (outWaitsForErr ? 1 : 0);
		result = 29 * result + (errWaitsForOut ? 1 : 0);
		result = 29 * result + (storeDupsInRecentCommands ? 1 : 0);
		result = 29 * result + moduleForClasspath.hashCode();
		result = 29 * result + (includeOutputPath ? 1 : 0);
		result = 29 * result + (includeTestsOutputPath ? 1 : 0);
		result = 29 * result + (hideExceptionStacktraces ? 1 : 0);
		return result;
	}
}