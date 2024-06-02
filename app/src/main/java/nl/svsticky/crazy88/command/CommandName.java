package nl.svsticky.crazy88.command;

public enum CommandName {
    IAM_ADMIN("iamadmin"),
    IAM_HELPER("iamhelper"),
    REGISTER("register"),
    UNLOCK("unlock"),
    SUBMIT("submit"),
    HELPER("helper"),
    MESSAGEME("messageme"),
    ENDGAME("endgame");

    public final String command;

    CommandName(String command) {
        this.command = command;
    }
}
