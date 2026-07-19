package lua.sample.shared;

public interface LuaBasicSampleHost {
    String backendName();

    float deltaTime();

    void clear(float red, float green, float blue, float alpha);

    void log(String message);

    void error(String message, Throwable error);

    void requestExit();
}
