package lua.example.basic;

import io.github.libfdx.backend.web.WebApplicationBackend;
import io.github.libfdx.backend.web.WebApplicationConfig;
import io.github.libfdx.graphics.gl.web.WebGLProvider;

final class LuaBasicWebLauncherSupport {
    private static final String CANVAS_ID = "libfdx-canvas";

    private LuaBasicWebLauncherSupport() {
    }

    static void start(String runtimeName) {
        WebApplicationConfig config = new WebApplicationConfig()
                .title("xLua Basic - WebGL " + runtimeName)
                .size(0, 0)
                .canvasId(CANVAS_ID)
                .graphics(new WebGLProvider());

        new WebApplicationBackend().start(config, new LuaBasicApplication());
    }
}
