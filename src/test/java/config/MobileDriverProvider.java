package config;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class MobileDriverProvider implements WebDriverProvider {

    private static final ThreadLocal<MobileApp> CURRENT_APP = new ThreadLocal<>();

    public static void setCurrentApp(MobileApp app) {
        CURRENT_APP.set(app);
    }

    public static void clearCurrentApp() {
        CURRENT_APP.remove();
    }

    @Override
    public @NonNull WebDriver createDriver(@NonNull Capabilities capabilities) {
        Properties props = loadProperties();
        MobileApp app = CURRENT_APP.get();

        if (app == null) {
            throw new IllegalStateException("Приложение для запуска не задано. Проверь getApp() в тесте.");
        }

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(props.getProperty("platformName"))
                .setPlatformVersion(props.getProperty("platformVersion"))
                .setDeviceName(props.getProperty("deviceName"))
                .setAutomationName(props.getProperty("automationName"))
                .setAppPackage(app.getAppPackage())
                .setAppActivity(app.getAppActivity())
                .setNewCommandTimeout(
                        Duration.ofSeconds(Long.parseLong(props.getProperty("newCommandTimeout", "120")))
                );

        options.setCapability("noReset",
                Boolean.parseBoolean(props.getProperty("noReset", "true")));
        options.setCapability("autoGrantPermissions",
                Boolean.parseBoolean(props.getProperty("autoGrantPermissions", "true")));

        try {
            return new AndroidDriver(
                    new URL(props.getProperty("appiumUrl", "http://127.0.0.1:4723")),
                    options
            );
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать AndroidDriver", e);
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream inputStream = MobileDriverProvider.class
                .getClassLoader()
                .getResourceAsStream("appium.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("Файл appium.properties не найден в resources");
            }

            props.load(inputStream);
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить appium.properties", e);
        }
    }
}