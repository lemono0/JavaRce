package org.command.exec.jni;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 * 手动指定lib
 * @Ref: https://github.com/javaweb-sec/javaweb-sec
 * ProcessImpl & UnixProcess by unsafe + Native
 */
public class JniCmdDemo {

    /**
     * load命令执行类名
     */
    private static final String COMMAND_CLASS_NAME = "org.command.exec.jni.Calc";

    /**
     * com.whoopsunix.vul.exec.jni.Calc 类的字节码
     * 只有一个public static native String run(String cmd);的方法
     */
    private static final byte[] COMMAND_CLASS_BYTES = new byte[]{
            -54, -2, -70, -66, 0, 0, 0, 52, 0, 20, 10, 0, 3, 0, 17, 7, 0, 18, 7, 0, 19, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 34, 76, 99, 111, 109, 47, 119, 104, 111, 111, 112, 115, 117, 110, 105, 120, 47, 118, 117, 108, 47, 101, 120, 101, 99, 47, 106, 110, 105, 47, 67, 97, 108, 99, 59, 1, 0, 3, 114, 117, 110, 1, 0, 38, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 16, 77, 101, 116, 104, 111, 100, 80, 97, 114, 97, 109, 101, 116, 101, 114, 115, 1, 0, 3, 99, 109, 100, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 9, 67, 97, 108, 99, 46, 106, 97, 118, 97, 12, 0, 4, 0, 5, 1, 0, 32, 99, 111, 109, 47, 119, 104, 111, 111, 112, 115, 117, 110, 105, 120, 47, 118, 117, 108, 47, 101, 120, 101, 99, 47, 106, 110, 105, 47, 67, 97, 108, 99, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 0, 33, 0, 2, 0, 3, 0, 0, 0, 0, 0, 2, 0, 1, 0, 4, 0, 5, 0, 1, 0, 6, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 7, 0, 0, 0, 6, 0, 1, 0, 0, 0, 6, 0, 8, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 9, 0, 10, 0, 0, 1, 9, 0, 11, 0, 12, 0, 1, 0, 13, 0, 0, 0, 5, 1, 0, 14, 0, 0, 0, 1, 0, 15, 0, 0, 0, 2, 0, 16
    };

    public static void main(String[] args) {
        String cmd = "open -a Calculator.app";// 定于需要执行的cmd

        try {
            ClassLoader loader = new ClassLoader(Calc.class.getClassLoader()) {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    try {
                        return super.findClass(name);
                    } catch (ClassNotFoundException e) {
                        return defineClass(COMMAND_CLASS_NAME, COMMAND_CLASS_BYTES, 0, COMMAND_CLASS_BYTES.length);
                    }
                }
            };

            /**
             * 替换lib路径
             */
            // 获取项目目录
            File libPath = new File(System.getProperty("user.dir") + "/Command/src/main/java/org/command/exec/jni/com/command/exec/jni/libcmd.jnilib");

            /**
             * load命令执行类
             */
            Class<?> commandClass = loader.loadClass(COMMAND_CLASS_NAME);

            // 可以用System.load也加载lib也可以用反射ClassLoader加载,如果loadLibrary0被拦截了可以换java.lang.ClassLoader$NativeLibrary类的load方法
//            System.load(libPath.getAbsolutePath());
            Method loadLibrary0Method = ClassLoader.class.getDeclaredMethod("loadLibrary0", Class.class, File.class);
            loadLibrary0Method.setAccessible(true);
            loadLibrary0Method.invoke(loader, commandClass, libPath);

            String content = (String) commandClass.getMethod("run", String.class).invoke(null, cmd);
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
