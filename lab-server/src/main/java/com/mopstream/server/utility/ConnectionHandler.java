package com.mopstream.server.utility;

import com.mopstream.common.interaction.Request;
import com.mopstream.common.interaction.Response;
import com.mopstream.common.interaction.ResponseCode;
import com.mopstream.common.utility.Outputer;
import com.mopstream.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Handles user connection.
 */
public class ConnectionHandler implements Runnable {
    private Server server;
    private Socket clientSocket;
    private CommandManager commandManager;
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    public ConnectionHandler(Server server, Socket clientSocket, CommandManager commandManager) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.commandManager = commandManager;
    }

    /**
     * Main handling cycle.
     */
    @Override
    public void run() {
        Request userRequest = null;
        Response responseToUser = null;
        try (ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream())) {
            do {
                userRequest = (Request) clientReader.readObject();
                responseToUser = forkJoinPool.invoke(new HandleRequestTask(userRequest, commandManager));
                Response finalResponseToUser = responseToUser;
                Request finalUserRequest = userRequest;
                Runnable task = () -> {
                    try {
                        clientWriter.writeObject(finalResponseToUser);
                        clientWriter.flush();
                    } catch (IOException exception) {
                        if (!finalUserRequest.getCommandName().equals("exit"))
                        Outputer.printerror("Произошла ошибка при отправке данных на клиент!");
                    }
                };
                new Thread(task).start();


            } while (responseToUser.getResponseCode() != ResponseCode.CLIENT_EXIT);

        } catch (ClassNotFoundException exception) {
            Outputer.printerror("Произошла ошибка при чтении полученных данных!");
        } catch (CancellationException exception) {
            Outputer.println("При обработке запроса произошла ошибка многопоточности!");
        } catch (IOException exception) {
            if (!userRequest.getCommandName().equals("exit"))
                Outputer.printerror("Непредвиденный разрыв соединения с клиентом!");
        } finally {
            try {
                clientSocket.close();
                Outputer.println("Клиент отключен от сервера.");
            } catch (IOException exception) {
                Outputer.printerror("Произошла ошибка при попытке завершить соединение с клиентом!");
            }
            server.releaseConnection();
        }
    }
}