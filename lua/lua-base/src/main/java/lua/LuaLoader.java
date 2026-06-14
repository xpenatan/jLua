package lua;

import com.github.xpenatan.jParser.loader.JParserLibraryLoader;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;
import com.github.xpenatan.jparser.runtime.RuntimeLoader;

/**
 * @author xpenatan
 */
public class LuaLoader {

    /*[-JNI;-NATIVE]
        #include "LuaCustom.h"
    */

    /*[-FFM;-NATIVE]
        #include "LuaCustom.h"
    */

    public static void init(JParserLibraryLoaderListener listener) {
        RuntimeLoader.init(new JParserLibraryLoaderListener() {
            @Override
            public void onLoad(boolean isSuccess, Throwable throwable) {
                if(isSuccess) {
                    JParserLibraryLoader.load("lua", listener);
                }
                else {
                    listener.onLoad(false, throwable);
                }
            }
        });
    }
}
