package lua.example.basic;

import io.github.libfdx.backend.desktop.DesktopApplicationBackend;
import io.github.libfdx.backend.desktop.DesktopApplicationConfig;
import io.github.libfdx.backend.desktop.DesktopOpenGLProvider;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        String backend = System.getProperty("jlua.sample.backend", "Desktop");
        DesktopApplicationConfig config = new DesktopApplicationConfig()
                .title("jLua Basic - libFDX " + backend)
                .size(640, 480)
                .vSync(true)
                .foregroundFps(60)
                .graphics(new DesktopOpenGLProvider());

        new DesktopApplicationBackend().start(config, new LuaBasicApplication());
    }
}
