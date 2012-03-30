package org.kos.bsfconsoleplugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.application.PathManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Plugin configuration UI.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class BSFConsoleConfigUI {
	//private static final Logger LOG = Logger.getInstance("org.kos.bsfconsoleplugin.BSFConsoleConfigUI");
	private JPanel contentPane;
	private JList bsfLanguagesList;
	private JButton addLanguageButton;
	private JButton removeLanguageButton;
	private JButton resetLanguagesButton;
	private JLabel engineClassNameLabel;

	private JTable startupScriptsTable;
	private JButton addStartupScriptButton;
	private JButton removeStartupScriptButton;
	private JButton moveStartupScriptUpButton;
	private JButton moveStartupScriptDownButton;

	private BSFLanguagesListModel bsfLanguagesListModel;
	private StartupScriptsTableModel startupScriptsTableModel;
	private AvailableLanguagesComboboxModel availableLanguagesComboboxModel;

	private JCheckBox restoreSystemStreamsCheckbox;
	private JCheckBox outWaitsForErrCheckbox;
	private JCheckBox errWaitsForOutCheckbox;
	private JCheckBox storeDupsInRecentCommandsCheckbox;
	private JComboBox useCasslathOfModuleComboBox;
	private JCheckBox includeOutputPathCheckbox;
	private JCheckBox includeTestsOutputPathCheckbox;
	private JCheckBox hideExceptionStacktracesCheckbox;
	private JLabel helpLabel;
	private JTable unavailableLanguagesTable;

	private BSFConsoleConfig config;
	private Project project;
	private final BSFConsolePlugin plugin;

	public BSFConsoleConfigUI(final Project project, final BSFConsolePlugin plugin) {
		this.project = project;
		this.plugin = plugin;


		//_______________________________________buttons_______________________________________

		addLanguageButton.setIcon(IconLoader.getIcon("/general/add.png"));
		removeLanguageButton.setIcon(IconLoader.getIcon("/general/remove.png"));
		resetLanguagesButton.setIcon(IconLoader.getIcon("/actions/sync.png"));

		addStartupScriptButton.setIcon(IconLoader.getIcon("/general/add.png"));
		removeStartupScriptButton.setIcon(IconLoader.getIcon("/general/remove.png"));
		moveStartupScriptUpButton.setIcon(IconLoader.getIcon("/actions/moveUp.png"));
		moveStartupScriptDownButton.setIcon(IconLoader.getIcon("/actions/moveDown.png"));

		final JButton[] buttons = new JButton[]{
				addLanguageButton, removeLanguageButton, resetLanguagesButton,
				addStartupScriptButton, removeStartupScriptButton, moveStartupScriptUpButton, moveStartupScriptDownButton
		};

		for (final JButton button : buttons) {
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setFocusPainted(false);
		}

		setConfig(plugin.getConfig());

		initFirstTab(project);
		initSecondTab(plugin);
		initThirdTab(project);
		initForthTab(plugin);
	}

	private void initFirstTab(final Project project) {
		bsfLanguagesListModel = new BSFLanguagesListModel();
		bsfLanguagesList.setModel(bsfLanguagesListModel);
		bsfLanguagesList.setCellRenderer(new LanguageCellRenderer());
		engineClassNameLabel.setText(" ");

		final String pluginsPath = PathManager.getPluginsPath();
		if (pluginsPath == null)
			helpLabel.setText("To add new languages put their jars into plugin's lib dir");
		else {
			final String fileSep = System.getProperty("file.separator");
			final StringBuilder sb = new StringBuilder(pluginsPath);
			sb.append(fileSep).append(BSFConsolePlugin.PLUGIN_NAME).append(fileSep).append("lib");
			helpLabel.setText("To add new languages put their jars into " + sb);
		}

		addLanguageButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final NewLanguageDialog newLanguageDialog = new NewLanguageDialog(project, false);
						newLanguageDialog.show();
						if (newLanguageDialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
							final String languageName = newLanguageDialog.getLanguageName();
							final String engineClassName = newLanguageDialog.getEngineClassName();
							getConfig().getBsfLanguagesRegistry().addBSFLanguage(new BSFLanguage(languageName, engineClassName));
							bsfLanguagesListModel.fireChange();
						}
					}
				}
		);

		removeLanguageButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int[] selection = bsfLanguagesList.getSelectedIndices();
						for (final int aSelection : selection)
							getConfig().getBsfLanguagesRegistry().removeBSFLanguage(aSelection);
						bsfLanguagesListModel.fireChange();
						startupScriptsTableModel.fireChange();
						availableLanguagesComboboxModel.fireChange();
					}
				}
		);

		resetLanguagesButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						config.resetBSFLanguages();
						bsfLanguagesListModel.fireChange();
						startupScriptsTableModel.fireChange();
						availableLanguagesComboboxModel.fireChange();
					}
				}
		);

		bsfLanguagesList.addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent e) {
						removeLanguageButton.setEnabled(bsfLanguagesList.getSelectedIndices().length > 0);
					}
				}
		);

		bsfLanguagesList.addMouseMotionListener(
				new MouseMotionAdapter() {
					@Override
					public void mouseMoved(final MouseEvent e) {
						final int index = bsfLanguagesList.locationToIndex(e.getPoint());
						if (index == -1 || !bsfLanguagesList.getCellBounds(index, index).contains(e.getPoint())) {
							engineClassNameLabel.setText(" ");
							return;
						}

						final BSFLanguage language = getConfig().getBsfLanguagesRegistry().getBsfLanguages().get(index);
						engineClassNameLabel.setText("Requires " + language.engineClassName);
					}
				}
		);

		bsfLanguagesList.addMouseListener(
				new MouseListener() {
					@Override
					public void mouseEntered(final MouseEvent e) {
					}

					@Override
					public void mousePressed(final MouseEvent e) {
					}

					@Override
					public void mouseReleased(final MouseEvent e) {
					}

					@Override
					public void mouseClicked(final MouseEvent e) {
					}

					@Override
					public void mouseExited(final MouseEvent e) {
						engineClassNameLabel.setText(" ");
					}
				}
		);
	}

	private void initSecondTab(final BSFConsolePlugin plugin) {
		startupScriptsTableModel = new StartupScriptsTableModel();
		startupScriptsTable.setModel(startupScriptsTableModel);
		availableLanguagesComboboxModel = new AvailableLanguagesComboboxModel();
		startupScriptsTable.setDefaultEditor(
				Object.class,
				new DefaultCellEditor(new JComboBox(availableLanguagesComboboxModel))
		);
		startupScriptsTable.setDefaultEditor(
				ScriptFile.class,
				new FileChooserCellEditorAndRenderer(new JTextField())
		);

		startupScriptsTable.setDefaultRenderer(
				ScriptFile.class,
				new FileChooserCellEditorAndRenderer(new JTextField())
		);

		startupScriptsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent e) {
						final int[] selectedRows = startupScriptsTable.getSelectedRows();
						final int rowsSelected = selectedRows.length;
						removeStartupScriptButton.setEnabled(rowsSelected > 0);
						moveStartupScriptUpButton.setEnabled(rowsSelected == 1 && selectedRows[0] > 0);
						moveStartupScriptDownButton.setEnabled(
								rowsSelected == 1 && selectedRows[0] < startupScriptsTableModel.getRowCount() - 1
						);
					}
				}
		);

		addStartupScriptButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						getConfig().addStartupScript(
								new StartupScript(plugin.getLanguageManager().getAvailableLanguages().iterator().next().getLabel(), null)
						);
						startupScriptsTableModel.fireChange();
					}
				}
		);

		removeStartupScriptButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int[] selectedRows = startupScriptsTable.getSelectedRows();
						if (selectedRows.length == 0)
							return;
						for (final int selectedRow : selectedRows)
							getConfig().removeStartupScript(selectedRow);
						startupScriptsTableModel.fireChange();
					}
				}
		);

		moveStartupScriptUpButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						moveSelectedStartupScript(-1);
					}
				}
		);

		moveStartupScriptDownButton.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						moveSelectedStartupScript(1);
					}
				}
		);
	}

	private void initThirdTab(final Project project) {
		restoreSystemStreamsCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						BSFConsoleConfigUI.this.config.setRestoreSystemStreams(restoreSystemStreamsCheckbox.isSelected());
					}
				}
		);

		storeDupsInRecentCommandsCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						BSFConsoleConfigUI.this.config.setStoreDupsInRecentCommands(storeDupsInRecentCommandsCheckbox.isSelected());
					}
				}
		);

		outWaitsForErrCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final boolean selected = outWaitsForErrCheckbox.isSelected();
						BSFConsoleConfigUI.this.config.setOutWaitsForErr(selected);
						if (selected && selected) {
							BSFConsoleConfigUI.this.config.setErrWaitsForOut(false);
							errWaitsForOutCheckbox.setSelected(false);
						}
					}
				}
		);

		errWaitsForOutCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final boolean selected = errWaitsForOutCheckbox.isSelected();
						BSFConsoleConfigUI.this.config.setErrWaitsForOut(selected);
						if (selected && selected) {
							BSFConsoleConfigUI.this.config.setOutWaitsForErr(false);
							outWaitsForErrCheckbox.setSelected(false);
						}
					}
				}
		);


		final Map<String, Module> namesToModules = getNamesToModules(project);
		final List<String> moduleNames = new ArrayList<String>(namesToModules.keySet());
		moduleNames.add("");
		useCasslathOfModuleComboBox.setModel(new DefaultComboBoxModel(moduleNames.toArray(new String[moduleNames.size()])));

		useCasslathOfModuleComboBox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final Object selectedItem = useCasslathOfModuleComboBox.getSelectedItem();
						final String selectedItemStr = selectedItem == null ? null : selectedItem.toString();
						if (selectedItem == null)
							BSFConsoleConfigUI.this.config.setModuleForClasspath("");
						else
							BSFConsoleConfigUI.this.config.setModuleForClasspath(selectedItemStr);

						enableAndCheck(includeOutputPathCheckbox, isOutputPathValid(selectedItemStr, namesToModules, false));
						enableAndCheck(includeTestsOutputPathCheckbox, isOutputPathValid(selectedItemStr, namesToModules, true));
					}
				}
		);


		includeOutputPathCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						BSFConsoleConfigUI.this.config.setIncludeOutputPath(includeOutputPathCheckbox.isSelected());
					}
				}
		);

		includeTestsOutputPathCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						BSFConsoleConfigUI.this.config.setIncludeTestsOutputPath(includeTestsOutputPathCheckbox.isSelected());
					}
				}
		);

		hideExceptionStacktracesCheckbox.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						BSFConsoleConfigUI.this.config.setHideExceptionStacktraces(hideExceptionStacktracesCheckbox.isSelected());
					}
				}
		);
	}

	private Map<String, Module> getNamesToModules(final Project project) {
		final ModuleManager mmgr = ModuleManager.getInstance(project);
		final Module[] modules = mmgr.getModules();
		final HashMap<String, Module> namesToModules = new HashMap<String, Module>(modules.length);
		for (final Module module : modules)
			namesToModules.put(module.getName(), module);
		return namesToModules;
	}

	private void initForthTab(final BSFConsolePlugin plugin) {
		final DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Name");
		tableModel.addColumn("Reason");

		final List<Language> languages = plugin.getLanguageManager().getAllLanguages();
		for (final Language language : languages) {
			if (!language.isAvailable())
				tableModel.addRow(new Object[]{
						language.getLabel(),
						language.whyNotAvailable()
				});
		}

		unavailableLanguagesTable.setModel(tableModel);
		unavailableLanguagesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		final TableColumnModel cm = unavailableLanguagesTable.getColumnModel();
		for (int col = 0; col < cm.getColumnCount(); col++) {
			double maxw = 0;
			final TableCellRenderer r = unavailableLanguagesTable.getDefaultRenderer(tableModel.getColumnClass(col));
			for (int row = 0; row < tableModel.getRowCount(); row++) {
				final Component comp = r.getTableCellRendererComponent(unavailableLanguagesTable, tableModel.getValueAt(row, col), false, false, row, col);
				final double w = comp.getPreferredSize().getWidth();
				if (w > maxw) maxw = w;
			}
			cm.getColumn(col).setPreferredWidth((int) maxw + 3);
		}
	}

	private boolean isOutputPathValid(final String moduleName, final Map<String, Module> namesToModules, final boolean testOutputPath) {
		if (moduleName == null || moduleName.length() == 0)
			return false;
		final Module m = namesToModules.get(moduleName);
		if (m == null)
			return false;

		final File f = new File(CompilerOutputPaths.getModuleOutputPath(m, testOutputPath));
		if (!f.exists() || !f.canRead() || !f.isDirectory())
			return false;
		if (!testOutputPath)
			return true;

		return !CompilerOutputPaths.getModuleOutputPath(m, false).equals(CompilerOutputPaths.getModuleOutputPath(m, true));
	}

	private void enableAndCheck(final JCheckBox checkBox, final boolean enabled) {
		checkBox.setEnabled(enabled);
		//if (!enabled)
		//	checkBox.setSelected(false);
		//LOG.debug("set checkbox " +checkBox.getName()+" state to "+enabled);
	}

	private void moveSelectedStartupScript(final int increment) {
		final int index = startupScriptsTable.getSelectedRows()[0];
		final int newIndex = index + increment;
		final ArrayList<StartupScript> startupScripts = getConfig().getStartupScripts();
		final StartupScript tmp = startupScripts.get(index);
		startupScripts.set(index, startupScripts.get(newIndex));
		startupScripts.set(newIndex, tmp);
		startupScriptsTableModel.fireChange();
		startupScriptsTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
	}

	public BSFConsoleConfig getConfig() {
		return config;
	}

	public void setConfig(final BSFConsoleConfig config) {
		this.config = new BSFConsoleConfig(config);

		restoreSystemStreamsCheckbox.setSelected(config.isRestoreSystemStreams());
		outWaitsForErrCheckbox.setSelected(config.isOutWaitsForErr());
		errWaitsForOutCheckbox.setSelected(config.isErrWaitsForOut());
		storeDupsInRecentCommandsCheckbox.setSelected(config.isStoreDupsInRecentCommands());
		includeOutputPathCheckbox.setSelected(config.isIncludeOutputPath());
		includeTestsOutputPathCheckbox.setSelected(config.isIncludeTestsOutputPath());
		useCasslathOfModuleComboBox.setSelectedItem(config.getModuleForClasspath());
		hideExceptionStacktracesCheckbox.setSelected(config.isHideExceptionStacktraces());

		final Map<String, Module> namesToModules = getNamesToModules(project);
		enableAndCheck(includeOutputPathCheckbox, isOutputPathValid(config.getModuleForClasspath(), namesToModules, false));
		enableAndCheck(includeTestsOutputPathCheckbox, isOutputPathValid(config.getModuleForClasspath(), namesToModules, true));

		if (bsfLanguagesListModel != null)
			bsfLanguagesListModel.fireChange();

		if (startupScriptsTableModel != null)
			startupScriptsTableModel.fireChange();
	}

	public JComponent getContentPane() {
		return contentPane;
	}

	private class BSFLanguagesListModel extends AbstractListModel {
		@Override
		public int getSize() {
			return config.getBsfLanguagesRegistry().getBsfLanguages().size();
		}

		@Override
		public Object getElementAt(final int index) {
			return config.getBsfLanguagesRegistry().getBsfLanguages().get(index);
		}

		public void fireChange() {
			fireContentsChanged(this, 0, getSize() - 1);
		}
	}

	private class AvailableLanguagesComboboxModel extends AbstractListModel implements ComboBoxModel {
		private Object selectedItem;

		@Override
		public int getSize() {
			return plugin.getLanguageManager().getAvailableLanguages().size();
		}

		@Override
		public Object getElementAt(final int index) {
			return plugin.getLanguageManager().getAvailableLanguages().get(index);
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public void setSelectedItem(final Object anItem) {
			selectedItem = anItem;
		}

		public void fireChange() {
			fireContentsChanged(this, 0, getSize() - 1);
		}
	}

	private class StartupScriptsTableModel extends AbstractTableModel {
		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return config.getStartupScripts().size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			final StartupScript startupScript = config.getStartupScripts().get(rowIndex);
			if (columnIndex == 0)
				return startupScript.languageLabel;
			return new ScriptFile(startupScript);
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
			return columnIndex == 0 ? String.class : ScriptFile.class;
		}

		@Override
		public String getColumnName(final int column) {
			return column == 0 ? "Language" : "Script File";
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			final StartupScript ss = config.getStartupScripts().get(rowIndex);
			if (columnIndex == 0)
				ss.languageLabel = aValue == null ? "" : ((Language) aValue).getLabel();
			else
				ss.scriptFileName = aValue == null ? "" : aValue.toString();

		}

		public void fireChange() {
			fireTableDataChanged();
		}
	}

	private class ScriptFile extends File {
		ScriptFile(final StartupScript startupScript) {
			super(startupScript.scriptFileName == null ? "" : startupScript.scriptFileName);
		}

		@Override
		public String toString() {
			if (!isFile())
				return "";
			try {
				return getCanonicalPath();
			} catch (IOException e) {
				return getAbsolutePath();
			}
		}
	}

	private class FileChooserCellEditorAndRenderer extends DefaultCellEditor implements TableCellRenderer, ActionListener {
		private final JPanel component;
		private final JButton fileChooserButton;

		FileChooserCellEditorAndRenderer(final JTextField textField) {
			super(textField);
			component = new JPanel(new BorderLayout());
			fileChooserButton = new JButton(IconLoader.getIcon("/general/ellipsis.png"));
			fileChooserButton.setMargin(new Insets(0, 0, 0, 0));
			fileChooserButton.addActionListener(this);
			resetComponent();
		}

		private void resetComponent() {
			component.removeAll();
			component.add(fileChooserButton, BorderLayout.EAST);
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
			final Component line = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			resetComponent();
			component.add(line, BorderLayout.CENTER);
			return component;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object value = delegate.getCellEditorValue();

//			final File f = new File(value == null ? "" : value.toString());
//			final JFileChooser fileChooser = new JFileChooser(f.getParentFile());
//			fileChooser.setFileFilter(new FileFilter() {
//				public boolean accept(File f) {
//					return f.exists() && f.canRead();
//				}
//
//				public String getDescription() {
//					return null;
//				}
//			});
//			final int retVal = fileChooser.showOpenDialog(component);
//			if (retVal == JFileChooser.APPROVE_OPTION) {
//				try {
//					delegate.setValue(fileChooser.getSelectedFile().getCanonicalPath());
//				} catch (IOException e1) {
//					delegate.setValue(fileChooser.getSelectedFile().getAbsolutePath());
//				}
//				super.fireEditingStopped();
//			} else
//				super.fireEditingCanceled();

			final FileChooserDescriptor fcd = new FileChooserDescriptor(true, false, false, false, false, false);

			VirtualFile f = null;
			if (value != null)
				f = LocalFileSystem.getInstance().findFileByPath(value.toString());

			final VirtualFile[] vf = FileChooser.chooseFiles(project, fcd, f);
			if (vf.length == 1 && vf[0].isValid()) {
				delegate.setValue(vf[0].getPath());
				super.fireEditingStopped();
			} else
				super.fireEditingCanceled();
		}

		@Override
		public boolean isCellEditable(final EventObject anEvent) {
			if (super.isCellEditable(anEvent))
				return true;
			if (anEvent instanceof MouseEvent) {
				final MouseEvent e = (MouseEvent) anEvent;
				final int row = startupScriptsTable.rowAtPoint(e.getPoint());
				final int column = startupScriptsTable.columnAtPoint(e.getPoint());
				final TableCellEditor cellEditor = startupScriptsTable.getCellEditor(row, column);
				if (cellEditor instanceof FileChooserCellEditorAndRenderer) {
					//awful hack with static data. Who knows how to do it in a proper way?
					return startupScriptsTable.getCellRect(row, column, true).getMaxX() - e.getX() <= fileChooserButton.getPreferredSize().getWidth();
				}
			}
			return false;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			final JLabel label = new JLabel();
			if (isSelected) {
				component.setForeground(table.getSelectionForeground());
				component.setBackground(table.getSelectionBackground());
			} else {
				component.setForeground(table.getForeground());
				component.setBackground(table.getBackground());
			}
			label.setFont(table.getFont());
			if (hasFocus) {
				component.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
				if (table.isCellEditable(row, column)) {
					component.setForeground(UIManager.getColor("Table.focusCellForeground"));
					component.setBackground(UIManager.getColor("Table.focusCellBackground"));
				}
			} else {
				component.setBorder(new EmptyBorder(1, 2, 1, 2));
			}
			label.setText(value == null ? "" : value.toString());

			resetComponent();
			component.add(label, BorderLayout.CENTER);
			return component;
		}
	}

	public static void main(final String[] args) {
		final BSFConsoleConfig _config = new BSFConsoleConfig();
		_config.resetBSFLanguages();
		_config.addStartupScript(new StartupScript("[BSF] ant", "/home/kos/sw2/java/build/build.xml"));

		final JFrame frame = new JFrame();
		frame.setBounds(300, 300, 600, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		//broken
		//frame.getContentPane().add(new BSFConsoleConfigUI(null, _config).getContentPane(), BorderLayout.CENTER);
		frame.setVisible(true);
	}
}