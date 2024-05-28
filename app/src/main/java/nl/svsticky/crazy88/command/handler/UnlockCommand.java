package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;

import java.util.List;

public class UnlockCommand implements CommandHandler {

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.UNLOCK,
                "Unlock new assignments for a team (Helpers only)",
                new CommandOption[] {
                        new CommandOption(OptionType.STRING, "name", "The name of the team")
                }
        );
    }
}