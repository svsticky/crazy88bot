package nl.svsticky.crazy88.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class SlashCommandListener extends ListenerAdapter {
    private final CommandManager commandManager;

    static final String INVALID_COMMAND_REPLY = "Invalid command";

    public SlashCommandListener(CommandManager manager) {
        this.commandManager = manager;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        CommandHandler[] handlers = this.commandManager.getHandlers();
        Optional<CommandHandler> handler = Arrays.stream(handlers)
                .filter(v -> v.getCommandData().commandName().command.equals(event.getName()))
                .findFirst();

        if(handler.isEmpty()) {
            event
                    .reply(INVALID_COMMAND_REPLY)
                    .queue();
        } else {
            long user = event.getUser().getIdLong();
            handler
                    .get()
                    .handle(event, user, event.getOptions());
        }
    }
}
