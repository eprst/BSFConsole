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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * BeanShell plugin. Adds BeanShell console in the toolwindow.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
@State(
		name = BSFConsolePlugin.PLUGIN_NAME,
		storages = {@Storage(id = BSFConsolePlugin.PLUGIN_NAME, file = "$PROJECT_FILE$")}
)
public class BSFConsolePlugin implements ProjectComponent, Configurable, PersistentStateComponent<BSFConsoleConfig> {
	public static final String PLUGIN_NAME = "BSFConsole";

	private Project project;

	private BSFConsoleConfig config;
	private BSFConsoleConfigUI configUI;

	private final LanguageManager languageManager = new LanguageManager(this);
	private final ClassLoaderManager classLoaderManager = new ClassLoaderManager(this);
	private final ConsolesManager consolesManager = new ConsolesManager(this);

	public BSFConsolePlugin(final Project project) {
		this.project = project;
	}

	private void setupSecurityManager() {
		try {
			System.setSecurityManager(new BSFConsoleSecurityManager(System.getSecurityManager()));
		} catch (SecurityException e) {
			Messages.showMessageDialog("Can't set up security manager: " + e.getMessage(), "Error", Messages.getErrorIcon());
			Logger.getInstance("org.kos.bsfconsoleplugin.BSFConsolePlugin").error("Can't install custom security manager", e);
		}
	}

	@NotNull
	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	@NotNull
	public ConsolesManager getConsolesManager() {
		return consolesManager;
	}

	private void runStartupScripts() {
		for (final StartupScript startupScript : getConfig().getStartupScripts()) {
			consolesManager.newStartupScriptConsoleTab(startupScript);
		}
	}

	/**
	 * Component should dispose system resources or perform another cleanup in this method.
	 */
	@Override
	public void disposeComponent() {
	}

	/**
	 * Unique name of this component. If there is another component with the same name or
	 * name is null internal assertion will occur.
	 *
	 * @return the name of this component
	 */
	@NotNull
	@Override
	public String getComponentName() {
		return PLUGIN_NAME;
	}

	/**
	 * Component should do initialization and communication with another components in this method.
	 */
	@Override
	public void initComponent() {
		getLanguageManager().registerBSFLanguages();
		//addJarsToClassPathProperty();
		setupSecurityManager();
	}

	/**
	 * Invoked when the project corresponding to this component instance is closed.<p>
	 * Note that components may be created for even unopened projects and this method can be never
	 * invoked for a particular component intance (for example for default project).
	 */
	@Override
	public void projectClosed() {
		consolesManager.closeAllConsoles();
		consolesManager.unregisterToolWindow();
	}

	/**
	 * Invoked when the project corresponding to this component instance is opened.<p>
	 * Note that components may be created for even unopened projects and this method can be never
	 * invoked for a particular component intance (for example for default project).
	 */
	@Override
	public void projectOpened() {
		languageManager.init();
		consolesManager.initToolWindow();
		runStartupScripts();
	}

	public BSFConsoleConfig getConfig() {
		if (config == null) config = new BSFConsoleConfig();
		return config;
	}

	public Project getProject() {
		return project;
	}

	public BSFConsoleSearchOptions getSearchOptions() {
		return getConfig().getSearchOptions();
	}

	@Override
	public BSFConsoleConfig getState() {
		return config;
	}

	@Override
	public void loadState(final BSFConsoleConfig state) {
		config = state;
		activateConfig();
	}

	public ClassLoaderManager getClassLoaderManager() {
		return classLoaderManager;
	}

	//_______________________________________configurable implementation_______________________________________

	/**
	 * Store the settings from configurable to other components.
	 */
	@Override
	public void apply() throws ConfigurationException {
		config = new BSFConsoleConfig(configUI.getConfig());
		activateConfig();
	}

	private void activateConfig() {
		getLanguageManager().registerBSFLanguages();
	}

	@Override
	public JComponent createComponent() {
		configUI = new BSFConsoleConfigUI(project, this);
		return configUI.getContentPane();
	}

	@Override
	public void disposeUIResources() {
		configUI = null;
	}

	@Override
	public String getDisplayName() {
		return PLUGIN_NAME;
	}

	@Override
	public String getHelpTopic() {
		return null;
	}

	@Override
	public Icon getIcon() {
		return IconLoader.getIcon("/org/kos/bsfconsoleplugin/icons/bsf.png");
	}

	@Override
	public boolean isModified() {
		return !getConfig().equals(configUI.getConfig());
	}

	/**
	 * Load settings from other components to configurable.
	 */
	@Override
	public void reset() {
		configUI.setConfig(config);
	}

	//additional urls initialization

	static {
	}

}