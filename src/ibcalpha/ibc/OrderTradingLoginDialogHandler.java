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
import java.time.Duration;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.Timer;

/**
 * Handels the dialog that appears when we try to do trading related things in
 * read-only-mode.
 *
 * Right now, just closes the dialog by clicking Cancel after 10s.
 *
 * @author Brocksdorff
 */
final public class OrderTradingLoginDialogHandler extends DefaultWindowHandler {

    public static final String WINDOWTITLE = "IB Trader Workstation";
    private final Duration delay;
    private Timer timer;

    /**
     *
     */
    public OrderTradingLoginDialogHandler() {
        this.delay = Duration.ofSeconds(Settings.settings().getInt("DialogDismissDelay", DefaultSettings.DELAY_DIALOG_DISMISS));
    }

    @Override
    public void onOpend(Window window) {
        JButton button = SwingUtils.findButton(window, StringConstants.CANCEL);
        if (button != null) {
            Utils.logToConsole("Click " + WINDOWTITLE + "::" + StringConstants.CANCEL + " in " + delay.toString());
            timer = new Timer((int) delay.toMillis(), (e) -> {
                if (SwingUtils.clickButton(window, StringConstants.CANCEL)) {
                    Utils.logToConsole(WINDOWTITLE + " cancel trading login.");
                } else {
                    Utils.logError(WINDOWTITLE + " could not cancel trading login!");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    @Override
    public void onClosed(Window window) {
        if (timer != null) {
            if (timer.isRunning()) {
                timer.stop();
                Utils.logToConsole(WINDOWTITLE + " timer stopped");
            }
            timer = null;
        }
    }

    @Override
    public boolean recogniseWindow(Window window) {
        if (window instanceof JDialog) {
            return SwingUtils.titleContains(window, WINDOWTITLE);
        } else {
            return false;
        }
    }
}
