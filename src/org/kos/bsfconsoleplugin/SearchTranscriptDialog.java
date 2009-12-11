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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ListIterator;

/**
 * Dialog for searching transcript.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class SearchTranscriptDialog extends DialogWrapper {
	private JPanel contentPanel;
	private JComboBox textToFindCombo;
	private JRadioButton fromCursorRadioButton;
	private JRadioButton entireScopeRadioButton;

	private BSFConsoleSearchOptions searchOptions;

	public SearchTranscriptDialog(final BSFConsoleSearchOptions searchOptions, final Project project, final boolean canBeParent) {
		super(project, canBeParent);

		this.searchOptions = searchOptions;

		final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(fromCursorRadioButton);
		buttonGroup.add(entireScopeRadioButton);

		if (searchOptions.searchFromCursor)
			fromCursorRadioButton.setSelected(true);
		else
			entireScopeRadioButton.setSelected(true);
		
		getOKAction().setEnabled(false);

		textToFindCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				final Object selectedItem = textToFindCombo.getSelectedItem();
				setOKActionEnabled(selectedItem != null && selectedItem.toString().length() > 0);
			}
		});

		textToFindCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				final Object selectedItem = textToFindCombo.getEditor().getItem();
				final boolean notEmpty = selectedItem != null && selectedItem.toString().length() > 0;
				final boolean ok = notEmpty && e.getKeyCode() == KeyEvent.VK_ENTER;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setOKActionEnabled(notEmpty);
						if (ok && notEmpty)
							doOKAction();
					}
				});
			}
		});

		final List<String> recentSearches = searchOptions.recentSearches;
		final int size = recentSearches.size();
		
		if (size > 0) {
			for (ListIterator<String> it = recentSearches.listIterator(size); it.hasPrevious();)
				textToFindCombo.addItem(it.previous());
			textToFindCombo.setSelectedIndex(0);
		} else
			setOKActionEnabled(false);

		setTitle("Find Text");
		init();
	}

	public String getText() {
		final Object selectedItem = textToFindCombo.getSelectedItem();
		return selectedItem == null ? "" : selectedItem.toString();
	}

	public boolean isFromCursor() {
		return fromCursorRadioButton.isSelected();
	}

	/**
	 * Factory method. It creates panel with dialog options. Options panel is located at the
	 * center of the dialog's content pane. The implementation can return <code>null</code>
	 * value. In this case there will be no options panel.
	 */
	@Override
	protected JComponent createCenterPanel() {
		return contentPanel;
	}

	/**
	 * @return component which should be focused when the the dialog appears
	 *         on the screen.
	 */
	@Override
	public JComponent getPreferredFocusedComponent() {
		return textToFindCombo;
	}

	/**
	 * This method is invoked by default implementation of "OK" action. It just closes dialog
	 * with <code>OK_EXIT_CODE</code>. This is convenient place to override functionality of "OK" action.
	 * Note that the method does nothing if "OK" action isn't enabled.
	 */
	@Override
	protected void doOKAction() {
		if (getText().length() == 0) {
			Messages.showMessageDialog(
					contentPanel,
					"Text to find must not be empty",
					"Error",
					Messages.getErrorIcon()
			);
//			JOptionPane.showMessageDialog(contentPanel,
//					"Text to find must not be empty",
//					"Error",
//					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final String text = getText();
		if (text != null && !searchOptions.recentSearches.contains(text))
			searchOptions.addRecentSearch(text);

		searchOptions.searchFromCursor = isFromCursor();

		super.doOKAction();
	}
}