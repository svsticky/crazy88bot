package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

public class ConfigModel {
    @Required
    public AdminModel admin;
    @Required
    public HelperModel helper;
    @Required
    public LocationModel[] locations;
    @Required
    public DiscordModel discord;
    @Required
    public DatabaseModel database;
}
