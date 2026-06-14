import com.github.xpenatan.jParser.builder.BuildMultiTarget;
import com.github.xpenatan.jParser.builder.targets.AndroidTarget;
import com.github.xpenatan.jParser.builder.targets.EmscriptenTarget;
import com.github.xpenatan.jParser.builder.targets.LinuxTarget;
import com.github.xpenatan.jParser.builder.targets.MacTarget;
import com.github.xpenatan.jParser.builder.targets.SourceLanguage;
import com.github.xpenatan.jParser.builder.targets.WindowsMSVCTarget;
import com.github.xpenatan.jParser.builder.tool.BuildToolListener;
import com.github.xpenatan.jParser.builder.tool.BuildToolOptions;
import com.github.xpenatan.jParser.builder.tool.BuilderTool;
import com.github.xpenatan.jParser.idl.IDLReader;
import com.github.xpenatan.jParser.idl.IDLRenaming;
import java.util.ArrayList;

public class Build {

    public static void main(String[] args) {
        String libName = "lua";
        String modulePrefix = "lua";
        String basePackage = "lua";
        String sourcePath =  "/build/lua";

        BuildToolOptions.BuildToolParams data = new BuildToolOptions.BuildToolParams();
        data.libName = libName;
        data.idlName = libName + "54";
        data.webModuleName = libName;
        data.packageName = basePackage;
        data.cppSourcePath = sourcePath;
        data.modulePrefix = modulePrefix;
        data.moduleJNISuffix = "-jni";
        data.moduleFFMSuffix = "-ffm";
        data.moduleWebSuffix = "-web";

        BuildToolOptions op = new BuildToolOptions(data, args);
        op.addAdditionalIDLRefPath(IDLReader.getRuntimeHelperFile());
        BuilderTool.build(op, new BuildToolListener() {
            @Override
            public void onAddTarget(BuildToolOptions op, IDLReader idlReader, ArrayList<BuildMultiTarget> targets) {
                if(op.containsArg("web")) {
                    targets.add(getWebTarget(op, idlReader));
                }
                if(op.containsArg("windows64_jni")) {
                    targets.add(getWindowTarget(op, false));
                }
                if(op.containsArg("windows64_ffm")) {
                    targets.add(getWindowTarget(op, true));
                }
                if(op.containsArg("linux64_jni")) {
                    targets.add(getLinuxTarget(op, false));
                }
                if(op.containsArg("linux64_ffm")) {
                    targets.add(getLinuxTarget(op, true));
                }
                if(op.containsArg("mac64_jni")) {
                    targets.add(getMacTarget(op, false, false));
                }
                if(op.containsArg("mac64_ffm")) {
                    targets.add(getMacTarget(op, false, true));
                }
                if(op.containsArg("macArm_jni")) {
                    targets.add(getMacTarget(op, true, false));
                }
                if(op.containsArg("macArm_ffm")) {
                    targets.add(getMacTarget(op, true, true));
                }
                if(op.containsArg("android_jni")) {
                    targets.add(getAndroidTarget(op));
                }
//                if(op.containsArg("iOS")) {
//                    targets.add(getIOSTarget(op));
//                }
            }
        }, new IDLRenaming() {
            @Override
            public String getIDLMethodName(String methodName) {
                if(methodName.startsWith("x_")) {
                    methodName = methodName.substring(2);
                }
                return methodName;
            }
        });
    }

