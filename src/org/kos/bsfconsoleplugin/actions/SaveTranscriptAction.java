/***************************************************************************
 *   Copyright (C) 2004 by Konstantin Sobolev                              *
 *   konstantin.sobolev@gmail.com                                                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 ***************************************************************************/

package org.kos.bsfconsoleplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.kos.bsfconsoleplugin.Console;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Action for saving console transcript to file.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class SaveTranscriptAction extends CurrentConsoleAction {
	@Override
	public void actionPerformed(final AnActionEvent e) {
		final Console console = getCurrentConsole(e);
		if (console == null)
			return;

		final String text = console.getText();
		final JFileChooser fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(console) != JFileChooser.APPROVE_OPTION)
			return;
		final File f = fileChooser.getSelectedFile();
		try {
			final FileWriter fw = new FileWriter(f);
			try {
				fw.write(text);
			} catch (IOException ex) {
				assert true;
			} finally {
				fw.close();
			}
		} catch (IOException ex) {
			assert true;
		}
		console.focus();
	}
}