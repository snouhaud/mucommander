/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2008 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.desktop;

import com.mucommander.file.AbstractFile;

import java.io.IOException;
import java.awt.Desktop;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * @author Nicolas Rinaudo
 */
class InternalBrowse extends UrlOperation {
    // - Instance variables --------------------------------------------------
    // -----------------------------------------------------------------------
    /** Underlying desktop instance. */
    private Desktop desktop;



    // - Initialisation ------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Creates a new <code>InternalOpenUrl</code> instance.
     */
    public InternalBrowse() {
        if(Desktop.isDesktopSupported())
            desktop = Desktop.getDesktop();
    }



    // - BrowseOperation implementation --------------------------------------
    // -----------------------------------------------------------------------
    /**
     * Returns <code>true</code> if this operation is available.
     * <p>
     * This operation is available if:
     * <ul>
     *   <li>Desktops are supported by the current system (<code>Desktop.isDesktopSupported()</code> returns <code>true</code>).</li>
     *   <li>Browsing is supported by the desktop (<code>Desktop.isSupported(Desktop.Action.BROWSE)</code> returns <code>true</code>).</li>
     * </ul>
     * </p>
     * @return <code>true</code> if this operations is available, <code>false</code> otherwise.
     */
    public boolean isAvailable() {return desktop != null && desktop.isSupported(Desktop.Action.BROWSE);}

    /**
     * Opens the specified URL in the system's default browser.
     * @param  target      URL to browse.
     * @throws IOException if an error occured.
     */
    public void execute(URL url) throws IOException {
        // If java.awt.Desktop browsing is available, use it.
        if(isAvailable()) {
            try {desktop.browse(url.toURI());}
            catch(URISyntaxException e) {throw new IOException(e);}
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the action's label.
     * @return the action's label.
     */
    public String getName() {return "java.awt.Desktop open URL";}
}