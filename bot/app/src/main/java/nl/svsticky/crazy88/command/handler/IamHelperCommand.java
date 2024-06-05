package nl.svsticky.crazy88.command.handler;

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
import nl.svsticky.crazy88.database.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IamHelperCommand implements CommandHandler {

    private final Driver driver;
    private final ConfigModel config;

    public IamHelperCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        Optional<String> givenPassword = options.stream()
                .filter(v -> v.getName().equals("password"))
                .findFirst()
                .map(OptionMapping::getAsString);

        Optional<Integer> givenHelperStationId = options.stream()
                .filter(v -> v.getName().equals("post"))
                .findFirst()
                .map(OptionMapping::getAsInt);

        if(givenPassword.isEmpty()) {
            replyCallback.reply(Replies.IAMHELPER_PASSWORD_MISSING).queue();
            return;
        }

        if(givenHelperStationId.isEmpty()) {
            replyCallback.reply(Replies.IAMHELPER_STATION_ID_MISSING).queue();
            return;
        }

        if(!givenPassword.get().equals(config.helper.password)) {
            replyCallback.reply(Replies.IAMHELPER_PASSWORD_INCORRECT).queue();
            return;
        }

        if(config.locations.get(givenHelperStationId.get()) == null) {
            replyCallback.reply(Replies.IAMHELPER_LOCATION_UNKNOWN).queue();
            return;
        }

        try {
            Optional<User> user = User.getById(this.driver, userId);
            if(user.isPresent()) {
                if(user.get().userType == User.UserType.HELPER) {
                    replyCallback.reply(Replies.IAMHELPER_ALREADY_HELPER).queue();
                    return;
                }

                if(user.get().userType != User.UserType.ADMIN) {
                    user.get().setUserType(User.UserType.HELPER);
                }

                user.get().setHelperStationId(givenHelperStationId);
            } else {
                User.create(this.driver, userId, User.UserType.HELPER, Optional.empty(), givenHelperStationId);
            }
        } catch (SQLException e) {
            replyCallback.reply(Replies.ERROR).queue();
            App.getLogger().error(e);
            return;
        }

        replyCallback.reply(Replies.IAMHELPER_USER_IS_NOW_HELPER).queue();
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData(
                CommandName.IAM_HELPER,
                "Register yourself as a helper",
                new CommandOption[] {
                        new CommandOption(OptionType.STRING, "password", "Het helper wachtwoord"),
                        new CommandOption(OptionType.INTEGER, "post", "Het nummer van de helperpost")
                }
        );
    }
}

