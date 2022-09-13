package com.mopstream.server.commands;

import com.mopstream.common.data.Discipline;
import com.mopstream.common.exceptions.CollectionIsEmptyException;
import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

/**
 * Command 'filter_by_discipline discipline'. Filters the collection by discipline.
 */
public class FilterByDisciplineCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public FilterByDisciplineCommand(CollectionManager collectionManager) {
        super("filter_by_discipline", "{discipline}", "вывести элементы, значение поля discipline которых равно заданному");
        this.collectionManager = collectionManager;
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
            Discipline discipline = (Discipline) objectArgument;
            String filteredInfo = collectionManager.disciplineFilteredInfo(discipline);
            if (!filteredInfo.isEmpty()) {
                ResponseOutputer.appendln(filteredInfo);
                return true;
            } else ResponseOutputer.appendln("В коллекции нет Лабораторных работ по этой дисциплине!");
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.appenderror("Коллекция пуста!");
        }
        return false;
    }
}
