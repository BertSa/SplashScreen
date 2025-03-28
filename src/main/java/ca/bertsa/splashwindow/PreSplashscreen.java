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

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("splashscreen/option.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static int width = 500;
    private static int height = 100;
    private static boolean customSize = false;

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
        if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve("splashscreen/" + PNG_NAME))) {
            File file = FabricLoader.getInstance().getConfigDir().resolve("splashscreen/" + PNG_NAME).toFile();
            bi = ImageIO.read(file);
        } else {
            InputStream stream = PreSplashscreen.class.getResourceAsStream("/assets/" + PNG_NAME);
            assert stream != null;
            bi = ImageIO.read(stream);
        }

        if (!customSize) {
            height = bi.getHeight();
            width = bi.getWidth();
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
                customSize = data.customSize;
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
        return data;
    }

    public static void saveConfig() {
        try {
            Data data = toData();
            Files.createDirectory(FabricLoader.getInstance().getConfigDir().resolve("splashscreen"));
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private static class Data {
        public boolean customSize;
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
