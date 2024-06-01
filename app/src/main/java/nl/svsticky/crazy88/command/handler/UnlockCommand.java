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
        // The unlock command can be slow
        InteractionHook interactionHook = replyCallback.deferReply().complete();

        Optional<Integer> givenTeamId = options.stream()
                .filter(v -> v.getName().equals("teamId"))
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
            team.unlockAssignments(locationId, config.locations.get(locationId).assignments);

            // Get the members of the team
            teamMemberIds = User.getForTeam(driver, team.teamId).stream().map(v -> v.userId).toList();
        } catch (SQLException e) {
            replyCallback.reply(Replies.ERROR).queue();
            return;
        }

        // Build the reply to all team members
        StringBuilder sb = new StringBuilder();
        sb.append("Je hebt nieuwe oprdachten ontrendeld:\n");
        for(String assignment : config.locations.get(locationId).assignments) {
            sb.append(String.format("- %s\n", assignment));
        }

        // Send the assignments to all team members
        for(long teamMemberId : teamMemberIds) {
            net.dv8tion.jda.api.entities.User user = App.getJdaInstance().getUserById(teamMemberId);
            user.openPrivateChannel()
                    .flatMap((pc) -> pc.sendMessage(sb.toString()))
                    .queue();
        }

        // Inform the helper
        interactionHook.editOriginal(Replies.UNLOCK_NEW_ASSIGNMENTS_UNLOCKED).queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.UNLOCK,
                "Unlock new assignments for a team (Helpers only)",
                new CommandOption[] {
                        new CommandOption(OptionType.INTEGER, "teamId", "Het team-nummer")
                }
        );
    }
}