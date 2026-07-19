package lua.sample.gdx.gl.android;

import android.content.Intent;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import lua.sample.gdx.gl.LuaGdxApplication;

public final class LuaGdxAndroidActivity extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySampleOptions();
        System.setProperty("jlua.sample.backend", "libGDX OpenGL ES Android JNI");

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true;
        config.useWakelock = true;
        config.useGL30 = true;
        initialize(new LuaGdxApplication(), config);
    }

    private void applySampleOptions() {
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }
        setSystemPropertyFromExtra(intent, "jlua.sample.exitAfterFrames");
        setSystemPropertyFromExtra(intent, "jlua.sample.failFast");
    }

    private void setSystemPropertyFromExtra(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        if(value != null && value.trim().length() > 0) {
            System.setProperty(key, value.trim());
        }
    }
}
