package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import nl.svsticky.crazy88.command.CommandData;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandName;
import nl.svsticky.crazy88.command.CommandOption;
import nl.svsticky.crazy88.command.Replies;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.database.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RegisterCommand implements CommandHandler {
    private final Driver driver;
    private final ConfigModel config;

    public RegisterCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        Optional<Integer> givenTeamId = options.stream()
                .filter(v -> v.getName().equals("teamId"))
                .findFirst()
                .map(OptionMapping::getAsInt);

        if(givenTeamId.isEmpty()) {
            replyCallback.reply(Replies.REGISTER_MISSING_TEAM_ID).queue();
            return;
        }

        int teamId = givenTeamId.get();
        Integer startingLocationId = config.teamStartingLocations.get(teamId);
        if(startingLocationId == null) {
            replyCallback.reply(Replies.REGISTER_UNKNOWN_TEAM_ID).queue();
            return;
        }

        try {
            // Create the team
            Optional<Team> mTeam = Team.getbyId(this.driver, teamId);
            Team team;
            if(mTeam.isEmpty()) {
                team  = Team.create(this.driver, teamId);
            } else {
                team = mTeam.get();
            }

            // Unlock the first location
            team.unlockLocation(startingLocationId);

            // Make sure the user exists and is in the team
            Optional<User> mUser = User.getById(this.driver, userId);
            if(mUser.isEmpty()) {
                User.create(this.driver, userId, User.UserType.REGULAR, Optional.of(teamId), Optional.empty());
            } else {
                mUser.get().setTeamId(Optional.of(teamId));
            }
        } catch (SQLException e) {
            replyCallback.reply(Replies.ERROR).queue();
            return;
        }

        // Send the first location to the user
        replyCallback.replyEmbeds(new EmbedBuilder()
                .setDescription("Je bent geregistreerd! Hier is je eerste locatie:")
                .addField(new MessageEmbed.Field(
                        "Long",
                        config.locations.get(startingLocationId).geoLong,
                        true
                ))
                .addField(new MessageEmbed.Field(
                        "Lat",
                        config.locations.get(startingLocationId).geoLat,
                        true
                ))
                .build()
        ).queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.REGISTER,
                "Register your team",
                new CommandOption[]{
                        new CommandOption(OptionType.INTEGER, "teamId", "Het toegewezen teamnummer.")
                }
        );
    }
}

