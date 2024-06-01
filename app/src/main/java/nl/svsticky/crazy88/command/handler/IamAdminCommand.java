package nl.svsticky.crazy88.command.handler;

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
import nl.svsticky.crazy88.database.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class IamAdminCommand implements CommandHandler {

    private final Driver driver;
    private final ConfigModel config;

    public IamAdminCommand(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    @Override
    public void handle(IReplyCallback replyCallback, long userId, List<OptionMapping> options) {
        Optional<String> givenPassword = options.stream()
                .filter(v -> v.getName().equals("password"))
                .findFirst()
                .map(OptionMapping::getAsString);

        if(givenPassword.isEmpty()) {
            replyCallback.reply(Replies.ADMIN_PASSWORD_MISSING).queue();
            return;
        }

        if(!givenPassword.get().equals(config.admin.password)) {
            replyCallback.reply(Replies.ADMIN_PASSWORD_INCORRECT).queue();
            return;
        }

        try {
            Optional<User> user = User.getById(this.driver, userId);
            if(user.isPresent()) {
                user.get().setUserType(User.UserType.ADMIN);
            } else {
                User.create(this.driver, userId, User.UserType.ADMIN, Optional.empty(), Optional.empty());
            }
        } catch (SQLException e) {
            replyCallback.reply(Replies.ERROR).queue();
            return;
        }

        replyCallback.reply(Replies.USER_IS_NOW_ADMIN).queue();
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
