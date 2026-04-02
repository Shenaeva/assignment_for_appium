package utils;

import java.io.File;
import java.io.IOException;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static void disableInternet() {
        exec(adbPath(), "shell", "svc", "wifi", "disable");
        exec(adbPath(), "shell", "svc", "data", "disable");
    }

    public static void enableInternet() {
        exec(adbPath(), "shell", "svc", "wifi", "enable");
        exec(adbPath(), "shell", "svc", "data", "enable");
    }

    private static String adbPath() {
        String androidHome = System.getenv("ANDROID_HOME");
        if (androidHome != null && !androidHome.isBlank()) {
            File adb = new File(androidHome + "/platform-tools/adb");
            if (adb.exists()) {
                return adb.getAbsolutePath();
            }
        }

        String androidSdkRoot = System.getenv("ANDROID_SDK_ROOT");
        if (androidSdkRoot != null && !androidSdkRoot.isBlank()) {
            File adb = new File(androidSdkRoot + "/platform-tools/adb");
            if (adb.exists()) {
                return adb.getAbsolutePath();
            }
        }

        File defaultMacPath = new File(System.getProperty("user.home") + "/Library/Android/sdk/platform-tools/adb");
        if (defaultMacPath.exists()) {
            return defaultMacPath.getAbsolutePath();
        }

        throw new RuntimeException("Не удалось найти adb. Проверь ANDROID_HOME / ANDROID_SDK_ROOT или путь к Android SDK.");
    }

    private static void exec(String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Команда завершилась с ошибкой: " + String.join(" ", command));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось выполнить команду: " + String.join(" ", command), e);
        }
    }
}