package nl.svsticky.crazy88.command.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SubmitCommand implements CommandHandler {

    private final Driver driver;
    private final ConfigModel config;

    public SubmitCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        InteractionHook interactionHook = replyCallback.deferReply().complete();

        // Retrieve options
        Optional<Integer> givenAssignmentId = options.stream()
                .filter(option -> option.getName().equals("assignmentid"))
                .findFirst()
                .map(OptionMapping::getAsInt);

        Optional<Message.Attachment> givenAttachment = options.stream()
                .filter(option -> option.getName().equals("assignment"))
                .findFirst()
                .map(OptionMapping::getAsAttachment);

        // Validate attachment was provided and is an image
        if(givenAttachment.isEmpty() || !givenAttachment.get().isImage()) {
            interactionHook.editOriginal(Replies.SUBMIT_MISSING_ASSIGNMENT).queue();
            return;
        }

        // Validate assignment ID was provided
        if(givenAssignmentId.isEmpty()) {
            interactionHook.editOriginal(Replies.SUBMIT_MISSING_ASSIGNMENT_ID).queue();
            return;
        }

        Team team;
        try {
            // Fetch the associated user
            Optional<User> mUser = User.getById(driver, userId);
            if(mUser.isEmpty() || mUser.get().teamId.isEmpty()) {
                interactionHook.editOriginal(Replies.SUBMIT_USER_NOT_REGISTERED).queue();
                return;
            }

            User user = mUser.get();
            if(user.teamId.isEmpty()) {
                interactionHook.editOriginal(Replies.SUBMIT_USER_NOT_REGISTERED).queue();
                return;
            }

            // Fetch the user's team
            Optional<Team> mTeam = Team.getbyId(driver, user.teamId.get());
            if(mTeam.isEmpty()) {
                interactionHook.editOriginal(Replies.ERROR).queue();
                return;
            }

            team = mTeam.get();
            List<Team.AvailableAssignment> availableAssignments = team.getAvailableAssignments();
            List<Integer> unlockedIds = availableAssignments.stream().map(Team.AvailableAssignment::id).toList();

            // Check if the assignment is unlocked
            if(!unlockedIds.contains(givenAssignmentId.get())) {
                interactionHook.editOriginal(Replies.SUBMIT_ASSIGNMENT_NOT_UNLOCKED).queue();
                return;
            }
        } catch (SQLException e) {
            App.getLogger().error(e);
            interactionHook.editOriginal(Replies.ERROR).queue();
            return;
        }

        try {
            Message.Attachment attachment = givenAttachment.get();

            // Example: 'path/to/my/submissions/3_4.png',
            // indicating assignment 4 for team 3.
            Path path = Paths.get(config.submit.submissionDirectory, String.format("%d_%d.%s",
                    team.teamId,
                    givenAssignmentId.get(),
                    attachment.getFileExtension()
            ));

            // Delete the file if it already exists
            // This allows for resubmission
            File maybeFile = path.toFile();
            if(maybeFile.exists()) {
                if(!maybeFile.delete()) {
                    interactionHook.editOriginal(Replies.ERROR).queue();
                    return;
                }
            }

            // Check the directory even exists
            Path dirPath = Paths.get(config.submit.submissionDirectory);
            if(!dirPath.toFile().exists()) {
                if(!dirPath.toFile().mkdirs()) {
                    interactionHook.editOriginal(Replies.ERROR).queue();
                    return;
                }
            }

            // Stream the file from the URL to memory
            ReadableByteChannel rbc = Channels.newChannel(new URI(attachment.getUrl()).toURL().openStream());
            FileOutputStream fos = new FileOutputStream(path.toFile());
            FileChannel fc = fos.getChannel();

            fc.transferFrom(rbc, 0, attachment.getSize());
            fos.close();
        } catch (IOException | URISyntaxException e) {
            App.getLogger().error(e);
            interactionHook.editOriginal(Replies.ERROR).queue();
            return;
        }

        // Mark the assignment as submitted in the database
        try {
            team.submitAssignment(givenAssignmentId.get());
        } catch (SQLException e) {
            App.getLogger().error(e);
            interactionHook.editOriginal(Replies.ERROR).queue();
            return;
        }

        interactionHook.editOriginal(Replies.SUBMIT_OK).queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.SUBMIT,
                "Submit an assignment",
                new CommandOption[] {
                        new CommandOption(OptionType.INTEGER, "assignmentid", "Het nummer van de opdracht"),
                        new CommandOption(OptionType.ATTACHMENT, "assignment", "A photo of your assignment")
                }
        );
    }
}

