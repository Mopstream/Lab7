package com.mopstream.server.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mopstream.common.interaction.User;
import com.mopstream.server.commands.Command;

/**
 * Operates the commands.
 */
public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Command addCommand;
    private final Command clearCommand;
    private final Command executeScriptCommand;
    private final Command exitCommand;
    private final Command filterByDisciplineCommand;
    private final Command filterLessThanMinimalPointCommand;
    private final Command headCommand;
    private final Command helpCommand;
    private final Command infoCommand;
    private final Command loginCommand;
    private final Command printAscendingCommand;
    private final Command registerCommand;
    private final Command removeByIdCommand;
    private final Command removeFirstCommand;
    private final Command removeLowerCommand;
    private final Command showCommand;
    private final Command updateCommand;

    private ReadWriteLock collectionLocker = new ReentrantReadWriteLock();

    public CommandManager(Command addCommand, Command clearCommand, Command executeScriptCommand, Command exitCommand, Command filterByDisciplineCommand,
                          Command filterLessThanMinimalPointCommand, Command headCommand, Command helpCommand, Command infoCommand, Command loginCommand, Command printAscendingCommand, Command registerCommand,
                          Command removeByIdCommand, Command removeFirstCommand, Command removeLowerCommand,
                          Command showCommand, Command updateCommand) {
        this.addCommand = addCommand;
        this.clearCommand = clearCommand;
        this.executeScriptCommand = executeScriptCommand;
        this.exitCommand = exitCommand;
        this.filterByDisciplineCommand = filterByDisciplineCommand;
        this.filterLessThanMinimalPointCommand = filterLessThanMinimalPointCommand;
        this.headCommand = headCommand;
        this.helpCommand = helpCommand;
        this.infoCommand = infoCommand;
        this.loginCommand = loginCommand;
        this.printAscendingCommand = printAscendingCommand;
        this.registerCommand = registerCommand;
        this.removeByIdCommand = removeByIdCommand;
        this.removeFirstCommand = removeFirstCommand;
        this.removeLowerCommand = removeLowerCommand;
        this.showCommand = showCommand;
        this.updateCommand = updateCommand;

        commands.add(addCommand);
        commands.add(clearCommand);
        commands.add(executeScriptCommand);
        commands.add(exitCommand);
        commands.add(filterByDisciplineCommand);
        commands.add(filterLessThanMinimalPointCommand);
        commands.add(headCommand);
        commands.add(helpCommand);
        commands.add(infoCommand);
        commands.add(printAscendingCommand);
        commands.add(removeByIdCommand);
        commands.add(removeFirstCommand);
        commands.add(removeLowerCommand);
        commands.add(showCommand);
        commands.add(updateCommand);

    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean help(String stringArgument, Object objectArgument, User user) {
        if (helpCommand.execute(stringArgument, objectArgument, user)) {
            for (Command command : commands) {
                ResponseOutputer.appendtable(command.getName() + " " + command.getUsage(), command.getDescription());
            }
            return true;
        } else return false;
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean info(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return infoCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean show(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return showCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean add(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return addCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean update(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return updateCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean removeById(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return removeByIdCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean clear(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return clearCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }


    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean executeScript(String stringArgument, Object objectArgument, User user) {
        return executeScriptCommand.execute(stringArgument, objectArgument, user);
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean exit(String stringArgument, Object objectArgument, User user) {
        return exitCommand.execute(stringArgument, objectArgument, user);
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean removeFirst(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return removeFirstCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean head(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return headCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean removeLower(String stringArgument, Object objectArgument, User user) {
        collectionLocker.writeLock().lock();
        try {
            return removeLowerCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.writeLock().unlock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean filterByDiscipline(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return filterByDisciplineCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().lock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean filterLessThanMinimalPoint(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return filterLessThanMinimalPointCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().lock();
        }
    }

    /**
     * Prints info about the all commands.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @return Command exit status.
     */
    public boolean printAscending(String stringArgument, Object objectArgument, User user) {
        collectionLocker.readLock().lock();
        try {
            return printAscendingCommand.execute(stringArgument, objectArgument, user);
        } finally {
            collectionLocker.readLock().lock();
        }
    }


    /**
     * Executes needed command.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @param user           User object.
     * @return Command exit status.
     */
    public boolean login(String stringArgument, Object objectArgument, User user) {
        return loginCommand.execute(stringArgument, objectArgument, user);
    }

    /**
     * Executes needed command.
     *
     * @param stringArgument Its string argument.
     * @param objectArgument Its object argument.
     * @param user           User object.
     * @return Command exit status.
     */
    public boolean register(String stringArgument, Object objectArgument, User user) {
        return registerCommand.execute(stringArgument, objectArgument, user);
    }
}