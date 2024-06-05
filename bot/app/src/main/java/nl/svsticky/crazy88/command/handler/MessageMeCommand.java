package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;
import nl.svsticky.crazy88.command.Replies;

import java.util.List;

public class MessageMeCommand implements CommandHandler {
    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        App.getJdaInstance().retrieveUserById(userId)
                .flatMap(User::openPrivateChannel)
                .flatMap(privateChannel -> privateChannel.sendMessage(Replies.MESSAGEME_WELCOME))
                .queue();

        replyCallback
                .reply(Replies.MESSAGME_OK)
                .setEphemeral(true)
                .queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.MESSAGEME,
                "Laat de bot jou een bericht sturen",
                new CommandOption[0]
        );
    }
}
