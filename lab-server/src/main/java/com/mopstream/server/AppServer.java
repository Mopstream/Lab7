package com.mopstream.server;

import com.mopstream.server.commands.*;
import com.mopstream.server.utility.*;

/**
 * Main server class. Creates all server instances.
 */
public class AppServer {
    private static final int MAX_CLIENTS = 1000;
    private static int PORT = 1337;
    private static String databaseUsername = "s335126";
    private static String databaseAddress = "jdbc:postgresql://pg:5432/studs";
    private static String databasePassword = "СЕКРЕТ";

    public static void main(String[] args) {
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseAddress, databaseUsername, databasePassword);
        DatabaseUserManager databaseUserManager = new DatabaseUserManager(databaseHandler);
        DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseHandler, databaseUserManager);
        CollectionManager collectionManager = new CollectionManager(databaseCollectionManager);
        CommandManager commandManager = new CommandManager(
                new AddCommand(collectionManager, databaseCollectionManager),
                new ClearCommand(collectionManager, databaseCollectionManager),
                new ExecuteScriptCommand(),
                new ExitCommand(),
                new FilterByDisciplineCommand(collectionManager),
                new FilterLessThanMinimalPointCommand(collectionManager),
                new HeadCommand(collectionManager),
                new HelpCommand(),
                new InfoCommand(collectionManager),
                new LoginCommand(databaseUserManager),
                new PrintAscendingCommand(collectionManager),
                new RegisterCommand(databaseUserManager),
                new RemoveByIdCommand(collectionManager, databaseCollectionManager),
                new RemoveFirstCommand(collectionManager, databaseCollectionManager),
                new RemoveLowerCommand(collectionManager, databaseCollectionManager),
                new ShowCommand(collectionManager),
                new UpdateCommand(collectionManager, databaseCollectionManager)
        );

        ShutdownHandling.addCollectionSavingHook();
        Server server = new Server(PORT, MAX_CLIENTS, commandManager);
        server.run();
        databaseHandler.closeConnection();
    }
}
