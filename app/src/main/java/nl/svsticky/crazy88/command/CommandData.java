package nl.svsticky.crazy88.command;

public record CommandData(CommandName commandName, String description, CommandOption[] options) { }
