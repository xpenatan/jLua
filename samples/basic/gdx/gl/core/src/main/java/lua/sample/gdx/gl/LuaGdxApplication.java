package lua.sample.gdx.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import lua.sample.shared.LuaBasicSample;
import lua.sample.shared.LuaBasicSampleHost;

public final class LuaGdxApplication extends ApplicationAdapter implements LuaBasicSampleHost {
    private LuaBasicSample sample;

    @Override
    public void create() {
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
        return System.getProperty("jlua.sample.backend", "libGDX OpenGL");
    }

    @Override
    public float deltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

    @Override
    public void clear(float red, float green, float blue, float alpha) {
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void log(String message) {
        Gdx.app.log("jLua", message);
    }

    @Override
    public void error(String message, Throwable error) {
        if(error != null) {
            Gdx.app.error("jLua", message, error);
        }
        else {
            Gdx.app.error("jLua", message);
        }
    }

    @Override
    public void requestExit() {
        Gdx.app.exit();
    }
}
