package com.mopstream.client;

import com.mopstream.client.utility.AuthHandler;
import com.mopstream.client.utility.UserHandler;
import com.mopstream.common.exceptions.NotInDeclaredLimitsException;
import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.common.utility.Outputer;

import java.util.Scanner;

/**
 * Main client class. Creates all client instances.
 */
public class AppClient {
    public static final String PS1 = "$ ";
    public static final String PS2 = "> ";

    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;

    private static String host = "localhost";
    private static int port = 1337;

    public static void main(String[] args) {
        Scanner userScanner = new Scanner(System.in);
        AuthHandler authHandler = new AuthHandler(userScanner);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, userHandler, authHandler);
        client.run();
        userScanner.close();
    }
}