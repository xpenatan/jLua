package lua.sample.fdx.desktop;

import io.github.libfdx.backend.desktopc.DesktopCApplicationBackend;
import io.github.libfdx.backend.desktopc.DesktopCApplicationConfig;
import io.github.libfdx.backend.desktopc.DesktopCOpenGLProvider;
import lua.sample.fdx.LuaFdxApplication;

public final class LuaFdxDesktopCLauncher {
    private LuaFdxDesktopCLauncher() {
    }

    public static void main(String[] args) {
        if(args != null && args.length > 0) {
            System.setProperty("jlua.sample.exitAfterFrames", args[0]);
        }
        System.setProperty("jlua.sample.backend", "libFDX OpenGL TeaVM C");
        System.setProperty("jlua.sample.failFast", "true");

        DesktopCApplicationConfig config = new DesktopCApplicationConfig()
                .title("jLua Basic - libFDX OpenGL TeaVM C")
                .size(640, 480)
                .vSync(true)
                .foregroundFps(60)
                .graphics(new DesktopCOpenGLProvider());

        new DesktopCApplicationBackend().start(config, new LuaFdxApplication());
    }
}
