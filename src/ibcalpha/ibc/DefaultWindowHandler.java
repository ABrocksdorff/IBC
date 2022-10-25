/*
 * Copyright (C) 2022 Brocksdorff <antonb@ath.mooo.com>
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
package ibcalpha.ibc;

import java.awt.Window;
import java.awt.event.WindowEvent;

/**
 *
 * @author Brocksdorff
 */
abstract class DefaultWindowHandler implements WindowHandler {

    @Override
    final public boolean filterEvent(final Window window, int eventId) {
        return true;
    }

    @Override
    final public void handleWindow(final Window window, int eventId) {
        switch (eventId) {
            case WindowEvent.WINDOW_OPENED: {
                onOpend(window);
                break;
            }
            case WindowEvent.WINDOW_CLOSED: {
                onClosed(window);
                break;
            }
            case WindowEvent.WINDOW_ACTIVATED: {
                onActivated(window);
                break;
            }
            default: {

            }
        }
    }

    public void onOpend(final Window window) {
    }

    public void onClosed(final Window window) {
    }

    public void onActivated(final Window window) {
    }

}
