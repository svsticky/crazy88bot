package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import nl.svsticky.crazy88.App;
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
import java.util.Map;
import java.util.Optional;

public class UnlockCommand implements CommandHandler {
    private final Driver driver;
    private final ConfigModel config;

    public UnlockCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }


    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {

        Optional<Integer> givenTeamId = options.stream()
                .filter(v -> v.getName().equals("teamid"))
                .findFirst()
                .map(OptionMapping::getAsInt);

        if(givenTeamId.isEmpty()) {
            replyCallback.reply(Replies.UNLOCK_MISSING_TEAM_ID).queue();
            return;
        }

        int locationId;
        List<Long> teamMemberIds;
        try {
            // Fetch the helper
            Optional<User> mUser = User.getById(this.driver, userId);
            if(mUser.isEmpty()) {
                replyCallback.reply(Replies.UNLOCK_USER_IS_NOT_HELPER).queue();
                return;
            }

            // Check if they are allowed to perform an unlock action
            User user = mUser.get();
            if(user.userType != User.UserType.ADMIN && user.userType != User.UserType.HELPER || user.helperStationId.isEmpty()) {
                replyCallback.reply(Replies.UNLOCK_USER_IS_NOT_HELPER).queue();
                return;
            }

            // Need the location ID outside the try/catch block
            locationId = user.helperStationId.get();

            // Get the team
            Optional<Team> mTeam = Team.getbyId(driver, givenTeamId.get());
            if(mTeam.isEmpty()) {
                replyCallback.reply(Replies.UNLOCK_UNKNOWN_TEAM_ID).queue();
                return;
            }

            // Mark the assignments as unlocked
            Team team = mTeam.get();

            // Check if the team already has unlocked this location
            if(team.getAvailableAssignments().stream().anyMatch(v -> v.locationId() == locationId)) {
                replyCallback.reply(Replies.UNLOCK_ALREADY_UNLOCKED).queue();
                return;
            }

            team.unlockAssignments(
                    locationId,
                    config.locations.get(locationId).assignments
            );

            // Get the members of the team
            teamMemberIds = User.getForTeam(driver, team.teamId).stream().map(v -> v.userId).toList();
        } catch (SQLException e) {
            App.getLogger().error(e);
            replyCallback.reply(Replies.ERROR).queue();
            return;
        }

        // Build the reply to all team members
        StringBuilder sb = new StringBuilder();
        sb.append("Je hebt nieuwe oprdachten ontrendeld:\n");
        for(Map.Entry<Integer, String> entry : config.locations.get(locationId).assignments.entrySet()) {
            sb.append(String.format("%d. %s\n", entry.getKey(), entry.getValue()));
        }

        // Send the assignments to all team members
        for(long teamMemberId : teamMemberIds) {
            App.getLogger().info("Sending assignments to {}", teamMemberId);
            App.getJdaInstance().retrieveUserById(teamMemberId)
                    .flatMap(net.dv8tion.jda.api.entities.User::openPrivateChannel)
                    .flatMap(pc -> pc.sendMessage(sb.toString()))
                    .queue();
        }

        // Inform the helper
        replyCallback.reply(Replies.UNLOCK_NEW_ASSIGNMENTS_UNLOCKED).queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.UNLOCK,
                "Unlock new assignments for a team (Helpers only)",
                new CommandOption[] {
                        new CommandOption(OptionType.INTEGER, "teamid", "Het team-nummer")
                }
        );
    }
}