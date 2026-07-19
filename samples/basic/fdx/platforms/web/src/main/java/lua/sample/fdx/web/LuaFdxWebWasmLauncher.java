package lua.sample.fdx.web;

public final class LuaFdxWebWasmLauncher {
    private LuaFdxWebWasmLauncher() {
    }

    public static void main(String[] args) {
        LuaFdxWebLauncherSupport.start("Wasm");
    }
}
