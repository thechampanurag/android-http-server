/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.gui.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.controller.MainController;
import ro.polak.http.gui.ServerGui;
import ro.polak.http.impl.DefaultServerConfigFactory;

/**
 * Server CLI interface along with a runner.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public class DefaultCliServerGui implements ServerGui {

    private static final Logger LOGGER = Logger.getLogger(DefaultCliServerGui.class.getName());

    public void init() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT  - %4$s  -  %2$s  -  %5$s%6$s%n");

        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(Level.FINE);
        rootLog.getHandlers()[0].setLevel(Level.FINE);

        ServerGui gui = new DefaultCliServerGui();
        System.out.println("   __ __ ______ ______ ___    ____                         \n" +
                "  / // //_  __//_  __// _ \\  / __/___  ____ _  __ ___  ____\n" +
                " / _  /  / /    / /  / ___/ _\\ \\ / -_)/ __/| |/ // -_)/ __/\n" +
                "/_//_/  /_/    /_/  /_/    /___/ \\__//_/   |___/ \\__//_/   \n");
        System.out.println("https://github.com/piotrpolak/android-http-server");
        System.out.println("");
        MainController mainController = new MainController(getServerConfigFactory());
        mainController.setGui(gui);
        mainController.start();
    }

    protected DefaultServerConfigFactory getServerConfigFactory() {
        return new DefaultServerConfigFactory();
    }

    /**
     * The main CLI runner method.
     *
     * @param args
     */
    public static void main(String[] args) {
        (new DefaultCliServerGui()).init();
    }

    @Override
    public void stop() {
        LOGGER.info("The server has stopped.");
    }

    @Override
    public void start() {
        LOGGER.info("The server has started.");
    }
}
