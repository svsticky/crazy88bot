package nl.svsticky.crazy88.events;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.svsticky.crazy88.command.*;
import nl.svsticky.crazy88.command.handler.*;

public class SlashCommandListener extends ListenerAdapter {
    static final String COMMAND_IAM_ADMIN = "iamadmin";
    static final String COMMAND_IAM_HELPER = "iamhelper";
    static final String COMMAND_REGISTER = "register";
    static final String COMMAND_UNLOCK = "unlock";
    static final String COMMAND_SUBMIT = "submit";
    static final String COMMAND_HELPER = "helper";
    static final String COMMAND_ENDGAME = "endgame";

    static final String INVALID_COMMAND_REPLY = "Invalid command";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandHandler handler = switch(event.getName()) {
            case COMMAND_IAM_ADMIN -> new IamAdminCommand();
            case COMMAND_IAM_HELPER -> new IamHelperCommand();
            case COMMAND_REGISTER -> new RegisterCommand();
            case COMMAND_UNLOCK -> new UnlockCommand();
            case COMMAND_SUBMIT -> new SubmitCommand();
            case COMMAND_HELPER -> new HelperCommand();
            case COMMAND_ENDGAME -> new EndGameCommand();
            default -> {
                event
                        .reply(INVALID_COMMAND_REPLY)
                        .queue();
                yield null;
            }
        };

        // Java's stupidy, can't return from the function inside the switch body
        if(handler == null) return;

        long user = event.getUser().getIdLong();
        handler.handle(event, user, event.getOptions());
    }
}
