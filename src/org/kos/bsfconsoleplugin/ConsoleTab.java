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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kos.bsfconsoleplugin.actions.SearchTranscriptAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

/**
 * Panel for the ToolWindow.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class ConsoleTab extends JPanel implements Disposable {
	public static final String TOOL_GROUP = "BSFConsole.ToolGroup";
	public static final String MENU_GROUP = "BSFConsole.MenuGroup";
	public static final String RECENT_COMMANDS_MENU_GROUP = "BSFConsole.RecentCommandsMenuGroup";

	private static final JPanel EMPTY_CONSOLE = new JPanel();

	private JComponent consolePane;

	private BSFConsolePlugin plugin;

	public ConsoleTab(final BSFConsolePlugin plugin) {
		this.plugin = plugin;

		setLayout(new BorderLayout());
		consolePane = EMPTY_CONSOLE;
		add(consolePane, BorderLayout.CENTER);

		final ActionManager actionManager = ActionManager.getInstance();
		((SearchTranscriptAction) actionManager.getAction("BSFConsole.SearchTranscript")).registerShortcuts(this);

		final DefaultActionGroup actionGroup = (DefaultActionGroup) actionManager.getAction(TOOL_GROUP);
		final ActionToolbar actionToolbar = actionManager.createActionToolbar(TOOL_GROUP, actionGroup, false);
		add(actionToolbar.getComponent(), BorderLayout.WEST);
	}

	@Nullable
	public BSFConsolePanel getConsolePanel() {
		if (consolePane instanceof BSFConsolePanel)
			return (BSFConsolePanel) consolePane;
		return null;
	}

	public boolean isEmpty() {
		return getConsolePanel() == null;
	}

	public void runStartupScript(final StartupScript startupScript) {
		final Language language = plugin.getLanguageManager().findLanguageByLabel(startupScript.languageLabel);
		if (language == null)
			return;
		final BSFConsolePanel panel = newConsole(language, false);
		if (panel == null)
			return;

		final String scriptName = startupScript.scriptFileName;
		if (scriptName == null || "".equals(scriptName.trim())) {
			panel.getInterpreter().start();
			return;
		}

		if (startupScript.scriptIsValid()) {
			final Runnable loader = new Runnable() {
				@Override
				public void run() {
					final File scriptFile = new File(scriptName);
					try {
						final String script = loadScript(scriptFile);
						panel.getInterpreter().exec(script);
					} catch (FileNotFoundException e) {
						panel.getConsole().asyncWaitAndError("startup script " + scriptName + " not found: " + e);
					} catch (IOException e) {
						panel.getConsole().asyncWaitAndError("error loading startup script: " + e);
					}

					panel.getInterpreter().start();
				}
			};

			new Thread(loader, "Startup Script " + scriptName).start(); //async to avoid deadlock
		}
	}

	private String loadScript(final File f) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(f));
		try {
			final StringBuilder sb = new StringBuilder();
			final char[] buf = new char[256];
			int num;
			while ((num = br.read(buf)) != -1)
				sb.append(buf, 0, num);
			return sb.toString();
		} finally {
			br.close();
		}
	}

	@Nullable
	public BSFConsolePanel newConsole(final Language language, final boolean start) {
		if (!isEmpty()) throw new IllegalStateException("not empty");

		try {
			final BSFConsolePanel panel = new BSFConsolePanel(plugin, language, start);
			consolePane = panel;
			remove(EMPTY_CONSOLE);
			add(panel, BorderLayout.CENTER);

			registerContextMenu(panel.getConsole().getTextPane(), MENU_GROUP);
			registerContextMenu(panel.getRecentCommandsPanel().getCommandsList(), RECENT_COMMANDS_MENU_GROUP);

			return panel;
		} catch (InterpreterInstantiationException e) {
			//LOG.debug(e);
			Messages.showMessageDialog("Error loading engine: " + e.getMessage(), "Error loading engine", Messages.getErrorIcon());
			//e.printStackTrace();
			//JOptionPane.showMessageDialog(null, "error loading engine: " + e);
			return null;
		}
	}

	private void registerContextMenu(final JComponent component, final String menuGroupID) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					final ActionGroup menuGroup = (ActionGroup) ActionManager.getInstance().getAction(menuGroupID);
					final ActionPopupMenu popup = ActionManager.getInstance().createActionPopupMenu(BSFConsolePlugin.PLUGIN_NAME, menuGroup);
					popup.getComponent().show(component, e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void dispose() {
		final BSFConsolePanel panel = getConsolePanel();
		if (panel != null) panel.close();
	}

	@NotNull
	public String getLanguageName() {
		final BSFConsolePanel panel = getConsolePanel();
		if (panel == null) return "";
		return panel.getLanguageName();
	}
}