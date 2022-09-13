package com.mopstream.server.utility;

import com.mopstream.common.interaction.Request;
import com.mopstream.common.interaction.Response;
import com.mopstream.common.interaction.ResponseCode;
import com.mopstream.common.interaction.User;

import java.util.concurrent.RecursiveTask;

/**
 * A class for handle request task.
 */
public class HandleRequestTask extends RecursiveTask<Response> {
    private Request request;
    private CommandManager commandManager;

    public HandleRequestTask(Request request, CommandManager commandManager) {
        this.request = request;
        this.commandManager = commandManager;
    }

    @Override
    protected Response compute() {
        User hashedUser = new User(
                request.getUser().getUsername(),
                PasswordHasher.hashPassword(request.getUser().getPassword())
        );
        ResponseCode responseCode = executeCommand(request.getCommandName(), request.getCommandStringArgument(),
                request.getCommandObjectArgument(), hashedUser);
        return new Response(responseCode, ResponseOutputer.getAndClear());
    }

    /**
     * Executes a command from a request.
     *
     * @param command               Name of command.
     * @param commandStringArgument String argument for command.
     * @param commandObjectArgument Object argument for command.
     * @return Command execute status.
     */
    private synchronized ResponseCode executeCommand(String command, String commandStringArgument,
                                                     Object commandObjectArgument, User user) {
        switch (command) {
            case "":
                break;
            case "add":
                if (!commandManager.add(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "clear":
                if (!commandManager.clear(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "execute_script":
                if (!commandManager.executeScript(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "exit":
                if (!commandManager.exit(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                return ResponseCode.CLIENT_EXIT;
            case "filter_by_discipline":
                if (!commandManager.filterByDiscipline(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "filter_less_than_minimal_point":
                if (!commandManager.filterLessThanMinimalPoint(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "help":
                if (!commandManager.help(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "head":
                if (!commandManager.head(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "info":
                if (!commandManager.info(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "login":
                if (!commandManager.login(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "print_ascending":
                if (!commandManager.printAscending(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "register":
                if (!commandManager.register(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "remove_by_id":
                if (!commandManager.removeById(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "remove_first":
                if (!commandManager.removeFirst(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "remove_lower":
                if (!commandManager.removeLower(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "show":
                if (!commandManager.show(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            case "update":
                if (!commandManager.update(commandStringArgument, commandObjectArgument, user))
                    return ResponseCode.ERROR;
                break;
            default:
                ResponseOutputer.appendln("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                return ResponseCode.ERROR;
        }
        return ResponseCode.OK;
    }
}