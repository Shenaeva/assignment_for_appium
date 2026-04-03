package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyHintsSliderPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Duration AD_FLOW_CHECK_TIMEOUT = Duration.ofSeconds(15);
    private static final int MAX_WATCH_CLICK_ATTEMPTS = 3;

    private final SelenideElement title =
            $(androidUIAutomator("new UiSelector().text(\"Ваши подсказки\")"));

    private final SelenideElement getHintsTitle =
            $(androidUIAutomator("new UiSelector().text(\"Получить подсказки\")"));

    private final SelenideElement hintsCounterContainer =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(9)"));

    private final SelenideElement rewardedWatchButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)"));

    public void shouldBeOpened() {
        title.shouldBe(Condition.visible, TIMEOUT);
        getHintsTitle.shouldBe(Condition.visible, TIMEOUT);
    }

    public void clickWatchAdUntilFlowStarts(AlchemyRewardAdPage rewardAdPage) {
        for (int attempt = 1; attempt <= MAX_WATCH_CLICK_ATTEMPTS; attempt++) {
            rewardedWatchButton.shouldBe(Condition.visible, TIMEOUT).click();

            if (rewardAdPage.didAdFlowStartWithin(AD_FLOW_CHECK_TIMEOUT)) {
                return;
            }
        }

        throw new IllegalStateException("Не удалось запустить rewarded ad после "
                + MAX_WATCH_CLICK_ATTEMPTS + " попыток");
    }

    public int getHintsCount() {
        hintsCounterContainer.shouldBe(Condition.visible, TIMEOUT);

        String ownText = safeTrim(hintsCounterContainer.getText());
        if (isInteger(ownText)) {
            return Integer.parseInt(ownText);
        }

        List<WebElement> textViews = hintsCounterContainer.toWebElement()
                .findElements(By.className("android.widget.TextView"));

        for (WebElement textView : textViews) {
            String text = safeTrim(textView.getText());
            if (isInteger(text)) {
                return Integer.parseInt(text);
            }
        }

        throw new IllegalStateException("Не удалось извлечь число подсказок из контейнера счетчика");
    }

    private boolean isInteger(String value) {
        return value != null && value.matches("\\d+");
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}