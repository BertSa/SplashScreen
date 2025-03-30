package ca.bertsa.splashscreen;

import ca.bertsa.splashscreen.SplashScreenConfig.SplashConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import javax.imageio.ImageIO;
import javax.swing.*;
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

public class PreSplashscreen implements PreLaunchEntrypoint {
    public static final String MOD_ID = "splashscreen";
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();

    private static final String PNG_NAME = "splash.png";
    private static final Path IMAGE_PATH = CONFIG_DIR.resolve(MOD_ID + "/" + PNG_NAME);

    public static JFrame frame;

    private SplashConfig config;

    public void onPreLaunch() {
        SplashScreenConfig.loadConfig();
        config = SplashScreenConfig.getConfig();
        try {
            this.initSplashscreen();
        } catch (Exception ignored) {
        }

    }

    private void initSplashscreen() throws IOException {
        frame = new JFrame("Minecraft");

        BufferedImage bi;
        if (Files.exists(IMAGE_PATH)) {
            File file = IMAGE_PATH.toFile();
            bi = ImageIO.read(file);
        } else {
            InputStream stream = PreSplashscreen.class.getResourceAsStream("/assets/" + MOD_ID + "/" + PNG_NAME);
            assert stream != null;
            bi = ImageIO.read(stream);
        }

        int w, h;
        switch (config.state) {
            case Precise:
                h = config.height;
                w = config.width;
                break;
            case Multiply:
                h = Math.round(bi.getHeight() * config.multiplier);
                w = Math.round(bi.getWidth() * config.multiplier);
                break;
            default:
                h = config.height = bi.getHeight();
                w = config.width = bi.getWidth();
                break;
        }

        ImageIcon img = new ImageIcon(bi.getScaledInstance(w, h, Image.SCALE_SMOOTH));

        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setBackground(new Color(0, 0, 0, 0));
        label.setSize(w, h);
        label.setIcon(img);
        label.setOpaque(true);

        PreSplashscreen.FrameDragListener frameDragListener = new PreSplashscreen.FrameDragListener(frame);
        frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);

        frame.getContentPane().setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
        frame.setType(Type.UTILITY);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.setUndecorated(true);
        frame.setBackground(new Color(1.0F, 1.0F, 1.0F, 0.0F));
        frame.setContentPane(label);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }


    public static class FrameDragListener extends MouseAdapter {
        private final JFrame frame;
        private Point coords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mousePressed(MouseEvent e) {
            this.coords = e.getPoint();
        }

        public void mouseReleased(MouseEvent e) {
            this.coords = null;
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            this.frame.setLocation(currCoords.x - this.coords.x, currCoords.y - this.coords.y);
        }
    }
}
