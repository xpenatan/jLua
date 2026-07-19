package lua.sample.fdx;

import io.github.libfdx.Fdx;
import io.github.libfdx.application.Application;
import io.github.libfdx.application.ApplicationAdapter;
import io.github.libfdx.core.Logger;
import io.github.libfdx.graphics.GraphicsContext;
import lua.sample.shared.LuaBasicSample;
import lua.sample.shared.LuaBasicSampleHost;

public final class LuaFdxApplication extends ApplicationAdapter implements LuaBasicSampleHost {
    private Application application;
    private GraphicsContext graphics;
    private Logger logger;
    private LuaBasicSample sample;

    @Override
    public void create(Fdx fdx) {
        application = fdx.app();
        graphics = fdx.graphics().main();
        logger = fdx.logger();
        sample = new LuaBasicSample(this);
        sample.create();
    }

    @Override
    public void render() {
        if(sample != null) {
            sample.render();
        }
    }

    @Override
    public void dispose() {
        if(sample != null) {
            sample.dispose();
            sample = null;
        }
    }

    @Override
    public String backendName() {
        return System.getProperty("jlua.sample.backend", "libFDX");
    }

    @Override
    public float deltaTime() {
        return application.deltaTime();
    }

    @Override
    public void clear(float red, float green, float blue, float alpha) {
        graphics.clear(red, green, blue, alpha);
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Override
    public void error(String message, Throwable error) {
        if(error != null) {
            logger.error(message, error);
        }
        else {
            logger.error(message);
        }
    }

    @Override
    public void requestExit() {
        application.requestExit();
    }
}
