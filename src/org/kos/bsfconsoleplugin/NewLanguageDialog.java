package org.kos.bsfconsoleplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * New language dialog.
 * 
 * @author <a href="mailto:konstantin.sobolev@gmail.com" title="">Konstantin Sobolev</a>
 */
public class NewLanguageDialog extends DialogWrapper {
	private JTextField languageNameTextField;
	private JTextField engineClassNameTextField;
	private JPanel centerPanel;

	public NewLanguageDialog(final Project project, final boolean canBeParent) {
		super(project, canBeParent);
		getOKAction().setEnabled(false);

		final DocumentAdapter documentAdapter = new DocumentAdapter() {
			@Override
			protected void textChanged(final DocumentEvent documentEvent) {
				final boolean bothFiedsAreNotEmpty = languageNameTextField.getText().trim().length() > 0 &&
						engineClassNameTextField.getText().trim().length() > 0;
				setOKActionEnabled(bothFiedsAreNotEmpty);
			}
		};

		languageNameTextField.getDocument().addDocumentListener(documentAdapter);
		//todo use TreeClassChooserDialog?
		engineClassNameTextField.getDocument().addDocumentListener(documentAdapter);


		setTitle("Add New BSF Language");
		init();
	}

	/**
	 * @return component which should be focused when the the dialog appears
	 *         on the screen.
	 */
	@Override
	public JComponent getPreferredFocusedComponent() {
		return languageNameTextField;
	}

	public String getLanguageName() {
		return languageNameTextField.getText();
	}

	public String getEngineClassName() {
		return engineClassNameTextField.getText();
	}

	/**
	 * Factory method. It creates panel with dialog options. Options panel is located at the
	 * center of the dialog's content pane. The implementation can return <code>null</code>
	 * value. In this case there will be no options panel.
	 */
	@Override
	protected JComponent createCenterPanel() {
		return centerPanel;
	}
}