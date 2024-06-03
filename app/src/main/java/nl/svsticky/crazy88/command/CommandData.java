package nl.svsticky.crazy88.command;

/**
 * The data of a command which can be executed
 * @param commandName The name of the command
 * @param description The description of the command
 * @param options Required options for the command
 */
public record CommandData(CommandName commandName, String description, CommandOption[] options) { }
