package org.kos.bsfconsoleplugin.languages;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Ant console completion manager.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class AntConsoleCompletionManager extends KeyWordCompletionManager {
	private static final Set<String> KEYWORDS = new HashSet<String>();

	private AntConsole antConsole;
	private KeyWordCompletionManager onOffCompletionManager;

	static {
		KEYWORDS.add("help");
		KEYWORDS.add("exit");
		KEYWORDS.add("desc");
		KEYWORDS.add("load");
		KEYWORDS.add("find");
		KEYWORDS.add("reload");
		KEYWORDS.add("timer");
		KEYWORDS.add("target");
	}

	public AntConsoleCompletionManager(final AntConsole antConsole) {
		super(KEYWORDS);
		this.antConsole = antConsole;

		final ArrayList<String> onOff = new ArrayList<String>(2);
		onOff.add("on");
		onOff.add("off");
		onOffCompletionManager = new KeyWordCompletionManager(onOff);
	}

	/**
	 * Finds all possible completions if the given line.
	 *
	 * @param line current line of text.
	 *
	 * @return array of all possible completions.
	 */
	@Override
	public String[] complete(final String line) {
		final String lastWord = CompletionManagerUtils.getLastWord(line);
		final HashSet<String> res = new HashSet<String>();
		final int lastWordLength = lastWord.length();
		if (line.length() == lastWordLength)
			res.addAll(Arrays.asList(super.complete(line)));

		if (line.length() == lastWordLength || line.startsWith("target ")) {
			final Project project = antConsole.getProject();
			if (project != null) {
				//noinspection unchecked
				final Enumeration<? extends Target> targets = project.getTargets().elements();
				while (targets.hasMoreElements()) {
					final String targetName = targets.nextElement().getName();
					if (targetName.startsWith(lastWord))
						res.add(targetName.substring(lastWordLength));
				}
			}
		}

		if (line.length() > lastWordLength) {
			if (line.startsWith("load ") || line.startsWith("find "))
				res.addAll(Arrays.asList(new FileNameCompletionManager(false).complete(line)));
			else if (line.startsWith("timer "))
				res.addAll(Arrays.asList(onOffCompletionManager.complete(line)));
		}

		return res.toArray(new String[res.size()]);
	}

	/**
	 * Creates completion manager instance.
	 *
	 * @param languageName language name.
	 * @param engine       language engine.
	 *
	 * @return constructed manager or <code>null</code> if manager cannot be constructed.
	 */
	@Nullable
	public static CompletionManager getInstance(final String languageName, final Object engine) {
		if ("ant".equals(languageName) && engine instanceof AntConsoleBSFEngine) {
			final AntConsoleBSFEngine antEngine = (AntConsoleBSFEngine) engine;
			return new AntConsoleCompletionManager(antEngine.getConsole());
		}
		return null;
	}
}