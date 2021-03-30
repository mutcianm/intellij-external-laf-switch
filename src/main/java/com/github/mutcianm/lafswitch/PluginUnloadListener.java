package com.github.mutcianm.lafswitch;

import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PluginUnloadListener implements DynamicPluginListener {

    private final Logger LOG = Logger.getInstance(PluginUnloadListener.class);
    private final static String MyPluginID = "com.github.mutcianm.external-laf-switch";

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        PluginId pluginId = pluginDescriptor.getPluginId();
        if (pluginId != null && MyPluginID.equals(pluginId.toString())) {
            try {
                LafSwitchService.getInstance().terminateListener();
            } catch (IOException e) {
                LOG.error("Failed to terminate listener socket during plugin unload", e);
            }
        }
    }

    @Override
    public void pluginLoaded(@NotNull IdeaPluginDescriptor pluginDescriptor) {
        PluginId pluginId = pluginDescriptor.getPluginId();
        if (pluginId != null && MyPluginID.equals(pluginId.toString())) {
            try {
                LafSwitchService.getInstance().restartSocket();
            } catch (IOException e) {
                LOG.error("Failed initializing listener socket during plugin loading", e);
            }
        }
    }
}
