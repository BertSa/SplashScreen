package ca.bertsa.splashwindow;

import net.fabricmc.api.ModInitializer;


public class Splashscreen implements ModInitializer {
    public void onInitialize() {
        if (PreSplashscreen.frame != null) {
            PreSplashscreen.frame.setVisible(false);
            PreSplashscreen.frame.dispose();
            PreSplashscreen.frame = null;
        }
    }
}
