package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;

import java.util.List;

public class IamAdminCommand implements CommandHandler {

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.IAM_ADMIN,
                "Register yourself as admin user",
                new CommandOption[] {
                        new CommandOption(OptionType.STRING, "password", "The admin password")
                }
        );
    }
}
