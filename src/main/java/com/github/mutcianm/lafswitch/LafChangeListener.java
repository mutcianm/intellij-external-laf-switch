package com.github.mutcianm.lafswitch;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

class LafChangeListener implements AppLifecycleListener {

    private final Logger LOG = Logger.getInstance(LafChangeListener.class);

    @Override
    public void appStarting(@Nullable Project projectFromCommandLine) {
        try {
            LafSwitchService.getInstance().restartSocket();
        } catch (IOException e) {
            LOG.error("Error initialising the socket", e);
        }
    }

}