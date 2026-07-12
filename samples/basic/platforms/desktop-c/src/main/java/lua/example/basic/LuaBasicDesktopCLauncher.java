package lua.example.basic;

import io.github.libfdx.backend.desktopc.DesktopCApplicationBackend;
import io.github.libfdx.backend.desktopc.DesktopCApplicationConfig;
import io.github.libfdx.backend.desktopc.DesktopCOpenGLProvider;

public final class LuaBasicDesktopCLauncher {
    private LuaBasicDesktopCLauncher() {
    }

    public static void main(String[] args) {
        if(args != null && args.length > 0) {
            System.setProperty("jlua.sample.exitAfterFrames", args[0]);
        }
        System.setProperty("jlua.sample.backend", "TeaVM C");
        System.setProperty("jlua.sample.failFast", "true");

        DesktopCApplicationConfig config = new DesktopCApplicationConfig()
                .title("jLua Basic - libFDX TeaVM C")
                .size(640, 480)
                .vSync(true)
                .foregroundFps(60)
                .graphics(new DesktopCOpenGLProvider());

        new DesktopCApplicationBackend().start(config, new LuaBasicApplication());
    }
}
