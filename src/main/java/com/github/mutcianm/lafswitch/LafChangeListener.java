package com.github.mutcianm.lafswitch;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;

class LafChangeListener implements AppLifecycleListener {

    private final Logger LOG = Logger.getInstance(LafChangeListener.class);

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void appStarted() {
        try {
            LafSwitchService.getInstance().restartSocket();
        } catch (IOException e) {
            LOG.error("Error initialising the socket", e);
        }
    }

}