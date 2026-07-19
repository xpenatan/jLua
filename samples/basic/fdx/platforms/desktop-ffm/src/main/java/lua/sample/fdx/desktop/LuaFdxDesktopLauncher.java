package lua.sample.fdx.desktop;

import io.github.libfdx.backend.desktop.DesktopApplicationBackend;
import io.github.libfdx.backend.desktop.DesktopApplicationConfig;
import io.github.libfdx.backend.desktop.DesktopOpenGLProvider;
import lua.sample.fdx.LuaFdxApplication;

public final class LuaFdxDesktopLauncher {
    private LuaFdxDesktopLauncher() {
    }

    public static void main(String[] args) {
        DesktopApplicationConfig config = new DesktopApplicationConfig()
                .title("jLua Basic - libFDX OpenGL FFM")
                .size(640, 480)
                .vSync(true)
                .foregroundFps(60)
                .graphics(new DesktopOpenGLProvider());

        new DesktopApplicationBackend().start(config, new LuaFdxApplication());
    }
}
