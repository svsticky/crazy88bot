package nl.svsticky.crazy88;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import nl.svsticky.crazy88.command.CommandHandler;
import nl.svsticky.crazy88.command.CommandManager;
import nl.svsticky.crazy88.config.ConfigHandler;
import nl.svsticky.crazy88.config.InvalidConfigurationException;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.config.model.DatabaseModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.events.SlashCommandListener;
import nl.svsticky.crazy88.http.HttpServer;
import nl.svsticky.crazy88.http.routes.FaviconRoute;
import nl.svsticky.crazy88.http.routes.submissions.GetSubmissionRoute;
import nl.svsticky.crazy88.http.routes.submissions.GradeSubmissionRoute;
import nl.svsticky.crazy88.http.routes.submissions.ListSubmissionsRoute;
import nl.svsticky.crazy88.http.routes.teams.ListTeamsRoute;
import nl.svsticky.crazy88.http.routes.teams.ScoreRoute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;

public class App {

    protected static Logger logger;
    private static JDA jdaInstance;

    public static Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) {
        logger = LogManager.getLogger(App.class);
        logger.info("Starting SVSticky Crazy88 Bot");

        ConfigModel config = openConfig();
        Driver database = openDatabase(config.database);

        startHttpServer(database, config);
        openJda(config, database);
    }

    /**
     * Start the internal HTTP server
     * @param driver The database driver
     * @param configModel The configuration
     */
    private static void startHttpServer(Driver driver, ConfigModel configModel) {
        HttpServer httpServer = new HttpServer(9001);
        httpServer.registerRoute("/favicon.ico", new FaviconRoute());
        httpServer.registerRoute("/teams/list", new ListTeamsRoute(driver));
        httpServer.registerRoute("/teams/score", new ScoreRoute(driver));
        httpServer.registerRoute("/submissions/list", new ListSubmissionsRoute(driver));
        httpServer.registerRoute("/submissions", new GetSubmissionRoute(configModel, driver));
        httpServer.registerRoute("/submissions/grade", new GradeSubmissionRoute(driver));

        try {
            httpServer.start();
        } catch (IOException e) {
            App.getLogger().error(e);
            System.exit(1);
        }
    }

    /**
     * Open a database connection
     * @param databaseModel The database configuration
     * @return The database driver
     */
    private static Driver openDatabase(DatabaseModel databaseModel) {
        try {
            Driver database = new Driver(databaseModel);
            database.applyMigrations();

            return database;
        } catch(IOException e) {
            logger.error("Failed to setup database (IO): ", e);
            System.exit(1);
            return null;
        } catch (SQLException e) {
            logger.error("Failed to setup database (SQL): ", e);
            System.exit(1);
            return null;
        }
    }

    /**
     * Start the JDA instance, i.e., the connection to Discord
     * @param config The configuration
     * @param driver The database driver
     */
    private static void openJda(ConfigModel config, Driver driver)  {
        CommandManager commandManager = new CommandManager(driver, config);
        try {
            jdaInstance = JDABuilder.createLight(config.discord.token)
                    .addEventListeners(new SlashCommandListener(commandManager))
                    .setEnabledIntents(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_PRESENCES
                    )
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build()
                    .awaitReady();

            jdaInstance.updateCommands().addCommands(
                    // Iterate over all handlers
                    Arrays.stream(commandManager.getHandlers())
                            // Retrieve command data
                            .map(CommandHandler::getCommandData)
                            // Convert data to slash command
                            .map(data -> Commands.slash(data.commandName().command, data.description())
                                    // Add options
                                    .addOptions(Arrays.stream(data.options())
                                            .map(option -> new OptionData(option.type(), option.name(), option.description(), true))
                                            .toList()
                                    )
                            )
                            .toList()
            ).queue();

        } catch (InvalidTokenException e) {
            logger.error("Invalid discord token provided: ", e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for Discord connection to be ready: ", e);
            System.exit(1);
        }
    }

    /**
     * Get the JDA instance, i.e., the connection to Discord
     * @return The JDA instance
     */
    public static JDA getJdaInstance() {
        return jdaInstance;
    }

    /**
     * Open the configuration file
     * @return The parsed configuration
     */
    private static ConfigModel openConfig() {
        String configPathStr = System.getenv("CONFIG_PATH");
        if(configPathStr == null) {
            logger.error("The environmental variable CONFIG_PATH is not set");
            System.exit(1);
            return null;
        }

        logger.debug("Opening configuration file");

        try {
            Path configPath = Paths.get(configPathStr);
            Path absolute = configPath.toAbsolutePath();
            return ConfigHandler.open(absolute);
        } catch (IOException | InvalidConfigurationException | InvalidPathException e) {
            logger.error("Failed to open configuration file", e);
            System.exit(1);
            return null;
        }
    }
}
