package lua.sample.gdx.gl.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import lua.sample.gdx.gl.LuaGdxApplication;

public final class LuaGdxDesktopLauncher {
    private LuaGdxDesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("jLua Basic - libGDX OpenGL FFM");
        config.setWindowedMode(640, 480);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new Lwjgl3Application(new LuaGdxApplication(), config);
    }
}
