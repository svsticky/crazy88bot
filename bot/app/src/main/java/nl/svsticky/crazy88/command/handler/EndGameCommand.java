package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;
import nl.svsticky.crazy88.command.Replies;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.GameState;

import java.sql.SQLException;
import java.util.List;

public class EndGameCommand implements CommandHandler {

    private final Driver driver;
    private final ConfigModel config;

    public EndGameCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        GameState gameState = new GameState(driver);
        try {
            gameState.setIsGameRunning(false);
            replyCallback.reply(Replies.GAME_ENDED).queue();
        } catch (SQLException e) {
            replyCallback.reply(Replies.ERROR).queue();
        }
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.ENDGAME,
                "End the Crazy88",
                new CommandOption[0]
        );
    }
}

