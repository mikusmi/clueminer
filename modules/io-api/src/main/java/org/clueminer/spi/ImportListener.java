/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.spi;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface ImportListener extends EventListener {

    /**
     * Called when importer or some of its parameters were changed
     *
     * @param importer
     * @param importerUI
     */
    void importerChanged(Importer importer, ImporterUI importerUI);

    void dataLoaded();

}
