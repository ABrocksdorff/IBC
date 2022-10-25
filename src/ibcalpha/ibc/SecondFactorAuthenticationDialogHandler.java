// This file is part of IBC.
// Copyright (C) 2004 Steven M. Kearns (skearns23@yahoo.com )
// Copyright (C) 2004 - 2021 Richard L King (rlking@aultan.com)
// Copyright (C) 2022 Brocksdorff <antonb@ath.mooo.com>
// For conditions of distribution and use, see copyright notice in COPYING.txt
// IBC is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// IBC is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with IBC.  If not, see <http://www.gnu.org/licenses/>.
package ibcalpha.ibc;

import java.awt.Window;
import java.time.Duration;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.Timer;

public class SecondFactorAuthenticationDialogHandler extends DefaultWindowHandler {

    private static final String WINDOWTITLE = "Second Factor Authentication";
    private static final String BUTTONTITLE = "Enter Read Only";
    private final Duration readOnlyDelay;
    private final Duration tradingLoginDelay;
    private final TradingLoginDialogHandler handler;
    private Timer timer;

    private SecondFactorAuthenticationDialogHandler() {
        this.handler = new TradingLoginDialogHandler();
        this.readOnlyDelay = Duration.ofSeconds(Settings.settings().getInt("DelayReadOnly", DefaultSettings.DELAY_READONLY));
        this.tradingLoginDelay = Duration.ofSeconds(Settings.settings().getInt("DelayTradingLogin", DefaultSettings.DELAY_TRADING_LOGIN));
    }

    static SecondFactorAuthenticationDialogHandler _secondFactorAuthenticationDialogHandler = new SecondFactorAuthenticationDialogHandler();

    static SecondFactorAuthenticationDialogHandler getInstance() {
        return _secondFactorAuthenticationDialogHandler;
    }

    /**
     *
     * @return
     */
    final public TradingLoginDialogHandler getHandler() {
        return handler;
    }

    @Override
    public void onClosed(Window window) {
        if (timer != null) {
            if (timer.isRunning()) {
                timer.stop();
                Utils.logToConsole(WINDOWTITLE + " timer stopped");
            }
            timer = null; //GC
        }
        if (LoginManager.loginManager().getLoginState() == LoginManager.LoginState.TWO_FA_IN_PROGRESS) {
            LoginManager.loginManager().secondFactorAuthenticationDialogClosed();
        }
    }

    @Override
    public void onOpend(Window window) {
        if (LoginManager.loginManager().getLoginState() == LoginManager.LoginState.LOGGED_IN) {
            //TradingLoginDialogHandler in read-only-mode
            handler.setCancel();
            Utils.logToConsole("Click " + WINDOWTITLE + "::" + StringConstants.CANCEL + " in " + tradingLoginDelay.toString());
            timer = new Timer((int) tradingLoginDelay.toMillis(), (e) -> {
                if (SwingUtils.clickButton(window, StringConstants.CANCEL)) {
                    Utils.logToConsole(WINDOWTITLE + "::" + StringConstants.CANCEL + " clicked");
                } else {
                    Utils.logError(WINDOWTITLE + "::" + StringConstants.CANCEL + " clicking failed");
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            if (LoginManager.loginManager().readonlyLoginRequired()) {
                //the stuff already there, just add delay to enter read-only-mode
                if (SwingUtils.findButton(window, BUTTONTITLE) == null) {
                    Utils.logError(WINDOWTITLE + " no " + BUTTONTITLE + " to be found, continue anyway");
                }
                Utils.logToConsole("Click " + WINDOWTITLE + "::" + BUTTONTITLE + " in " + readOnlyDelay.toString());
                timer = new Timer((int) readOnlyDelay.toMillis(), (e) -> {
                    doReadonlyLogin(window);
                });
                timer.setRepeats(false);
                timer.start();
            }
            if (secondFactorDeviceSelectionRequired(window)) {
                selectSecondFactorDevice(window);
            }
            LoginManager.loginManager().setLoginState(LoginManager.LoginState.TWO_FA_IN_PROGRESS);
        }
    }

    @Override
    public boolean recogniseWindow(Window window) {
        // For TWS this window is a JFrame; for Gateway it is a JDialog
        if (!(window instanceof JDialog || window instanceof JFrame)) {
            return false;
        }
        return SwingUtils.titleContains(window, WINDOWTITLE);
    }

    private void doReadonlyLogin(Window window) {
        if (SwingUtils.clickButton(window, BUTTONTITLE)) {
            Utils.logToConsole("initiating read-only login.");
        } else {
            Utils.logError("could not initiate read-only login.");
        }
    }

    private boolean secondFactorDeviceSelectionRequired(Window window) {
        // this area appears in the Second Factor Authentication dialog when the
        // user has enabled more than one second factor authentication method

        return (SwingUtils.findTextArea(window, "Select second factor device") != null);
    }

    private void selectSecondFactorDevice(Window window) {
        JList<?> deviceList = SwingUtils.findList(window, 0);
        if (deviceList == null) {
            Utils.exitWithError(ErrorCodes.ERROR_CODE_CANT_FIND_CONTROL, "could not find second factor device list.");
            return;
        }

        String secondFactorDevice = Settings.settings().getString("SecondFactorDevice", "");
        if (secondFactorDevice.length() == 0) {
            Utils.logError("You should specify the required second factor device using the SecondFactorDevice setting in config.ini");
            return;
        }

        ListModel<?> model = deviceList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            String entry = model.getElementAt(i).toString().trim();
            if (entry.equals(secondFactorDevice)) {
                deviceList.setSelectedIndex(i);

                if (!SwingUtils.clickButton(window, "OK")) {
                    Utils.logError("could not select second factor device: OK button not found");
                }
                return;
            }
        }
        Utils.logError("could not find second factor device '" + secondFactorDevice + "' in the list");
    }

}
