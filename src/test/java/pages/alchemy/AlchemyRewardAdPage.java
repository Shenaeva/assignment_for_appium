package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyRewardAdPage {

    private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration REWARD_TIMEOUT = Duration.ofSeconds(60);

    /**
     * Признаки того, что рекламный экран открылся.
     * Не все из них обязательно использовать в тесте,
     * часть оставлена как резерв на будущее.
     */
    public final SelenideElement adHeroImage =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(3)"));

    public final SelenideElement adTimerContainerPrimary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.ViewGroup\").instance(5)"));

    public final SelenideElement adTimerContainerSecondary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.ViewGroup\").instance(6)"));

    /**
     * Хрупкий локатор, добавлен на будущее.
     * Завязан на текущее значение текста в progress bar.
     */
    public final SelenideElement adProgressBar =
            $(androidUIAutomator("new UiSelector().text(\"38998.0\").instance(0)"));

    public final SelenideElement adViewPrimary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(0)"));

    public final SelenideElement adViewSecondary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(1)"));

    /**
     * Финальный экран после просмотра rewarded ad.
     */
    public final SelenideElement adLabel =
            $(androidUIAutomator("new UiSelector().text(\"РЕКЛАМА\")"));

    public final SelenideElement rewardReceivedTitle =
            $(androidUIAutomator("new UiSelector().text(\"НАГРАДА ПОЛУЧЕНА\")"));

    /**
     * Крестик закрытия финального экрана награды.
     */
    public final SelenideElement closeRewardButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(1)"));

    public void waitForAdOpened() {
        adHeroImage.shouldBe(Condition.visible, SHORT_TIMEOUT);
    }

    public void waitForRewardReceivedScreen() {
        rewardReceivedTitle.shouldBe(Condition.visible, REWARD_TIMEOUT);
    }

    public void closeRewardScreen() {
        closeRewardButton.shouldBe(Condition.visible, SHORT_TIMEOUT).click();
    }
}