package core;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import config.MobileApp;
import config.MobileDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public abstract class TestBase {

    protected abstract MobileApp getApp();

    @BeforeEach
    void setUp() {
        MobileDriverProvider.setCurrentApp(getApp());

        Configuration.browser = MobileDriverProvider.class.getName();
        Configuration.timeout = 15000;
        Configuration.browserSize = null;
        Configuration.screenshots = true;
        Configuration.savePageSource = false;

        System.out.println("Запуск приложения: " + getApp().getAppPackage());

        Selenide.open();
    }

    @AfterEach
    void tearDown() {
        try {
            if (hasWebDriverStarted()) {
                WebDriver driver = getWebDriver();
                if (driver instanceof AndroidDriver androidDriver) {
                    androidDriver.terminateApp(getApp().getAppPackage());
                }
            }
        } catch (Exception e) {
            System.out.println("Не удалось завершить приложение: " + e.getMessage());
        } finally {
            MobileDriverProvider.clearCurrentApp();
            Selenide.closeWebDriver();
        }
    }

    protected WebDriver getDriver() {
        return getWebDriver();
    }

    protected void saveScreenshot(String fileName) {
        try {
            byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            Path dir = Paths.get("artifacts", "screenshots");
            Files.createDirectories(dir);
            Files.write(dir.resolve(fileName + ".png"), screenshot);
        } catch (Exception e) {
            System.out.println("Не удалось сохранить скриншот: " + e.getMessage());
        }
    }
}