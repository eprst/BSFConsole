package org.kos.bsfconsoleplugin;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.apache.bsf.BSFManager;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public class LanguageManager {
	private final BSFConsolePlugin plugin;
	private JSR223LanguagesRegistry jsr223LanguagesRegistry;
	private List<Language> allLanguages;
	private List<Language> availableLanguages;

	public LanguageManager(final BSFConsolePlugin plugin) {
		this.plugin = plugin;
		init();
	}

	public void init() {
		jsr223LanguagesRegistry = new JSR223LanguagesRegistry(plugin);
	}

	public void registerBSFLanguages() {
		for (final BSFLanguage bsfLanguage : plugin.getConfig().getBsfLanguagesRegistry().getBsfLanguages()) {
			final String languageName = bsfLanguage.languageName;
			//if (!BSFManager.isLanguageRegistered(languageName))
			BSFManager.registerScriptingEngine(languageName, bsfLanguage.engineClassName, null);
		}
	}

	public JSR223LanguagesRegistry getJsr223LanguagesRegistry() {
		assert jsr223LanguagesRegistry != null : "a1";
		return jsr223LanguagesRegistry;
	}

	@Nullable
	public Language findLanguageByLabel(@NotNull final String label) {
		final Language l = findLanguageByLabel(label, plugin.getConfig().getBsfLanguagesRegistry().getBsfLanguages());
		if (l != null) return l;
		return findLanguageByLabel(label, getJsr223LanguagesRegistry().getLanguages());
	}

	@Nullable
	public Language findLanguageByLabel(@NotNull final String label, @NotNull final Collection<? extends Language> langs) {
		for (final Language lang : langs) {
			if (lang.getLabel().equals(label))
				return lang;
		}
		return null;
	}

	public List<Language> getAvailableLanguages() {
		if (availableLanguages == null || availableLanguages.size() == 0) {
			availableLanguages = new ArrayList<Language>();
			availableLanguages.addAll(plugin.getConfig().getBsfLanguagesRegistry().getAvailableBSFLanguages());
			availableLanguages.addAll(getJsr223LanguagesRegistry().getAvailableLanguages());
		}
		return availableLanguages;
	}

	public List<Language> getAllLanguages() {
		if (allLanguages == null || allLanguages.size() == 0) {
			allLanguages = new ArrayList<Language>();
			allLanguages.addAll(plugin.getConfig().getBsfLanguagesRegistry().getBsfLanguages());
			allLanguages.addAll(getJsr223LanguagesRegistry().getLanguages());
		}
		return allLanguages;
	}
}
