package nl.svsticky.crazy88.command;

import nl.svsticky.crazy88.command.handler.EndGameCommand;
import nl.svsticky.crazy88.command.handler.HelperCommand;
import nl.svsticky.crazy88.command.handler.IamAdminCommand;
import nl.svsticky.crazy88.command.handler.IamHelperCommand;
import nl.svsticky.crazy88.command.handler.MessageMeCommand;
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
                new HelperCommand(driver, config),
                new IamAdminCommand(driver, config),
                new IamHelperCommand(driver, config),
                new RegisterCommand(driver, config),
                new SubmitCommand(driver, config),
                new UnlockCommand(driver, config),
                new MessageMeCommand(),
        };
    }

    public CommandHandler[] getHandlers() {
        return this.handlers;
    }
}
