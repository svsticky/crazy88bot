package nl.svsticky.crazy88.command;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface  CommandHandler {
    void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options);

    CommandData getCommandData();
}
