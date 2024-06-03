package nl.svsticky.crazy88.command;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface  CommandHandler {
    /**
     * Handle the execution of a command
     * @param replyCallback Discord reply handle
     * @param userId The ID of the user who executed the command
     * @param options The supplied command options
     */
    void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options);

    /**
     * Get information about the command
     * @return The data for the command
     */
    CommandData getCommandData();
}
