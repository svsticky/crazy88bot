package nl.svsticky.crazy88.command;

import nl.svsticky.crazy88.command.handler.EndGameCommand;
import nl.svsticky.crazy88.command.handler.HelperCommand;
import nl.svsticky.crazy88.command.handler.IamAdminCommand;
import nl.svsticky.crazy88.command.handler.IamHelperCommand;
import nl.svsticky.crazy88.command.handler.RegisterCommand;
import nl.svsticky.crazy88.command.handler.SubmitCommand;
import nl.svsticky.crazy88.command.handler.UnlockCommand;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.database.driver.Driver;

public class CommandManager {
    private final CommandHandler[] handlers;

    public CommandManager(Driver driver, ConfigModel config) {

        this.handlers = new CommandHandler[] {
                new EndGameCommand(driver, config),
                new HelperCommand(),
                new IamAdminCommand(driver, config),
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
