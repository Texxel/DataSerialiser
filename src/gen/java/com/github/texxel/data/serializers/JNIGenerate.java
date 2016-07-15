package com.github.texxel.data.serializers;

import com.badlogic.gdx.jnigen.*;

public class JNIGenerate {

    public static void remakeSources() throws Exception {
        NativeCodeGenerator jnigen = new NativeCodeGenerator();
        jnigen.generate("src/main/java", "build/classes/main/", "jni");

        BuildTarget linux64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true );

        new AntScriptGenerator().generate( new BuildConfig("data"), linux64 );
        BuildExecutor.executeAnt("jni/build-linux64.xml", "-v -Dhas-compiler=true clean postcompile");
        BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");

        new JniGenSharedLibraryLoader("libs/data-natives.jar").load("data");

        // Sanity check to make sure stuff is working
        Object o = ObjectCreator.allocate(JNIGenerate.class);
        ObjectCreator.callEmptyConstructor(o);
    }

    public static void main( String[] string ) throws Exception {
        remakeSources();
    }

}