    private static BuildMultiTarget getWindowTarget(BuildToolOptions op, boolean isFFM) {
        BuildMultiTarget multiTarget = new BuildMultiTarget();
        String libBuildCPPPath = op.getModuleBuildCPPPath();
        String sourceDir = op.getSourceDir();
        String api = isFFM ? "ffm" : "jni";

//        WindowsMSVCTarget.DEBUG_BUILD = true;

        // Make a static library
        WindowsMSVCTarget windowsTarget = new WindowsMSVCTarget();
        windowsTarget.isStatic = true;
        windowsTarget.headerDirs.add("-I" + sourceDir);
        windowsTarget.cppInclude.add(sourceDir + "/*.c");
        windowsTarget.cppExclude.add(sourceDir + "/lua.c");
        windowsTarget.cppExclude.add(sourceDir + "/ltests.c");
        windowsTarget.cppExclude.add(sourceDir + "/onelua.c");
        multiTarget.add(windowsTarget);

        // Compile glue code and link
        WindowsMSVCTarget linkTarget = new WindowsMSVCTarget();
        linkTarget.libDirSuffix += api;
        linkTarget.cppFlags.add("-std:c++17");
        linkTarget.headerDirs.add("-I" + sourceDir);
        linkTarget.headerDirs.add("-I" + op.getCustomSourceDir());
        if(isFFM) {
            linkTarget.setupFFMGlueCode(libBuildCPPPath);
        }
        else {
            linkTarget.setupJNIGlueCode(libBuildCPPPath);
        }
        linkTarget.linkerFlags.add("/DLL");
        linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/windows/vc/lua64_.lib");
        multiTarget.add(linkTarget);

        return multiTarget;
    }

    private static BuildMultiTarget getLinuxTarget(BuildToolOptions op, boolean isFFM) {
        BuildMultiTarget multiTarget = new BuildMultiTarget();
        String libBuildCPPPath = op.getModuleBuildCPPPath();
        String sourceDir = op.getSourceDir();
        String api = isFFM ? "ffm" : "jni";

        // Make a static library
        LinuxTarget linuxTarget = new LinuxTarget(SourceLanguage.C);
        linuxTarget.isStatic = true;
        linuxTarget.headerDirs.add("-I" + sourceDir);
        linuxTarget.cppInclude.add(sourceDir + "/*.c");
        linuxTarget.cppExclude.add(sourceDir + "/lua.c");
        linuxTarget.cppExclude.add(sourceDir + "/ltests.c");
        linuxTarget.cppExclude.add(sourceDir + "/onelua.c");
        multiTarget.add(linuxTarget);

        // Compile glue code and link
        LinuxTarget linkTarget = new LinuxTarget();
        linkTarget.libDirSuffix += api;
        linkTarget.cppFlags.add("-std=c++17");
        linkTarget.headerDirs.add("-I" + sourceDir);
        linkTarget.headerDirs.add("-I" + op.getCustomSourceDir());
        linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/linux/liblua64_.a");
        if(isFFM) {
            linkTarget.setupFFMGlueCode(libBuildCPPPath);
        }
        else {
            linkTarget.setupJNIGlueCode(libBuildCPPPath);
        }
        multiTarget.add(linkTarget);

        return multiTarget;
    }

    private static BuildMultiTarget getMacTarget(BuildToolOptions op, boolean isArm, boolean isFFM) {
        BuildMultiTarget multiTarget = new BuildMultiTarget();
        String libBuildCPPPath = op.getModuleBuildCPPPath();
        String sourceDir = op.getSourceDir();
        String api = isFFM ? "ffm" : "jni";

        // Make a static library
        MacTarget macTarget = new MacTarget(SourceLanguage.C, isArm);
        macTarget.isStatic = true;
        macTarget.headerDirs.add("-I" + sourceDir);
        macTarget.cppInclude.add(sourceDir + "/*.c");
        macTarget.cppExclude.add(sourceDir + "/lua.c");
        macTarget.cppExclude.add(sourceDir + "/ltests.c");
        macTarget.cppExclude.add(sourceDir + "/onelua.c");
        multiTarget.add(macTarget);

        // Compile glue code and link
        MacTarget linkTarget = new MacTarget(isArm);
        linkTarget.libDirSuffix += api;
        linkTarget.cppFlags.add("-std=c++17");
        linkTarget.headerDirs.add("-I" + sourceDir);
        linkTarget.headerDirs.add("-I" + op.getCustomSourceDir());
        if(isArm) {
            linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/mac/arm/liblua64_.a");
        }
        else {
            linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/mac/liblua64_.a");
        }
        if(isFFM) {
            linkTarget.setupFFMGlueCode(libBuildCPPPath);
        }
        else {
            linkTarget.setupJNIGlueCode(libBuildCPPPath);
        }
        multiTarget.add(linkTarget);

        return multiTarget;
    }

