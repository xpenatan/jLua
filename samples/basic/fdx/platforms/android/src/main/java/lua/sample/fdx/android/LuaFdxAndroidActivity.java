package lua.sample.fdx.android;

import android.content.Intent;
import io.github.libfdx.application.ApplicationListener;
import io.github.libfdx.backend.android.AndroidApplicationActivity;
import io.github.libfdx.backend.android.AndroidApplicationConfig;
import io.github.libfdx.backend.android.AndroidGlesProvider;
import lua.sample.fdx.LuaFdxApplication;

public final class LuaFdxAndroidActivity extends AndroidApplicationActivity {
    @Override
    protected AndroidApplicationConfig createApplicationConfig() {
        applySampleOptions();
        System.setProperty("jlua.sample.backend", "libFDX OpenGL ES Android JNI");
        return new AndroidApplicationConfig()
                .title("jLua Basic - libFDX OpenGL ES")
                .size(640, 480)
                .vSync(true)
                .foregroundFps(60)
                .graphics(new AndroidGlesProvider());
    }

    @Override
    protected ApplicationListener createApplicationListener() {
        return new LuaFdxApplication();
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
