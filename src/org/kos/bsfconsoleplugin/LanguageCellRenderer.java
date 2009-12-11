/*
 * EnterpriseWizard
 *
 * Copyright (C) 2007 EnterpriseWizard, Inc. All Rights Reserved.
 *
 * $Id$
 * Created by Konstantin Sobolev (kos@supportwizard.com) on 07.11.2008$
 * Last modification $Date$
 */

package org.kos.bsfconsoleplugin;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

/**
 * A cell renderer from the languages list.
 *
 * @author <a href="mailto:kos@supportwizard.com" title="">Konstantin Sobolev</a>
 * @version $ Revision$
 */
public class LanguageCellRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		boolean strike = false;
		if (value instanceof Language) {
			final Language language = (Language) value;
			strike = ! language.isAvailable();
		}

		final Map attr = getFont().getAttributes();
		//noinspection unchecked
		attr.put(TextAttribute.STRIKETHROUGH, strike);
		//noinspection unchecked
		setFont(new Font(attr));

		return this;
	}
}
