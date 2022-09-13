package com.mopstream.server.commands;

import com.mopstream.common.exceptions.DatabaseHandlingException;
import com.mopstream.common.interaction.NewLab;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.DatabaseCollectionManager;
import com.mopstream.server.utility.ResponseOutputer;
import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.server.utility.CollectionManager;

import java.time.LocalDate;

/**
 * Command 'add'. Adds a new element to collection.
 */
public class AddCommand extends AbstractCommand {
    private final CollectionManager collectionManager;
    private DatabaseCollectionManager databaseCollectionManager;
    public AddCommand(CollectionManager collectionManager, DatabaseCollectionManager databaseCollectionManager) {
        super("add", "{element}", "добавить новый элемент в коллекцию");
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
            NewLab newLab = (NewLab) objectArgument;
            collectionManager.addToCollection(databaseCollectionManager.insertLab(newLab, user));
            ResponseOutputer.appendln("Лабораторная работа успешно добавлена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (ClassCastException exception) {
            ResponseOutputer.appenderror("Переданный клиентом объект неверен!");
        } catch (DatabaseHandlingException e) {
            ResponseOutputer.appenderror("Произошла ошибка при обращении к базе данных!");
        }
        return false;
    }
}