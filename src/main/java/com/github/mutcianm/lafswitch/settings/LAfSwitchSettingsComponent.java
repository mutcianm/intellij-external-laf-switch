package com.github.mutcianm.lafswitch.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.util.List;

public class LAfSwitchSettingsComponent {
    private final JPanel myMainPanel;
    private final ComboBox<String> darkTheme;
    private final ComboBox<String> lightTheme;
    private final JBTextField listenHost = new JBTextField();
    private final JBIntSpinner listenPort = new JBIntSpinner(16666, 1024, 65535);

    public LAfSwitchSettingsComponent(List<String> lafNames) {
        darkTheme = new ComboBox<>(new CollectionComboBoxModel<>(lafNames));
        lightTheme = new ComboBox<>(new CollectionComboBoxModel<>(lafNames));

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Dark theme", JBUI.Panels.simplePanel(darkTheme))
                .addLabeledComponent("Light theme", JBUI.Panels.simplePanel(lightTheme))
                .addLabeledComponent("Host", JBUI.Panels.simplePanel(listenHost))
                .addLabeledComponent("Port", listenPort)
                .addComponentFillVertically(new JPanel(), 0)
                .setAlignLabelOnRight(true)
                .getPanel();
    }

    public JPanel getMyMainPanel() {
        return myMainPanel;
    }

    public void setDarkTheme(String name) {
        darkTheme.setSelectedItem(name);
    }

    public void setLightTheme(String name) {
        lightTheme.setSelectedItem(name);
    }

    public String getDarkTheme() {
        return darkTheme.getItem();
    }

    public String getLightTheme() {
        return lightTheme.getItem();
    }

    public String getHost() {
        return listenHost.getText();
    }

    public void setHost(String host) {
        listenHost.setText(host);
    }

    public int getPort() {
        return listenPort.getNumber();
    }

    public void setPort(int port) {
        listenPort.setNumber(port);
    }
}
