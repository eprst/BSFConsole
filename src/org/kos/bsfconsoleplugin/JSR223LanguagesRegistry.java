package org.kos.bsfconsoleplugin;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * JSR-223 languages registry.
 */
public class JSR223LanguagesRegistry {
	private final List<JSR223Language> languages;
	private final BSFConsolePlugin plugin;
	private boolean languagesInitialized = false;

	//todo: class loader must be changed when project libraries are changed

	public JSR223LanguagesRegistry(final BSFConsolePlugin plugin) {
		languages = new ArrayList<JSR223Language>();
		this.plugin = plugin;
	}

	private void initLanguages() {
		if (languagesInitialized) return;
		languagesInitialized = true;
		try {
			Class.forName("javax.script.ScriptEngineManager");
		} catch (ClassNotFoundException e) {
			return; //we're on 1.5
		}

		assert plugin != null;
		final ClassLoaderManager classLoaderManager = plugin.getClassLoaderManager();

		final ClassLoaderInfo classLoaderInfo = classLoaderManager.getModuleClassLoader();
		final String classPath = classLoaderInfo.classPath;
		final String sunBootClassPath = System.getProperty("sun.boot.class.path");
		if (sunBootClassPath != null && !sunBootClassPath.contains(classPath)) { //hack for Scala
			final String pathSep = System.getProperty("path.separator");
			System.setProperty("sun.boot.class.path", sunBootClassPath+pathSep+classPath);
		}
		final ClassLoader l = classLoaderInfo.classLoader;
		Thread.currentThread().setContextClassLoader(l); //hack for Clojure
		try {
			@SuppressWarnings({"unchecked"})
			final Class<ScriptEngineManager> cls = (Class<ScriptEngineManager>) l.loadClass(ScriptEngineManager.class.getName());
			final Constructor<ScriptEngineManager> constructor = cls.getConstructor(ClassLoader.class);
			final ScriptEngineManager sem = constructor.newInstance(l);

			//final ScriptEngineManager sem = new ScriptEngineManager(classLoaderInfo.classLoader);
			for (final ScriptEngineFactory f : sem.getEngineFactories()) {
				final JSR223Language lang = new JSR223Language(f);
				if (!languages.contains(lang))
					languages.add(lang);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.sort(languages);
	}

	public void resetLanguages() {
		languagesInitialized = false;
		languages.clear();
	}

	public List<JSR223Language> getLanguages() {
		initLanguages();
		return languages;
	}

	public List<JSR223Language> getAvailableLanguages() {
		initLanguages();
		final ArrayList<JSR223Language> res = new ArrayList<JSR223Language>();
		for (final JSR223Language language : languages) {
			if (language.isAvailable())
				res.add(language);
		}
		return res;
	}
}
