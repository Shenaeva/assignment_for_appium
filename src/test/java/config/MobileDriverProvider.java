package config;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class MobileDriverProvider implements WebDriverProvider {

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        Properties props = loadProperties();
        MobileApp app = AppResolver.resolve();

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(props.getProperty("platformName"))
                .setPlatformVersion(props.getProperty("platformVersion"))
                .setDeviceName(props.getProperty("deviceName"))
                .setAutomationName(props.getProperty("automationName"))
                .setAppPackage(app.getAppPackage())
                .setNewCommandTimeout(
                        Duration.ofSeconds(Long.parseLong(props.getProperty("newCommandTimeout", "120")))
                );

        options.setCapability("noReset",
                Boolean.parseBoolean(props.getProperty("noReset", "true")));
        options.setCapability("autoGrantPermissions",
                Boolean.parseBoolean(props.getProperty("autoGrantPermissions", "true")));

        try {
            return new AndroidDriver(
                    new URL(props.getProperty("appiumUrl")),
                    options
            );
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать AndroidDriver", e);
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (var is = getClass().getClassLoader().getResourceAsStream("appium.properties")) {
            if (is == null) {
                throw new RuntimeException("Файл appium.properties не найден в resources");
            }
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить appium.properties", e);
        }
    }
}