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
import javax.swing.JDialog;

/**
 * Handels the Trading Login dialog when switching from read-only-mode to
 * trading mode.
 *
 * It is a sub handler to 2FA so after trigger 2FA it switches to cancel mode.
 *
 * @author Brocksdorff
 */
final public class TradingLoginDialogHandler extends DefaultWindowHandler {

    public static final String WINDOWTITLE = "Trading Login";
    private boolean doCancel;

    /**
     *
     */
    public TradingLoginDialogHandler() {
        this.doCancel = false;
    }

    /**
     *
     */
    public void setCancel() {
        Utils.logToConsole(WINDOWTITLE + " switch to cancel mode");
        doCancel = true;
    }

    @Override
    public void onActivated(Window window) {
        if (doCancel) {
            if (SwingUtils.clickButton(window, StringConstants.CANCEL)) {
                Utils.logToConsole(WINDOWTITLE + "::" + StringConstants.CANCEL + " clicked");
            } else {
                Utils.logError(WINDOWTITLE + "::" + StringConstants.CANCEL + " clicking failed");
            }
            Utils.logToConsole(WINDOWTITLE + " switch to main mode");
            doCancel = false;
        } else {
            Utils.logToConsole(WINDOWTITLE + " filling in password ...");
            SwingUtils.setTextField(window, 1, LoginManager.loginManager().IBAPIPassword());

            if (SwingUtils.clickButton(window, StringConstants.LOG_IN)) {
                Utils.logToConsole(WINDOWTITLE + " initiating trading login");
            } else {
                Utils.logError(WINDOWTITLE + " could not initiate trading login");
            }
        }
    }

    @Override
    public boolean recogniseWindow(Window window) {
        if (window instanceof JDialog) {
            return SwingUtils.titleEquals(window, WINDOWTITLE);
        } else {
            return false;
        }
    }

}
