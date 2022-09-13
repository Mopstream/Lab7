package com.mopstream.client.utility;

import com.mopstream.client.AppClient;
import com.mopstream.common.data.*;
import com.mopstream.common.exceptions.CommandUsageException;
import com.mopstream.common.exceptions.IncorrectInputInScriptException;
import com.mopstream.common.exceptions.ScriptRecursionException;
import com.mopstream.common.interaction.NewLab;
import com.mopstream.common.interaction.Request;
import com.mopstream.common.interaction.ResponseCode;
import com.mopstream.common.interaction.User;
import com.mopstream.common.utility.Outputer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Receives user requests.
 */
public class UserHandler {
    private final int labWorkAsker = 1;

    private Scanner userScanner;
    private Stack<File> scriptStack = new Stack<>();
    private Stack<Scanner> scannerStack = new Stack<>();

    public UserHandler(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    /**
     * Receives user input.
     *
     * @param serverResponseCode Last server's response code.
     * @return New request to server.
     */
    public Request handle(ResponseCode serverResponseCode, User user) {
        String userInput;
        String[] userCommand;
        ProcessingCode processingCode;
        int rewriteAttempts = 0;
        try {
            do {
                try {
                    if (fileMode() && (serverResponseCode == ResponseCode.ERROR))
                        throw new IncorrectInputInScriptException();
                    while (fileMode() && !userScanner.hasNextLine()) {
                        userScanner.close();
                        userScanner = scannerStack.pop();
                        Outputer.println("Возвращаюсь к скрипту '" + scriptStack.pop().getName() + "'...");
                    }
                    if (fileMode()) {
                        userInput = userScanner.nextLine();
                        if (!userInput.isEmpty()) {
                            Outputer.print(AppClient.PS1);
                            Outputer.println(userInput);
                        }
                    } else {
                        Outputer.print(AppClient.PS1);
                        userInput = userScanner.nextLine();
                    }
                    userCommand = (userInput.trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                } catch (NoSuchElementException | IllegalStateException exception) {
                    Outputer.println();
                    Outputer.printerror("Произошла ошибка при вводе команды!");
                    userCommand = new String[]{"", ""};
                    rewriteAttempts++;
                    if (rewriteAttempts >= labWorkAsker) {
                        Outputer.printerror("Превышено количество попыток ввода!");
                        System.exit(0);
                    }
                }
                processingCode = processCommand(userCommand[0], userCommand[1]);
            } while (processingCode == ProcessingCode.ERROR && !fileMode() || userCommand[0].isEmpty());
            try {
                if (fileMode() && (serverResponseCode == ResponseCode.ERROR || processingCode == ProcessingCode.ERROR))
                    throw new IncorrectInputInScriptException();
                switch (processingCode) {
                    case OBJECT:
                        NewLab newLabadd = generateLabAdd();
                        return new Request(userCommand[0], userCommand[1], newLabadd, user);
                    case UPDATE_OBJECT:
                        NewLab newLabUpdate = generateLabUpdate();
                        return new Request(userCommand[0], userCommand[1], newLabUpdate, user);
                    case SCRIPT:
                        File scriptFile = new File(userCommand[1]);
                        if (!scriptFile.exists()) throw new FileNotFoundException();
                        if (!scriptStack.isEmpty() && scriptStack.search(scriptFile) != -1)
                            throw new ScriptRecursionException();
                        scannerStack.push(userScanner);
                        scriptStack.push(scriptFile);
                        userScanner = new Scanner(scriptFile);
                        Outputer.println("Выполняю скрипт '" + scriptFile.getName() + "'...");
                        break;
                }
            } catch (FileNotFoundException exception) {
                Outputer.printerror("Файл со скриптом не найден!");
            } catch (ScriptRecursionException exception) {
                Outputer.printerror("Скрипты не могут вызываться рекурсивно!");
                throw new IncorrectInputInScriptException();
            }
        } catch (IncorrectInputInScriptException exception) {
            Outputer.printerror("Выполнение скрипта прервано!");
            while (!scannerStack.isEmpty()) {
                userScanner.close();
                userScanner = scannerStack.pop();
            }
            scriptStack.clear();
            return new Request(user);
        }
        return new Request(userCommand[0], userCommand[1], null, user);
    }

    /**
     * Processes the entered command.
     *
     * @return Status of code.
     */
    private ProcessingCode processCommand(String command, String commandArgument) {
        try {
            switch (command) {
                case "":
                    return ProcessingCode.ERROR;
                case "add":
                case "remove_lower":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException("{element}");
                    return ProcessingCode.OBJECT;
                case "clear":
                case "exit":
                case "help":
                case "info":
                case "print_ascending":
                case "remove_first":
                case "server_exit":
                case "show":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "execute_script":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<file_name>");
                    return ProcessingCode.SCRIPT;
                case "filter_by_discipline":
                    if (!commandArgument.isEmpty()) throw new CommandUsageException("{discipline}");
                    return ProcessingCode.OBJECT;
                case "filter_less_than_minimal_point":
                    if (commandArgument.isEmpty()) throw new CommandUsageException();
                    break;
                case "remove_by_id":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<ID>");
                    break;
                case "update":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<ID> {element}");
                    return ProcessingCode.UPDATE_OBJECT;
                case "admin":
                    if (commandArgument.isEmpty()) throw new CommandUsageException("<password>");
                    break;
                default:
                    Outputer.println("Команда '" + command + "' не найдена. Наберите 'help' для справки.");
                    return ProcessingCode.ERROR;
            }
        } catch (CommandUsageException exception) {
            if (exception.getMessage() != null) command += " " + exception.getMessage();
            Outputer.println("Использование: '" + command + "'");
            return ProcessingCode.ERROR;
        }
        return ProcessingCode.OK;
    }

    /**
     * Generates labwork to add.
     *
     * @return Labwork to add.
     * @throws IncorrectInputInScriptException When something went wrong in script.
     */
    private NewLab generateLabAdd() throws IncorrectInputInScriptException {
        LabWorkAsker labWorkAsker = new LabWorkAsker(userScanner);
        if (fileMode()) labWorkAsker.setFileMode();
        return new NewLab(
                labWorkAsker.askName(),
                labWorkAsker.askCoordinates(),
                labWorkAsker.askMinimalPoint(),
                labWorkAsker.askDifficulty(),
                labWorkAsker.askDiscipline()
        );
    }

    /**
     * Generates labwork to update.
     *
     * @return Labwork to update.
     * @throws IncorrectInputInScriptException When something went wrong in script.
     */
    private NewLab generateLabUpdate() throws IncorrectInputInScriptException {
        LabWorkAsker labWorkAsker = new LabWorkAsker(userScanner);
        if (fileMode()) labWorkAsker.setFileMode();
        String name = labWorkAsker.askQuestion("Хотите изменить название лабы?") ?
                labWorkAsker.askName() : null;
        Coordinates coordinates = labWorkAsker.askQuestion("Хотите изменить координаты лабы?") ?
                labWorkAsker.askCoordinates() : null;
        Long minimalPoint = labWorkAsker.askQuestion("Хотите изменить минимальный балл?") ?
                labWorkAsker.askMinimalPoint() : -1;
        Difficulty difficulty = labWorkAsker.askQuestion("Хотите изменить сложность лабы?") ?
                labWorkAsker.askDifficulty() : null;
        Discipline discipline = labWorkAsker.askQuestion("Хотите изменить предмет лабы?") ?
                labWorkAsker.askDiscipline() : null;
        return new NewLab(name, coordinates, minimalPoint, difficulty, discipline);
    }

    /**
     * Checks if UserHandler is in file mode now.
     *
     * @return Is UserHandler in file mode now boolean.
     */
    private boolean fileMode() {
        return !scannerStack.isEmpty();
    }
}