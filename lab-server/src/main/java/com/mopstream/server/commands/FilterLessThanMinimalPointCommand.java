package com.mopstream.server.commands;

import com.mopstream.common.exceptions.CollectionIsEmptyException;
import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.CollectionManager;
import com.mopstream.server.utility.ResponseOutputer;

/**
 * Command 'filter_less_than_minimal_point minimalPoint'. Filters the collection by minimal point.
 */
public class FilterLessThanMinimalPointCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public FilterLessThanMinimalPointCommand(CollectionManager collectionManager) {
        super("filter_less_than_minimal_point", "<minimalPoint>", "вывести элементы, значение поля minimalPoint которых меньше заданного");
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command.
     *
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        boolean result = false;
        try {
            if (stringArgument.isEmpty() || objectArgument != null) throw new WrongAmountOfElementsException();
            if (collectionManager.collectionSize() == 0) throw new CollectionIsEmptyException();
            long minimalPoint = Long.parseLong(stringArgument);
            String filteredInfo = collectionManager.filterByMinimalPoint(minimalPoint);
            if (!filteredInfo.isEmpty()) {
                ResponseOutputer.appendln(filteredInfo);
                result = true;
            } else ResponseOutputer.appendln("В коллекции нет Лабораторных работ с меньшим минимальным баллом, чем этот!");
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        } catch (CollectionIsEmptyException exception) {
            ResponseOutputer.appenderror("Коллекция пуста!");
        }
        return result;
    }
}
