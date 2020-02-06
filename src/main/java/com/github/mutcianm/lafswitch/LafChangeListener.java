package com.github.mutcianm.lafswitch;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.actions.QuickChangeLookAndFeel;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.IntelliJLookAndFeelInfo;
import com.intellij.ide.ui.laf.LafManagerImpl;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

class LafChangeListener implements AppLifecycleListener {

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String input = in.readLine();
        UIManager.LookAndFeelInfo info;
        switch (input) {
            case "dark":
                info = new DarculaLookAndFeelInfo();
                break;
            case "light":
                info = new IntelliJLookAndFeelInfo();
                break;
            default:
                info = Arrays.stream(LafManagerImpl.getInstance().getInstalledLookAndFeels())
                        .filter(lookAndFeelInfo -> lookAndFeelInfo.getName().equals(input))
                        .findFirst().orElse(null);
        }
        if (info != null) {
            ApplicationManager.getApplication().invokeLater(() ->
                    QuickChangeLookAndFeel.switchLafAndUpdateUI(LafManager.getInstance(), info, true));
        }

    }

    @Override
    public void appStarting(@Nullable Project projectFromCommandLine) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            int portNumber = Registry.intValue("autolafswitch.port",16666);
            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}