package com.mopstream.server.commands;

import com.mopstream.common.data.LabWork;
import com.mopstream.common.exceptions.*;
import com.mopstream.common.interaction.NewLab;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.DatabaseCollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

import java.time.LocalDateTime;

/**
 * Command 'remove_lower'. Removes elements lower than user entered.
 */
public class RemoveLowerCommand extends AbstractCommand {
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;

    public RemoveLowerCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("remove_lower", "{element}", "удалить из коллекции все элементы, меньшие, чем заданный");
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
            if (!stringArgument.isEmpty() || objectArgument == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            NewLab newLab = (NewLab) objectArgument;
            LabWork labToFind = new LabWork(
                    0L,
                    newLab.getName(),
                    newLab.getCoordinates(),
                    LocalDateTime.now(),
                    newLab.getMinimalPoint(),
                    newLab.getDifficulty(),
                    newLab.getDiscipline(),
                    user
            );
            LabWork labFromCollection = collectionManager.getByValue(labToFind);
            if (labFromCollection == null) throw new LabWorkNotFoundException();

            for (LabWork labWork : collectionManager.getLower(labFromCollection)) {
                if (!labWork.getOwner().equals(user)) throw new PermissionDeniedException();
                if (!databaseCollectionManager.checkLabUserId(labWork.getId(), user))
                    throw new ManualDatabaseEditException();
            }
            for (LabWork labWork : collectionManager.getLower(labFromCollection)) {
                databaseCollectionManager.deleteLabById(labWork.getId());
                collectionManager.removeFromCollection(labWork);
            }
            ResponseOutputer.appendln("Лабы успешно удалены!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.appenderror("Коллекция пуста!");
        } catch (LabWorkNotFoundException exception) {
            ResponseOutputer.appenderror("Лабораторной работы с такими параметрами в коллекции нет!");
        } catch (ClassCastException exception) {
            ResponseOutputer.appenderror("Переданный клиентом объект неверен!");
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