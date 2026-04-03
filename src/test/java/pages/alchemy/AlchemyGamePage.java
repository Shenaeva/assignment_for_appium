package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyGamePage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    /**
     * Кликабельные элементы зоны подсказок.
     * По твоим данным клик по любому из них открывает слайдер "Ваши подсказки".
     */
    public final SelenideElement hintsAreaPrimary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(4)"));

    public final SelenideElement hintsAreaSecondary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(5)"));

    public final SelenideElement hintsAreaTertiary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(6)"));

    /**
     * Старые локаторы на text(\"2\") оставлены только как справка.
     * Не использовать для проверок, потому что число 2 встречается и в других местах экрана.
     */
    // public final SelenideElement oldHintsCounterPrimary =
    //         $(androidUIAutomator("new UiSelector().text(\"2\").instance(0)"));
    //
    // public final SelenideElement oldHintsCounterSecondary =
    //         $(androidUIAutomator("new UiSelector().text(\"2\").instance(1)"));

    public void openHintsSlider() {
        if (hintsAreaPrimary.isDisplayed()) {
            hintsAreaPrimary.shouldBe(Condition.visible, TIMEOUT).click();
            return;
        }

        if (hintsAreaSecondary.isDisplayed()) {
            hintsAreaSecondary.shouldBe(Condition.visible, TIMEOUT).click();
            return;
        }

        hintsAreaTertiary.shouldBe(Condition.visible, TIMEOUT).click();
    }
}