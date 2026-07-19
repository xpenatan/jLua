package lua.sample.fdx.web;

import io.github.libfdx.backend.web.WebApplicationBackend;
import io.github.libfdx.backend.web.WebApplicationConfig;
import io.github.libfdx.graphics.gl.web.WebGLProvider;
import lua.sample.fdx.LuaFdxApplication;

final class LuaFdxWebLauncherSupport {
    private static final String CANVAS_ID = "libfdx-canvas";

    private LuaFdxWebLauncherSupport() {
    }

    static void start(String runtimeName) {
        System.setProperty("jlua.sample.backend", "libFDX WebGL " + runtimeName);
        WebApplicationConfig config = new WebApplicationConfig()
                .title("jLua Basic - libFDX WebGL " + runtimeName)
                .size(0, 0)
                .canvasId(CANVAS_ID)
                .graphics(new WebGLProvider());

        new WebApplicationBackend().start(config, new LuaFdxApplication());
    }
}
