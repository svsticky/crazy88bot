package nl.svsticky.crazy88.config;

import com.google.gson.Gson;
import dev.array21.classvalidator.ClassValidator;
import dev.array21.classvalidator.Pair;
import nl.svsticky.crazy88.config.model.ConfigModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {

    /**
     * Open the configuration file
     * @param path The path to the configuration file
     * @return The parsed configuration file
     * @throws IOException IO error
     * @throws InvalidConfigurationException The configuration was invalid
     */
    public static ConfigModel open(Path path) throws IOException, InvalidConfigurationException {
        if(!Files.exists(path)) {
            throw new IOException("Configuration file does not exist: " + path);
        }

        if(!Files.isRegularFile(path)) {
            throw new IOException("Configuration file is not a file: " + path);
        }

        String configContent = Files.readString(path);
        Gson gson = new Gson();
        ConfigModel config = gson.fromJson(configContent, ConfigModel.class);

        validateConfig(config);

        return config;
    }

    /**
     * Validate the configuration matches requirments
     * @param model The object to validate
     * @throws InvalidConfigurationException The configuration was invalid
     */
    private static void validateConfig(ConfigModel model) throws InvalidConfigurationException {
        Pair<Boolean, String> validationResult = ClassValidator.validateType(model);

        if(validationResult.getA() == null || !validationResult.getA()) {
            throw new InvalidConfigurationException(validationResult.getB());
        }
    }
}
