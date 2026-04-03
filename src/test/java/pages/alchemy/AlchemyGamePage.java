package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyGamePage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final SelenideElement hintsAreaPrimary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(4)"));

    private final SelenideElement hintsAreaSecondary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(5)"));

    private final SelenideElement hintsAreaTertiary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(6)"));

    public void openHintsSlider() {
        getHintsContainer()
                .shouldBe(Condition.visible, TIMEOUT)
                .click();
    }

    public int getHintsCount() {
        SelenideElement container = getHintsContainer().shouldBe(Condition.visible, TIMEOUT);

        String ownText = safeTrim(container.getText());
        if (isInteger(ownText)) {
            return Integer.parseInt(ownText);
        }

        List<WebElement> textViews = container.toWebElement()
                .findElements(By.className("android.widget.TextView"));

        for (WebElement textView : textViews) {
            String text = safeTrim(textView.getText());
            if (isInteger(text)) {
                return Integer.parseInt(text);
            }
        }

        throw new IllegalStateException("Не удалось извлечь число подсказок на игровом экране");
    }

    public void waitUntilReady() {
        long endTime = System.currentTimeMillis() + Duration.ofSeconds(20).toMillis();

        while (System.currentTimeMillis() < endTime) {
            if (isDisplayed(hintsAreaPrimary) || isDisplayed(hintsAreaSecondary) || isDisplayed(hintsAreaTertiary)) {
                return;
            }
        }

        throw new IllegalStateException("Не дождались возврата на игровой экран после рекламы");
    }

    private boolean isDisplayed(SelenideElement element) {
        try {
            return element.exists() && element.isDisplayed();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private SelenideElement getHintsContainer() {
        if (hintsAreaPrimary.exists() && hintsAreaPrimary.isDisplayed()) {
            return hintsAreaPrimary;
        }

        if (hintsAreaSecondary.exists() && hintsAreaSecondary.isDisplayed()) {
            return hintsAreaSecondary;
        }

        return hintsAreaTertiary;
    }

    private boolean isInteger(String value) {
        return value != null && value.matches("\\d+");
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}