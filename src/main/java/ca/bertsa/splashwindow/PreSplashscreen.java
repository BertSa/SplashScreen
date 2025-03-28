package ca.bertsa.splashwindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreSplashscreen implements PreLaunchEntrypoint {
    public static final String MOD_ID = "splashscreen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static JFrame frame;

    public void onPreLaunch() {
        LOGGER.info("Pre Launch");
        try {
            this.initSplash();
        } catch (Exception ignored) {
        }

    }

    private void initSplash() throws IOException {
        int w = 500;
        int h = 100;
        frame = new JFrame("Minecraft");
        InputStream stream = FabricLoader.class.getClassLoader().getResourceAsStream("assets/splash.png");

        assert stream != null;

        ImageIcon img = new ImageIcon(ImageIO.read(stream).getScaledInstance(w, h, 4));

        JLabel label = new JLabel("", 0);
        label.setBackground(new Color(0, 0, 0, 0));
        label.setSize(w, h);
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
        frame.setSize(w, h);
        frame.setLocationRelativeTo((Component)null);
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
