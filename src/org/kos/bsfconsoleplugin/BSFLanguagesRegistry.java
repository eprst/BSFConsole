package org.kos.bsfconsoleplugin;

import java.util.*;

/**
 * Registry of BSF languages.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFLanguagesRegistry {
	private BSFConsoleConfig config;
	public LinkedList<BSFLanguage> bsfLanguages = new LinkedList<BSFLanguage>();
	private List<BSFLanguage> availableLanguages;

	public BSFLanguagesRegistry() {
		loadDefaultLanguages();
	}

	public void resetBSFLanguages() {
		getBsfLanguages().clear();
		loadDefaultLanguages();
	}

	private void loadDefaultLanguages() {
		getBsfLanguages().add(new BSFLanguage("javascript", "org.apache.bsf.engines.javascript.JavaScriptEngine"));
		getBsfLanguages().add(new BSFLanguage("jacl", "org.apache.bsf.engines.jacl.JaclEngine"));
		getBsfLanguages().add(new BSFLanguage("netrexx", "org.apache.bsf.engines.netrexx.NetRexxEngine"));
		getBsfLanguages().add(new BSFLanguage("java", "org.apache.bsf.engines.java.JavaEngine"));
		getBsfLanguages().add(new BSFLanguage("javaclass", "org.apache.bsf.engines.javaclass.JavaClassEngine"));
		getBsfLanguages().add(new BSFLanguage("bml", "org.apache.bml.ext.BMLEngine"));
		getBsfLanguages().add(new BSFLanguage("vbscript", "org.apache.bsf.engines.activescript.ActiveScriptEngine"));
		getBsfLanguages().add(new BSFLanguage("jscript", "org.apache.bsf.engines.activescript.ActiveScriptEngine"));
		getBsfLanguages().add(new BSFLanguage("perlscript", "org.apache.bsf.engines.activescript.ActiveScriptEngine"));
		getBsfLanguages().add(new BSFLanguage("perl", "org.apache.bsf.engines.perl.PerlEngine"));
		getBsfLanguages().add(new BSFLanguage("jpython", "org.apache.bsf.engines.jpython.JPythonEngine"));
		getBsfLanguages().add(new BSFLanguage("jython", "org.apache.bsf.engines.jython.JythonEngine"));
		getBsfLanguages().add(new BSFLanguage("lotusscript", "org.apache.bsf.engines.lotusscript.LsEngine"));
		getBsfLanguages().add(new BSFLanguage("xslt", "org.apache.bsf.engines.xslt.XSLTEngine"));
		getBsfLanguages().add(new BSFLanguage("pnuts", "pnuts.ext.PnutsBSFEngine"));
		getBsfLanguages().add(new BSFLanguage("beanbasic", "org.apache.bsf.engines.beanbasic.BeanBasicEngine"));
		getBsfLanguages().add(new BSFLanguage("beanshell", "bsh.util.BeanShellBSFEngine"));
		getBsfLanguages().add(new BSFLanguage("jruby", "org.jruby.javasupport.bsf.JRubyEngine"));
		getBsfLanguages().add(new BSFLanguage("judoscript", "com.judoscript.BSFJudoEngine"));
		getBsfLanguages().add(new BSFLanguage("groovy", "org.codehaus.groovy.bsf.GroovyEngine"));
		getBsfLanguages().add(new BSFLanguage("ant", "org.kos.bsfconsoleplugin.languages.AntConsoleBSFEngine"));
		Collections.sort(getBsfLanguages());

		availableLanguages = null;
	}

	public BSFLanguagesRegistry(final BSFConsoleConfig config) {
		this.config = config;
//		if (getBsfLanguages() != null && getBsfLanguages().isEmpty())
		loadDefaultLanguages();
	}

	public BSFLanguagesRegistry(final BSFLanguagesRegistry o) {
		this.config = o.config;
		bsfLanguages = new LinkedList<BSFLanguage>(o.bsfLanguages);
		availableLanguages = null;
	}

	public void setConfig(final BSFConsoleConfig config) {
		this.config = config;
	}

	public void addBSFLanguage(final BSFLanguage language) {
		getBsfLanguages().add(language);
		Collections.sort(getBsfLanguages());
	}

	public List<BSFLanguage> getBsfLanguages() {
		return bsfLanguages;
	}

	public void removeBSFLanguage(final BSFLanguage language) {
		if (availableLanguages != null)
			availableLanguages.remove(language);
		getBsfLanguages().remove(language);
		config.removeLanguageFromStartupScripts(language);
	}

	public void removeBSFLanguage(final int index) {
		if (availableLanguages != null)
			availableLanguages.remove(getBsfLanguages().get(index));
		config.removeLanguageFromStartupScripts(getBsfLanguages().get(index));
		getBsfLanguages().remove(index);
	}

	public List<BSFLanguage> getAvailableBSFLanguages() {
		if (availableLanguages != null)
			return availableLanguages;

		availableLanguages = new ArrayList<BSFLanguage>(getBsfLanguages().size());
		for (final BSFLanguage bsfLanguage : getBsfLanguages())
			if (bsfLanguage.isAvailable())
				availableLanguages.add(bsfLanguage);
		return availableLanguages;
	}

//	public boolean isBSFLanguageAvailable(final String languageName) {
//		for (final BSFLanguage language : getAvailableBSFLanguages())
//			if (language.languageName.equals(languageName))
//				return true;
//		return false;
//	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final BSFLanguagesRegistry that = (BSFLanguagesRegistry) o;

		return bsfLanguages.equals(that.bsfLanguages);
	}

	@Override
	public int hashCode() {
		return bsfLanguages.hashCode();
	}
}
