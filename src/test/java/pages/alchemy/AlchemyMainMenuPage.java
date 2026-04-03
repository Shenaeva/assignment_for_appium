package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.accessibilityId;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyMainMenuPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final SelenideElement settingsButton =
            $(accessibilityId("Настройки"));

    private final SelenideElement howToPlayButton =
            $(accessibilityId("Как играть?"));

    private final SelenideElement achievementsButton =
            $(androidUIAutomator("new UiSelector().text(\"Достижения\")"));

    private final SelenideElement playButton =
            $(androidUIAutomator("new UiSelector().text(\"Играть\")"));

    private final SelenideElement suggestElementButton =
            $(androidUIAutomator("new UiSelector().text(\"Предложить элемент\")"));

    private final SelenideElement otherGamesButton =
            $(androidUIAutomator("new UiSelector().text(\"Другие игры\")"));

    public void clickPlay() {
        playButton.shouldBe(Condition.visible, TIMEOUT).click();
    }
}