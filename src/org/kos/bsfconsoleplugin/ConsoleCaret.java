package org.kos.bsfconsoleplugin;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;

/**
 * Fixes middle mouse button behaviour.
 */
public class ConsoleCaret extends DefaultCaret {
	@Override
	public void mouseClicked(final MouseEvent e) {
		boolean processed = false;
		final int nclicks = e.getClickCount(); //SwingUtilities2.getAdjustedClickCount(getComponent(), e);
		final JTextComponent component = getComponent();
		if (!e.isConsumed()) {
			if (SwingUtilities.isMiddleMouseButton(e)) {
				// mouse 2 behavior
				if (nclicks == 1 && component.isEditable() && component.isEnabled()
					/*&& SwingUtilities2.canEventAccessSystemClipboard(e)*/) {
					// paste system selection, if it exists
					final JTextComponent c = (JTextComponent) e.getSource();
					if (c != null) {
						for (Container parent = c.getParent(); parent != null; parent = parent.getParent()) {
							if (parent instanceof Console)
								((Console) parent).prepareForInput();
						}
						try {
							final Toolkit tk = c.getToolkit();
							final Clipboard buffer = tk.getSystemSelection();
							if (buffer != null) {
								// platform supports system selections, update it.
								//adjustCaret(e);
								final TransferHandler th = c.getTransferHandler();
								if (th != null) {
									Transferable trans = null;

									try {
										trans = buffer.getContents(null);
									} catch (IllegalStateException ise) {
										// clipboard was unavailable
										UIManager.getLookAndFeel().provideErrorFeedback(c);
									}

									if (trans != null) {
										th.importData(c, trans);
									}
								}
								component.requestFocus();
								processed = true;
							}
						} catch (HeadlessException he) {
							// do nothing... there is no system clipboard
						}
					}
				}
			}
		}
		if (!processed)
			super.mouseClicked(e);
	}
}
