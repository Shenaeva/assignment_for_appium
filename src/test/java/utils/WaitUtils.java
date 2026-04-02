package utils;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public final class WaitUtils {

    private WaitUtils() {
    }

    public static boolean waitUntil(WebDriver driver, Duration timeout, Function<WebDriver, Boolean> condition) {
        try {
            new WebDriverWait(driver, timeout).until(condition::apply);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean waitUntilVisible(WebDriver driver, SelenideElement element, Duration timeout) {
        return waitUntil(driver, timeout, d -> element.exists() && element.isDisplayed());
    }

    public static boolean waitUntilInvisible(WebDriver driver, SelenideElement element, Duration timeout) {
        return waitUntil(driver, timeout, d -> !element.exists() || !element.isDisplayed());
    }

    public static void tapCenter(AndroidDriver driver, SelenideElement element) {
        Rectangle rect = element.getRect();
        int centerX = rect.getX() + rect.getWidth() / 2;
        int centerY = rect.getY() + rect.getHeight() / 2;
        tapByCoordinates(driver, centerX, centerY);
    }

    public static void tapByCoordinates(AndroidDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);

        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(tap));
    }

    public static void tapViewportCenter(AndroidDriver driver) {
        var size = driver.manage().window().getSize();
        int centerX = size.getWidth() / 2;
        int centerY = size.getHeight() / 2;
        tapByCoordinates(driver, centerX, centerY);
    }
}