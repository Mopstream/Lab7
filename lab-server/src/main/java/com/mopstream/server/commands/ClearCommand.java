package com.mopstream.server.commands;

import com.mopstream.common.data.LabWork;
import com.mopstream.common.exceptions.DatabaseHandlingException;
import com.mopstream.common.exceptions.ManualDatabaseEditException;
import com.mopstream.common.exceptions.PermissionDeniedException;
import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.DatabaseCollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

/**
 * Command 'clear'. Clears the collection.
 */
public class ClearCommand extends AbstractCommand {
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;
    public ClearCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("clear","", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.databaseCollectionManager = databaseCollectionManager;
    }

    /**
     * Executes the command.
     *
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (!stringArgument.isEmpty() || objectArgument != null) throw new WrongAmountOfElementsException();
            for (LabWork labWork : collectionManager.getCollection()) {
                if (!labWork.getOwner().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionManager.checkLabUserId(labWork.getId(), user)) throw new ManualDatabaseEditException();
            }
            databaseCollectionManager.clearCollection();
            collectionManager.clearCollection();
            ResponseOutputer.appendln("Коллекция очищена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (DatabaseHandlingException exception) {
            ResponseOutputer.appenderror("Произошла ошибка при обращении к базе данных!");
        } catch (PermissionDeniedException exception) {
            ResponseOutputer.appenderror("Недостаточно прав для выполнения данной команды!");
            ResponseOutputer.appendln("Принадлежащие другим пользователям объекты доступны только для чтения.");
        } catch (ManualDatabaseEditException exception) {
            ResponseOutputer.appenderror("Произошло прямое изменение базы данных!");
            ResponseOutputer.appendln("Перезапустите клиент для избежания возможных ошибок.");
        }
        return false;
    }
}