package lua.sample.gdx.gl.desktop;

import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import lua.sample.gdx.gl.LuaGdxApplication;

public final class LuaGdxDesktopCLauncher {
    private LuaGdxDesktopCLauncher() {
    }

    public static void main(String[] args) {
        if(args != null && args.length > 0) {
            String value = args[0];
            if(value.startsWith("--exit-after-frames=")) {
                value = value.substring("--exit-after-frames=".length());
            }
            System.setProperty("jlua.sample.exitAfterFrames", value);
        }
        System.setProperty("jlua.sample.backend", "libGDX OpenGL TeaVM C");
        System.setProperty("jlua.sample.failFast", "true");

        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("jLua Basic - libGDX OpenGL TeaVM C");
        config.setWindowedMode(640, 480);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new GLFWApplication(new LuaGdxApplication(), config);
    }
}
