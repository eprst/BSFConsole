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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Panel with a list of commands history.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class RecentCommandsPanel extends JPanel {
	private Console console;
	private BSFConsolePlugin plugin;

	private JScrollPane scrollPane;
	private JList commandsList;

	private RecentCommandsListModel model;

	public RecentCommandsPanel(final Console console, final BSFConsolePlugin plugin) {
		this.console = console;
		this.plugin = plugin;

		init();
	}

	public JList getCommandsList() {
		return commandsList;
	}

	public void clear() {
		model.clear();
	}

	public boolean isEmpty() {
		return model.getSize() == 0;
	}

	public void appendCommandToHistory(final String command) {
		if (shouldntAddCommandToHistory(command))
			return;

		model.append(command);
		final int lastCellIndex = model.getSize() - 1;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				commandsList.setSelectedIndex(lastCellIndex);
				commandsList.ensureIndexIsVisible(lastCellIndex);
			}
		});
	}

	private void init() {
		model = new RecentCommandsListModel();
		commandsList = new JList(model);
		scrollPane = new JScrollPane(commandsList);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		commandsList.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(final MouseEvent e) {
				final int index = commandsList.locationToIndex(e.getPoint());
				if (index != -1) {
					commandsList.setSelectedIndex(index);
					scrollPane.scrollRectToVisible(commandsList.getCellBounds(index, index));
				}
			}
		});

		commandsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final int index = commandsList.locationToIndex(e.getPoint());
				if (index != -1 && commandsList.getCellBounds(index, index).contains(e.getPoint())) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							final String command = (String) model.getElementAt(index);
							if (command != null && console != null)
								console.simulateInput(command);
							else
								JOptionPane.showMessageDialog(null, command);
						}
					});
				}
			}
		});
	}

	private boolean shouldntAddCommandToHistory(final String command) {
		return command == null || command.trim().length() == 0 || command.lastIndexOf('\n') != -1 || command.lastIndexOf('\r') != -1 ||
		       command.equals(model.getLastElement()) ||
		       plugin != null && !plugin.getConfig().isStoreDupsInRecentCommands() && model.containsElement(command);
	}

	private class RecentCommandsListModel extends AbstractListModel {
		private ArrayList<String> history = new ArrayList<String>(50);

		/**
		 * Returns the length of the list.
		 *
		 * @return the length of the list
		 */
		@Override
		public int getSize() {
			return history.size();
		}

		/**
		 * Returns the value at the specified index.
		 *
		 * @param index the requested index
		 *
		 * @return the value at <code>index</code>
		 */
		@Override
		public Object getElementAt(final int index) {
			return history.get(index);
		}

		public void append(final String command) {
			final int index = getSize();
			history.add(command);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireIntervalAdded(this, index, index);
				}
			});
		}

		public void clear() {
			if (getSize() > 0) {
				final int last = getSize() - 1;
				history.clear();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireIntervalRemoved(this, 0, last);
					}
				});
			}
		}

		@Nullable
		public String getLastElement() {
			if (getSize() == 0)
				return null;
			return (String) getElementAt(getSize() - 1);
		}

		public boolean containsElement(final String command) {
			return history.contains(command);
		}
	}

	public static void main(final String[] args) {
		final RecentCommandsPanel panel = new RecentCommandsPanel(null, null);


		final JFrame frame = new JFrame("test");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel);
		frame.setSize(300, 300);
		frame.setLocation(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JTextField tf = new JTextField();
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				panel.appendCommandToHistory(tf.getText());
			}
		});
		frame.getContentPane().add(tf, BorderLayout.SOUTH);


		frame.setVisible(true);
	}
}