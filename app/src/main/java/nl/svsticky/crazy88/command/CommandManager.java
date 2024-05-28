package nl.svsticky.crazy88.command;

import nl.svsticky.crazy88.command.handler.*;

public class CommandManager {
    private final CommandHandler[] handlers;

    public CommandManager() {
        this.handlers = new CommandHandler[] {
                new EndGameCommand(),
                new HelperCommand(),
                new IamAdminCommand(),
                new IamHelperCommand(),
                new RegisterCommand(),
                new SubmitCommand(),
                new UnlockCommand(),
        };
    }

    public CommandHandler[] getHandlers() {
        return this.handlers;
    }
}
