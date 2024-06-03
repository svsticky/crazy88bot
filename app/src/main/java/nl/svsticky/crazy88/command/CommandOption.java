package nl.svsticky.crazy88.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * A command option
 * @param type The datatype of the option
 * @param name The name of the option, must be all lowercase
 * @param description The description of the option
 */
public record CommandOption(OptionType type, String name, String description) { }
