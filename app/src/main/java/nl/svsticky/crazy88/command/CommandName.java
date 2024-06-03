package nl.svsticky.crazy88.command;

/**
 * The name of the command
 */
public enum CommandName {
    // The value must be all lowercase
    IAM_ADMIN("iamadmin"),
    IAM_HELPER("iamhelper"),
    REGISTER("register"),
    UNLOCK("unlock"),
    SUBMIT("submit"),
    HELPER("helper"),
    MESSAGEME("messageme"),
    ENDGAME("endgame");

    /**
     * The discord name of the command
     */
    public final String command;

    CommandName(String command) {
        this.command = command;
    }
}
