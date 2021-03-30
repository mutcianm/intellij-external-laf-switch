package com.github.mutcianm.lafswitch.settings;

import com.github.mutcianm.lafswitch.LafSwitchService;
import com.intellij.ide.ui.laf.LafManagerImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LafSwitchConfigurable implements Configurable {

    private LAfSwitchSettingsComponent mySettingsComponent;

    @Override
    public String getDisplayName() {
        return "External LAF Switcher";
    }

    @Override
    public @Nullable JComponent createComponent() {
        List<String> lafNames = Arrays
                .stream(LafManagerImpl.getInstance().getInstalledLookAndFeels())
                .map(UIManager.LookAndFeelInfo::getName)
                .collect(Collectors.toList());
        mySettingsComponent = new LAfSwitchSettingsComponent(lafNames);
        return mySettingsComponent.getMyMainPanel();
    }

    @Override
    public void reset() {
        LafSwitchService lafSwitchService = LafSwitchService.getInstance();
        LafSwitchService.State lafSwitchServiceState = lafSwitchService.getState();
        if (lafSwitchServiceState != null) {
            mySettingsComponent.setHost(lafSwitchServiceState.listenHost);
            mySettingsComponent.setPort(lafSwitchServiceState.listenPort);
            mySettingsComponent.setLightTheme(lafSwitchServiceState.lightName);
            mySettingsComponent.setDarkTheme(lafSwitchServiceState.darkName);
        } else {
            throw new IllegalStateException("LafSwitchService.State is null");
        }
    }

    @Override
    public boolean isModified() {
        LafSwitchService lafSwitchService = LafSwitchService.getInstance();
        LafSwitchService.State lafSwitchServiceState = lafSwitchService.getState();
        if (lafSwitchServiceState != null) {
            return !Objects.equals(lafSwitchServiceState.lightName, mySettingsComponent.getLightTheme()) ||
                    !Objects.equals(lafSwitchServiceState.darkName, mySettingsComponent.getDarkTheme()) ||
                    !Objects.equals(lafSwitchServiceState.listenHost, mySettingsComponent.getHost()) ||
                    lafSwitchServiceState.listenPort != mySettingsComponent.getPort();
        } else {
            throw new IllegalStateException("LafSwitchService.State is null");
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        LafSwitchService lafSwitchService = LafSwitchService.getInstance();
        LafSwitchService.State lafSwitchServiceState = lafSwitchService.getState();
        if (lafSwitchServiceState != null) {
            lafSwitchServiceState.lightName = mySettingsComponent.getLightTheme();
            lafSwitchServiceState.darkName  = mySettingsComponent.getDarkTheme();
            lafSwitchServiceState.listenHost= mySettingsComponent.getHost();
            lafSwitchServiceState.listenPort= mySettingsComponent.getPort();
            try {
                LafSwitchService.getInstance().restartSocket();
            } catch (IOException e) {
                throw new ConfigurationException(e.getMessage(), e, "Failed to Initialize Listener Socket");
            }
        } else {
            throw new IllegalStateException("LafSwitchService.State is null");
        }
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
