package utils;

import java.io.IOException;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static void disableInternet() {
        exec("adb shell svc wifi disable");
        exec("adb shell svc data disable");
    }

    public static void enableInternet() {
        exec("adb shell svc wifi enable");
        exec("adb shell svc data enable");
    }

    private static void exec(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Команда завершилась с ошибкой: " + command);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось выполнить команду: " + command, e);
        }
    }
}