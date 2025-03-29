package ca.bertsa.splashscreen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ca.bertsa.splashscreen.PreSplashscreen.CONFIG_DIR;
import static ca.bertsa.splashscreen.PreSplashscreen.MOD_ID;
import static com.mojang.text2speech.Narrator.LOGGER;

public class SplashScreenConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = CONFIG_DIR.resolve(MOD_ID + "/option.json");

    private static SplashConfig config;

    public static SplashConfig getConfig() {
        return config;
    }

    public static void loadConfig() {

        if (!Files.exists(CONFIG_PATH)) {
            LOGGER.info("Creating config file");
            config = new SplashConfig();
            saveConfig();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            SplashConfig data = GSON.fromJson(json, SplashConfig.class);
            if (data != null) {
                config = data;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }

    }

    public static void saveConfig() {
        try {
            if (!Files.exists(CONFIG_DIR.resolve(MOD_ID))) {
                Files.createDirectories(CONFIG_DIR.resolve(MOD_ID));
            }
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public static class SplashConfig {
        private static final float DEFAULT_MULTIPLIER = 1;
        private static final int DEFAULT_HEIGHT = 200;
        private static final int DEFAULT_WIDTH = 500;

        public CustomizeState state = CustomizeState.Off;
        public float multiplier = DEFAULT_MULTIPLIER;
        public int height = DEFAULT_HEIGHT;
        public int width = DEFAULT_WIDTH;
    }
}
