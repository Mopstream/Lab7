package com.mopstream.server.commands;

import com.mopstream.common.data.LabWork;
import com.mopstream.common.exceptions.*;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.DatabaseCollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

/**
 * Command 'remove_greater'. Removes head of the collection.
 */
public class RemoveFirstCommand extends AbstractCommand {
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;
    public RemoveFirstCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_first", "", "удалить первый элемент из коллекции");
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
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            LabWork labWorkToRemove = collectionManager.getHead();
            if (labWorkToRemove == null) throw new LabWorkNotFoundException();
            if (!labWorkToRemove.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkLabUserId(labWorkToRemove.getId(), user))
                throw new ManualDatabaseEditException();
            databaseCollectionManager.deleteLabById(labWorkToRemove.getId());
            collectionManager.removeFromCollection(labWorkToRemove);
            ResponseOutputer.appendln("Лабораторная работа успешно удалена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.appenderror("Коллекция пуста!");
        } catch (NumberFormatException exception) {
            ResponseOutputer.appenderror("ID должен быть представлен числом!");
        } catch (LabWorkNotFoundException exception) {
            ResponseOutputer.appenderror("Лабораторной работы с таким ID в коллекции нет!");
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