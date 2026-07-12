package lua.example.basic;

import io.github.libfdx.Fdx;
import io.github.libfdx.application.Application;
import io.github.libfdx.application.ApplicationAdapter;
import io.github.libfdx.core.Logger;
import io.github.libfdx.graphics.GraphicsContext;
import lua.LuaFunction;
import lua.LuaLoader;
import lua.LuaState;
import lua.extension.LuaErrorStatus;
import lua.extension.LuaExt;

public final class LuaBasicApplication extends ApplicationAdapter {
    private static final String SCRIPT =
            "local time = 0.0\n" +
            "function render(delta)\n" +
            "    time = time + delta\n" +
            "    local red = 0.12 + (math.sin(time * 0.90) + 1.0) * 0.20\n" +
            "    local green = 0.16 + (math.sin(time * 1.30 + 1.2) + 1.0) * 0.18\n" +
            "    local blue = 0.24 + (math.sin(time * 1.70 + 2.4) + 1.0) * 0.22\n" +
            "    host_clear(red, green, blue, 1.0)\n" +
            "end\n";

    private Application application;
    private GraphicsContext graphics;
    private Logger logger;
    private LuaExt lua;
    private int exitAfterFrames;
    private boolean failFast;
    private int renderedFrames;
    private boolean ready;
    private boolean failed;
    private String failureMessage;

    @Override
    public void create(Fdx fdx) {
        application = fdx.app();
        graphics = fdx.graphics().main();
        logger = fdx.logger();
        exitAfterFrames = intProperty("jlua.sample.exitAfterFrames", -1);
        failFast = booleanProperty("jlua.sample.failFast", false);
        LuaLoader.init((isSuccess, error) -> {
            if(!isSuccess) {
                fail("jLua failed to initialize", error);
                return;
            }
            lua = new LuaExt();
            registerHostFunctions();
            LuaErrorStatus status = lua.buildScript(SCRIPT);
            if(!status.isValid()) {
                fail(status.getError(), null);
                return;
            }
            ready = true;
            logger.info("jLua libFDX sample initialized"
                    + ", backend=" + System.getProperty("jlua.sample.backend", "default"));
        });
    }

    @Override
    public void render() {
        if(graphics == null) {
            return;
        }
        if(!ready || failed) {
            graphics.clear(0.08f, 0.10f, 0.13f, 1.0f);
            if(failed && failFast) {
                throw new IllegalStateException(failureMessage);
            }
            return;
        }

        LuaState luaState = lua.getLuaState();
        luaState.lua_getglobal("render");
        if(luaState.lua_isfunction(-1) == 0) {
            luaState.lua_pop(1);
            fail("Lua script does not define render(delta)", null);
            return;
        }
        luaState.lua_pushnumber(application.deltaTime());
        LuaErrorStatus status = lua.callFunction(1, 0, 0);
        if(!status.isValid()) {
            fail(status.getError(), null);
            return;
        }

        renderedFrames++;
        if(exitAfterFrames > 0 && renderedFrames >= exitAfterFrames) {
            logger.info("jLua libFDX sample completed " + renderedFrames + " frames");
            application.requestExit();
        }
    }

    @Override
    public void dispose() {
        if(lua != null) {
            lua.dispose();
            lua = null;
        }
    }

    private void registerHostFunctions() {
        lua.registerGlobalFunction("host_clear", new LuaFunction() {
            @Override
            protected int onCall(LuaState luaState) {
                float red = (float)luaState.lua_tonumber(1);
                float green = (float)luaState.lua_tonumber(2);
                float blue = (float)luaState.lua_tonumber(3);
                float alpha = (float)luaState.lua_tonumber(4);
                graphics.clear(red, green, blue, alpha);
                return 0;
            }
        });
    }

    private void fail(String message, Throwable error) {
        failed = true;
        failureMessage = message;
        if(logger == null) {
            return;
        }
        if(error != null) {
            logger.error(message, error);
        }
        else {
            logger.error(message);
        }
    }

    private static int intProperty(String name, int fallback) {
        String value = System.getProperty(name);
        if(value == null || value.trim().length() == 0) {
            return fallback;
        }
        return Integer.parseInt(value.trim());
    }

    private static boolean booleanProperty(String name, boolean fallback) {
        String value = System.getProperty(name);
        if(value == null || value.trim().length() == 0) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
