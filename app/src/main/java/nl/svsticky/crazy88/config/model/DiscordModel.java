package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

public class DiscordModel {
    /**
     * The API token as found on discord.com/developers
     */
    @Required
    public String token;
}
