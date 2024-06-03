package nl.svsticky.crazy88.command;

/**
 * Possible command replies
 */
public class Replies {
    public static final String ERROR = "Er is iets helemaal fout gegaan. Probeer 't straks nog een keer";

    public static final String GAME_ENDED = "Het spel is afgelopen";

    public static final String IAMADMIN_PASSWORD_MISSING = "Het admin wachtwoord was niet opgegeven";
    public static final String IAMADMIN_PASSWORD_INCORRECT = "Het opgegeven wachtwoord is onjuist";
    public static final String IAMADMIN_USER_IS_NOW_ADMIN = "Gefeliciteerd! Je bent nu een admin";

    public static final String IAMHELPER_PASSWORD_MISSING = "Het helper wachtwoord was niet opgegeven";
    public static final String IAMHELPER_PASSWORD_INCORRECT = "Het opgegeven wachtwoord is onjuist";
    public static final String IAMHELPER_USER_IS_NOW_HELPER = "Gefeliciteerd! Je bent nu een helper";
    public static final String IAMHELPER_STATION_ID_MISSING = "Het postnummer was niet opgegeven";
    public static final String IAMHELPER_LOCATION_UNKNOWN = "De opgegeven post bestaat niet";
    public static final String IAMHELPER_ALREADY_HELPER = "Je bent al geregistreerd als helper";

    public static final String UNLOCK_MISSING_TEAM_ID = "Het teamnummer is niet opgegeven";
    public static final String UNLOCK_USER_IS_NOT_HELPER = "Je bent geen helper";
    public static final String UNLOCK_UNKNOWN_TEAM_ID = "Het opgegeven teamnummer bestaat niet";
    public static final String UNLOCK_NEW_ASSIGNMENTS_UNLOCKED = "Success! Het team heeft nieuwe opdrachten ontvangen";
    public static final String UNLOCK_ALREADY_UNLOCKED = "De opdrachten zijn al ontgrendeld voor dit team";


    public static final String REGISTER_ALREADY_REGISTER_DIFFERENT_TEAM = "Je bent al geregistreerd bij een ander team";
    public static final String REGISTER_ALREADY_REGISTER_SAME_TEAM = "Je bent al geregistreerd bij dit team";
    public static final String REGISTER_MISSING_TEAM_ID = "Het teamnummer is niet opgegeven";
    public static final String REGISTER_UNKNOWN_TEAM_ID = "Het opgegeven teamnummer is onbekend";

    public static final String HELPER_USER_NOT_REGISTERED = "Je bent nog niet geregistreerd";
    public static final String HELPER_ALL_LOCATIONS_UNLOCKED = "Je hebt alle locaties al ontgrendeld";

    public static final String MESSAGEME_WELCOME = "Hoi! Welkom bij de Crazy88 van Studievereniging Sticky! Gebruik het /register commando om je aan te melden";
    public static final String MESSAGME_OK = "Oke!";

    public static final String SUBMIT_USER_NOT_REGISTERED = "Je bent nog niet geregistreerd";
    public static final String SUBMIT_MISSING_ASSIGNMENT_ID = "Het opdrachtnummer was niet opgegeven";
    public static final String SUBMIT_MISSING_ASSIGNMENT = "De opdracht bevatte geen of geen geldig bestand";
    public static final String SUBMIT_ASSIGNMENT_NOT_UNLOCKED = "De opgegeven opdracht is not niet ontgrendeld of bestaat niet";
    public static final String SUBMIT_OK = "De opdracht is ingestuurd!";
}
