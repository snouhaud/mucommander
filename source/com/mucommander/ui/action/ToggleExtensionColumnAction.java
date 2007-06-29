/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (c) 2002-2007 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with muCommander; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.mucommander.ui.action;

import com.mucommander.text.Translator;
import com.mucommander.ui.MainFrame;
import com.mucommander.ui.table.Columns;
import com.mucommander.ui.table.FileTable;

import java.util.Hashtable;

/**
 * Shows/hides the 'Extension' column of the currently active FileTable. If the column is currently visible, this will
 * hide it and vice-versa.
 *
 * @author Maxence Bernard
 */
public class ToggleExtensionColumnAction extends MucoAction {

    public ToggleExtensionColumnAction(MainFrame mainFrame, Hashtable properties) {
        super(mainFrame, properties, false);
        setLabel(Translator.get("extension"));
    }

    public void performAction() {
        FileTable activeTable = mainFrame.getActiveTable();
        activeTable.setColumnVisible(Columns.EXTENSION, !activeTable.isColumnVisible(Columns.EXTENSION));
    }
}
