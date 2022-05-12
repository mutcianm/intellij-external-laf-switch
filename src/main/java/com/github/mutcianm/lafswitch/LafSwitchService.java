package com.github.mutcianm.lafswitch;

import com.intellij.ide.actions.QuickChangeLookAndFeel;
import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.laf.IntelliJLookAndFeelInfo;
import com.intellij.ide.ui.laf.LafManagerImpl;
import com.intellij.ide.ui.laf.darcula.DarculaLookAndFeelInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@Service(Service.Level.APP)
@State(name = "com.github.mutcianm.lafswitch.settings.LafSwitchSettingsState",
       storages = { @Storage(value = "LafSwitchService.xml", roamingType = RoamingType.DISABLED) })
public final class LafSwitchService implements PersistentStateComponent<LafSwitchService.State> {

    private final Logger LOG = Logger.getInstance(LafSwitchService.class);
    private ServerSocket serverSocket;
    private State myState = new State();

    public static class State {
        public String darkName;
        public String lightName;
        public String listenHost;
        public int listenPort;
        public State() {
            darkName  = new DarculaLookAndFeelInfo().getName();
            lightName = new IntelliJLookAndFeelInfo().getName();
            listenHost = "localhost";
            listenPort = 16666;
        }
    }

    public UIManager.LookAndFeelInfo getDarkLAF() {
        return findLafByNameOrKind(myState.darkName, LAF_KIND.DARK);
    }

    public UIManager.LookAndFeelInfo getLightLAF() {
        return findLafByNameOrKind(myState.lightName, LAF_KIND.LIGHT);
    }

    private UIManager.LookAndFeelInfo findLafByNameOrKind(String name, LAF_KIND kind) throws LafNotFoundException {
        UIManager.LookAndFeelInfo lafByNameStrict = findLafByName(name);
        if (lafByNameStrict != null) {
            return lafByNameStrict;
        } else {
            UIManager.LookAndFeelInfo lafByNameLike = findLafByNameLike(kind.name());
            if (lafByNameLike != null) {
                LOG.info("Can't find LAF '" + name + "'. Found " + kind + " substitute: '" + lafByNameLike.getName() +"'");
                switch (kind) {
                    case DARK:
                        myState.darkName    = lafByNameLike.getName();
                        break;
                    case LIGHT:
                        myState.lightName   = lafByNameLike.getName();
                        break;
                }
                return lafByNameLike;
            } else {
                throw new LafNotFoundException(name, kind);
            }
        }
    }

    @Nullable
    private UIManager.LookAndFeelInfo findLafByName(String name) {
        return Arrays
                .stream(LafManagerImpl.getInstance().getInstalledLookAndFeels())
                .filter(laf -> Objects.equals(laf.getName(), name))
                .findFirst().orElse(null);
    }

    @Nullable
    private UIManager.LookAndFeelInfo findLafByNameLike(String nameLike) {
        return Arrays
                .stream(LafManagerImpl.getInstance().getInstalledLookAndFeels())
                .filter(laf -> laf.getName().toLowerCase().contains(nameLike.toLowerCase()))
                .findFirst().orElse(null);
    }

    public static LafSwitchService getInstance() {
        return ApplicationManager.getApplication().getService(LafSwitchService.class);
    }

    @Override
    public @Nullable LafSwitchService.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    private ServerSocket initSocket() throws IOException {
        terminateListener();
        InetAddress inetAddress = InetAddress.getByName(myState.listenHost);
        return new ServerSocket(myState.listenPort, 50, inetAddress);
    }

    private void listenForCommand() {
        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            } catch (SocketException se) {
                LOG.info("Closing listener socket on " + myState.listenHost + ":" + myState.listenPort);
                break;
            } catch (IOException e) {
                LOG.error("Failed to handle command", e);
            }
        }
    }

    public void terminateListener() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed())
            serverSocket.close();
    }

    public void restartSocket() throws IOException {
        serverSocket = initSocket();
        ApplicationManager.getApplication().executeOnPooledThread(this::listenForCommand);
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String input = in.readLine().trim();
        UIManager.LookAndFeelInfo info;
        switch (input) {
            case "dark":
                info = getDarkLAF();
                break;
            case "light":
                info = getLightLAF();
                break;
            default:
                info = Arrays.stream(LafManagerImpl.getInstance().getInstalledLookAndFeels())
                        .filter(lookAndFeelInfo -> lookAndFeelInfo.getName().equals(input))
                        .findFirst().orElse(null);
        }
        if (info != null) {
            ApplicationManager.getApplication().invokeLater(() ->
                    QuickChangeLookAndFeel.switchLafAndUpdateUI(LafManager.getInstance(), info, true));
        } else {
            String reply = "can't find LAF " + input;
            clientSocket.getOutputStream().write(reply.getBytes(StandardCharsets.UTF_8));
        }
    }
}
