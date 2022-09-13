package com.mopstream.server.commands;

import com.mopstream.common.exceptions.WrongAmountOfElementsException;
import com.mopstream.common.interaction.User;
import com.mopstream.server.utility.ResponseOutputer;

/**
 * Command 'execute_script'. Execute scripts from a file. Actually only checks argument and prints messages.
 */
public class ExecuteScriptCommand extends AbstractCommand {
    public ExecuteScriptCommand() {
        super("execute_script", "<file_name>", "исполнить скрипт из указанного файла");
    }

    /**
     * Executes the command, but partially.
     *
     * @return Command exit status.
     */
    @Override
    public boolean execute(String stringArgument, Object objectArgument, User user) {
        try {
            if (stringArgument.isEmpty() || objectArgument != null) throw new WrongAmountOfElementsException();
            return true;
        } catch (WrongAmountOfElementsException exception) {
            ResponseOutputer.appendln("Использование: '" + getName() + " " + getUsage() + "'");
        }
        return false;
    }
}