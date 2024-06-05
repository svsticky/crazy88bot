package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

public class DatabaseModel {
    @Required
    public String host;
    @Required
    public String username;
    @Required
    public String password;
    @Required
    public String database;
    @Required
    public int port;
}