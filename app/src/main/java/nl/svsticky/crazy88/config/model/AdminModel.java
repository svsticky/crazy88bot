package nl.svsticky.crazy88.config.model;

import dev.array21.classvalidator.annotations.Required;

public class AdminModel {
    /**
     * The password for the `iamadmin` command
     */
    @Required
    public String password;
}
