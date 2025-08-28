package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;
import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.command.Replies;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.config.model.LocationModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.database.model.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelperCommand implements CommandHandler {

    private final Driver driver;
    private final ConfigModel config;

    public HelperCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        try {
            Optional<User> mUser = User.getById(driver, userId);
            if(mUser.isEmpty() || mUser.flatMap(v -> v.teamId).isEmpty()) {
                replyCallback.reply(Replies.HELPER_USER_NOT_REGISTERED).queue();
                return;
            }

            User user = mUser.get();
            Optional<Team> mTeam = Team.getbyId(driver, user.teamId.get());
            if(mTeam.isEmpty()) {
                replyCallback.reply(Replies.ERROR).queue();
                return;
            }

            Team team = mTeam.get();
            List<Team.AvailableLocation> alreadyUnlocked = team.getAvailableLocations();
            List<Integer> alreadyUnlockedIds = alreadyUnlocked.stream().map(Team.AvailableLocation::id).collect(Collectors.toSet()).stream().toList();

            if(alreadyUnlocked.size() == config.locations.size()) {
                replyCallback.reply(Replies.HELPER_ALL_LOCATIONS_UNLOCKED).queue();
                return;
            }

            // int max = Collections.max(alreadyUnlockedIds);
            // int next = max == config.locations.size() ? 1 : max + 1;
            int next = team.getNextHelperLocation();

            team.unlockLocation(next);
            LocationModel nextLocation = config.locations.get(next);

            replyCallback.replyEmbeds(new EmbedBuilder()
                    .setDescription("De volgende helper is ontgrendeld:")
                    .addField(new MessageEmbed.Field(
                            "Naam",
                            nextLocation.name,
                            true
                    ))
                    .addField(new MessageEmbed.Field(
                            "Lat",
                            nextLocation.geoLat,
                            true
                    ))
                    .addField(new MessageEmbed.Field(
                            "Long",
                            nextLocation.geoLong,
                            true
                    ))
                    .build()
            ).queue();
        } catch (SQLException e) {
            App.getLogger().error(e);

            replyCallback.reply(Replies.ERROR).queue();
        }
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.HELPER,
                "Ontvang de locatie van een helper",
                new CommandOption[0]
        );
    }
}

