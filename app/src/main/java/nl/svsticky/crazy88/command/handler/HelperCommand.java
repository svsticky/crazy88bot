package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;

import java.util.List;

public class HelperCommand implements CommandHandler {

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        replyCallback.deferReply().queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.HELPER,
                "Get the location of a helper",
                new CommandOption[0]
        );
    }
}

