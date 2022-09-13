package com.mopstream.server.commands;

import com.mopstream.common.data.*;
import com.mopstream.common.exceptions.*;
import com.mopstream.common.interaction.NewLab;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.DatabaseCollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Command 'update'. Updates the information about selected labwork.
 */
public class UpdateCommand extends AbstractCommand {
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;
    public UpdateCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("update", "<ID> {element}", "обновить значение элемента коллекции по ID");
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
            if (stringArgument.isEmpty() || objectArgument == null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();

            Long id = Long.parseLong(stringArgument);
            if (id <= 0) throw new NumberFormatException();
            LabWork oldLabWork = collectionManager.getById(id);
            if (oldLabWork == null) throw new LabWorkNotFoundException();
            if (!oldLabWork.getOwner().equals(user)) throw new PermissionDeniedException();
            if (!databaseCollectionManager.checkLabUserId(oldLabWork.getId(), user))throw new ManualDatabaseEditException();
            NewLab newLab = (NewLab)objectArgument;

            databaseCollectionManager.updateLabById(id,newLab);

            String name = newLab.getName() == null ? oldLabWork.getName() : newLab.getName();
            Coordinates coordinates = newLab.getCoordinates() == null ? oldLabWork.getCoordinates() : newLab.getCoordinates();
            LocalDateTime creationDate = oldLabWork.getCreationDate();
            Long minimalPoint = newLab.getMinimalPoint() == -1 ? oldLabWork.getMinimalPoint() : newLab.getMinimalPoint();
            Difficulty difficulty = newLab.getDifficulty() == null ? oldLabWork.getDifficulty() : newLab.getDifficulty();
            Discipline discipline = newLab.getDiscipline() == null ? oldLabWork.getDiscipline() : newLab.getDiscipline();

            collectionManager.removeFromCollection(oldLabWork);
            collectionManager.addToCollection(new LabWork(id, name, coordinates, creationDate, minimalPoint, difficulty, discipline, user));
            ResponseOutputer.appendln("Лабораторная работа успешно изменена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.appenderror("Коллекция пуста!");
        } catch (NumberFormatException exception) {
            ResponseOutputer.appenderror("ID должен быть представлен положительным числом!");
        } catch (LabWorkNotFoundException exception) {
            ResponseOutputer.appenderror("Лабораторной работы с таким ID в коллекции нет!");
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