    private static BuildMultiTarget getWebTarget(BuildToolOptions op, IDLReader idlReader) {
        BuildMultiTarget multiTarget = new BuildMultiTarget();
        String libBuildCPPPath = op.getModuleBuildCPPPath();
        String sourceDir = op.getSourceDir();

        EmscriptenTarget.DEBUG_BUILD = false;

        // Make a static library
        EmscriptenTarget libTarget = new EmscriptenTarget(SourceLanguage.C);
        libTarget.idlReader = idlReader;
        libTarget.isStatic = true;
        libTarget.cppInclude.add(sourceDir + "/*.c");
        libTarget.cppExclude.add(sourceDir + "/lua.c");
        libTarget.cppExclude.add(sourceDir + "/ltests.c");
        libTarget.cppExclude.add(sourceDir + "/onelua.c");
        libTarget.compileGlueCode = false;
        libTarget.headerDirs.add("-I" + sourceDir);
        multiTarget.add(libTarget);

        // Compile glue code and link
        EmscriptenTarget linkTarget = new EmscriptenTarget();
        linkTarget.idlReader = idlReader;
        linkTarget.cppInclude.add(libBuildCPPPath + "/src/runtime/RuntimeHelper.cpp");
        linkTarget.exportedRuntimeMethods.removeIf(method ->
                method.equals("loadDynamicLibrary") ||
                method.equals("loadWebAssemblyModule") ||
                method.equals("LDSO"));
        linkTarget.cppFlags.add("-std=c++17");
        linkTarget.headerDirs.add("-I" + sourceDir);
        linkTarget.headerDirs.add("-include" + op.getCustomSourceDir() + "LuaCustom.h");
        linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/emscripten/lua_.a");
        multiTarget.add(linkTarget);

        return multiTarget;
    }

    private static BuildMultiTarget getAndroidTarget(BuildToolOptions op) {
        BuildMultiTarget multiTarget = new BuildMultiTarget();
        String sourceDir = op.getSourceDir();
        String libBuildCPPPath = op.getModuleBuildCPPPath();

        AndroidTarget.ApiLevel apiLevel = AndroidTarget.ApiLevel.Android_10_29;
        ArrayList<AndroidTarget.Target> targets = new ArrayList<>();

        targets.add(AndroidTarget.Target.x86);
        targets.add(AndroidTarget.Target.x86_64);
        targets.add(AndroidTarget.Target.armeabi_v7a);
        targets.add(AndroidTarget.Target.arm64_v8a);

        for(int i = 0; i < targets.size(); i++) {
            AndroidTarget.Target target = targets.get(i);

            // Make a static library
            AndroidTarget androidTarget = new AndroidTarget(SourceLanguage.C, target, apiLevel);
            androidTarget.isStatic = true;
            androidTarget.headerDirs.add("-I" + sourceDir);
            androidTarget.cppInclude.add(sourceDir + "/*.c");
            androidTarget.cppExclude.add(sourceDir + "/lua.c");
            androidTarget.cppExclude.add(sourceDir + "/ltests.c");
            androidTarget.cppExclude.add(sourceDir + "/onelua.c");
            multiTarget.add(androidTarget);

            // Compile glue code and link
            AndroidTarget linkTarget = new AndroidTarget(target, apiLevel);
            linkTarget.cppFlags.add("-std=c++17");
            linkTarget.headerDirs.add("-I" + sourceDir);
            linkTarget.headerDirs.add("-I" + op.getCustomSourceDir());
            linkTarget.setupJNIGlueCode(libBuildCPPPath);
            linkTarget.linkerFlags.add(libBuildCPPPath + "/libs/android/" + target.getFolder() +"/lib" + op.libName + ".a");
            linkTarget.linkerFlags.add("-Wl,-z,max-page-size=16384");
            multiTarget.add(linkTarget);
        }
        return multiTarget;
    }
}
