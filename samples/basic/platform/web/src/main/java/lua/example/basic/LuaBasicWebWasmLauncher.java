package lua.example.basic;

public final class LuaBasicWebWasmLauncher {
    private LuaBasicWebWasmLauncher() {
    }

    public static void main(String[] args) {
        LuaBasicWebLauncherSupport.start("Wasm");
    }
}
