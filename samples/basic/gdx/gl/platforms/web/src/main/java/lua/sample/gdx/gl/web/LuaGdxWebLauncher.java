package lua.sample.gdx.gl.web;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import lua.sample.gdx.gl.LuaGdxApplication;

public final class LuaGdxWebLauncher {
    private LuaGdxWebLauncher() {
    }

    public static void main(String[] args) {
        if(args != null && args.length > 0) {
            String value = args[0];
            if(value.startsWith("--exit-after-frames=")) {
                value = value.substring("--exit-after-frames=".length());
            }
            System.setProperty("jlua.sample.exitAfterFrames", value);
        }
        System.setProperty("jlua.sample.backend", "libGDX WebGL");

        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;

        new WebApplication(new LuaGdxApplication(), config);
    }
}
