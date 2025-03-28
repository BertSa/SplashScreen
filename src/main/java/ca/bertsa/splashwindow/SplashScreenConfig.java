package ca.bertsa.splashwindow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ca.bertsa.splashwindow.PreSplashscreen.CONFIG_DIR;
import static ca.bertsa.splashwindow.PreSplashscreen.MOD_ID;
import static com.mojang.text2speech.Narrator.LOGGER;

public class SplashScreenConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = CONFIG_DIR.resolve(MOD_ID + "/option.json");

    private static final boolean DEFAULT_CUSTOMSIZE = false;
    private static final boolean DEFAULT_PRECISE = false;
    private static final float DEFAULT_MULTIPLIER = 1;
    private static final int DEFAULT_HEIGHT = 200;
    private static final int DEFAULT_WIDTH = 500;

    private static SplashConfig conf;

    public static SplashConfig getConf(){
        return conf;
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            LOGGER.info("Creating config file");
            createConfFile();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            SplashConfig data = GSON.fromJson(json, SplashConfig.class);
            if (data != null) {
                conf = data;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }

    }

    private static void createConfFile() {
        SplashConfig data = new SplashConfig();
        data.height = DEFAULT_HEIGHT;
        data.width = DEFAULT_WIDTH;
        data.customSize = DEFAULT_CUSTOMSIZE;
        data.precise = DEFAULT_PRECISE;
        data.multiplier = DEFAULT_MULTIPLIER;
        saveConfig(data);
    }


    public static void saveConfig(SplashConfig config) {
        try {
            Files.createDirectory(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID));
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public static class SplashConfig {
        public boolean customSize;
        public float multiplier;
        public boolean precise;
        int height;
        int width;
    }
}
