package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

import java.util.HashMap;

public class ConfigModel {
    @Required
    public AdminModel admin;
    @Required
    public HelperModel helper;
    /**
     * A map mapping the ID of a location to it's configuration
     */
    @Required
    public HashMap<Integer, LocationModel> locations;
    @Required
    public DiscordModel discord;
    @Required
    public DatabaseModel database;
    @Required
    public SubmitModel submit;
    /**
     * A map mapping the ID of a team to the ID of a location
     */
    @Required
    public HashMap<Integer, Integer> teamStartingLocations;
}
