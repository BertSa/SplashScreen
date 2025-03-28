package ca.bertsa.splashwindow;

import java.awt.*;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreSplashscreen implements PreLaunchEntrypoint {
    private static final String PNG_NAME = "splash.png";
    public static final String MOD_ID = "splashscreen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path CONFIG_PATH = CONFIG_DIR.resolve(MOD_ID + "/option.json");
    public static final Path imagePath = CONFIG_DIR.resolve(MOD_ID + "/" + PNG_NAME);

    private static final boolean DEFAULT_CUSTOMSIZE = false;
    private static final boolean DEFAULT_PRECISE = false;
    private static final float DEFAULT_MULTIPLIER = 1;


    private static int width;
    private static int height;
    private static boolean customSize = DEFAULT_CUSTOMSIZE;
    private static boolean precise = DEFAULT_PRECISE;
    private static float multiplier = DEFAULT_MULTIPLIER;

    public static JFrame frame;

    public void onPreLaunch() {
        LOGGER.info("Pre Launch");
        try {
            this.initSplashscreen();
        } catch (Exception ignored) {
        }

    }

    private void initSplashscreen() throws IOException {
        loadConfig();

        frame = new JFrame("Minecraft");

        BufferedImage bi;
        if (Files.exists(imagePath)) {
            File file = imagePath.toFile();
            bi = ImageIO.read(file);
        } else {
            InputStream stream = PreSplashscreen.class.getResourceAsStream("/assets/" + PNG_NAME);
            assert stream != null;
            bi = ImageIO.read(stream);
        }

        if (!customSize) {
            height = bi.getHeight();
            width = bi.getWidth();
        } else if (!precise) {
            height = Math.round(bi.getHeight() * multiplier);
            width = Math.round(bi.getWidth() * multiplier);
        }

        ImageIcon img = new ImageIcon(bi.getScaledInstance(width, height, 4));

        JLabel label = new JLabel("", 0);
        label.setBackground(new Color(0, 0, 0, 0));
        label.setSize(width, height);
        label.setIcon(img);
        label.setOpaque(true);

        frame.getContentPane().setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));

        PreSplashscreen.FrameDragListener frameDragListener = new PreSplashscreen.FrameDragListener(frame);
        frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);

        frame.setType(Type.UTILITY);
        frame.setDefaultCloseOperation(3);
        frame.setUndecorated(true);
        frame.setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
        frame.setContentPane(label);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    public static void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            LOGGER.info("Creating config file");
            saveConfig();
            return;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            Data data = GSON.fromJson(json, Data.class);
            if (data != null) {
                height = data.height;
                width = data.width;
                multiplier = data.multiplier;
                customSize = data.customSize;
                precise = data.precise;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }

    }

    private static Data toData() {
        Data data = new Data();
        data.height = height;
        data.width = width;
        data.customSize = customSize;
        data.multiplier = multiplier;
        data.precise = precise;
        return data;
    }

    public static void saveConfig() {
        try {
            Data data = toData();
            Files.createDirectory(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID));
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private static class Data {
        public boolean customSize;
        public float multiplier;
        public boolean precise;
        int height;
        int width;
    }

    public static class FrameDragListener extends MouseAdapter {
        private final JFrame frame;
        private Point coords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            this.coords = null;
        }

        public void mousePressed(MouseEvent e) {
            this.coords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            this.frame.setLocation(currCoords.x - this.coords.x, currCoords.y - this.coords.y);
        }
    }
}
