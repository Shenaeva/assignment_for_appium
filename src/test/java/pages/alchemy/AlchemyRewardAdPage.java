package pages.alchemy;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyRewardAdPage {

    private static final Duration AD_STAGE_TIMEOUT = Duration.ofSeconds(120);
    private static final Duration CLOSE_BUTTON_TIMEOUT = Duration.ofSeconds(25);
    private static final Duration NEXT_STAGE_CHECK_TIMEOUT = Duration.ofSeconds(5);

    private final SelenideElement hintsSliderTitle =
            $(androidUIAutomator("new UiSelector().text(\"Ваши подсказки\")"));

    private final SelenideElement rewardReceivedTitle =
            $(androidUIAutomator("new UiSelector().text(\"НАГРАДА ПОЛУЧЕНА\")"));

    // Треугольник: жмем только если он реально виден
    private final SelenideElement adTriangleButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(2)"));

    // Крестик: его только ждем
    private final SelenideElement closeRewardButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(1)"));

    public boolean didAdFlowStartWithin(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);

        try {
            return wait.until(driver ->
                    !isVisible(hintsSliderTitle)
                            || isVisible(rewardReceivedTitle)
                            || isVisible(adTriangleButton)
                            || isVisible(closeRewardButton));
        } catch (Throwable ignored) {
            return false;
        }
    }

    public void finishRewardedAdFlow() {
        handleSingleAdStage();

        if (hasAnotherAdStageWithin(NEXT_STAGE_CHECK_TIMEOUT)) {
            handleSingleAdStage();
        }
    }

    private void handleSingleAdStage() {
        waitForFinalAdStage();

        boolean triangleClicked = false;
        long endTime = System.currentTimeMillis() + CLOSE_BUTTON_TIMEOUT.toMillis();

        while (System.currentTimeMillis() < endTime) {
            if (isVisible(closeRewardButton)) {
                closeRewardButton.click();
                return;
            }

            if (!triangleClicked && isVisible(adTriangleButton)) {
                adTriangleButton.click();
                triangleClicked = true;
            }
        }

        throw new IllegalStateException("Не появился крестик закрытия рекламы");
    }

    private void waitForFinalAdStage() {
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), AD_STAGE_TIMEOUT);

        wait.until(driver ->
                isVisible(rewardReceivedTitle)
                        || isVisible(adTriangleButton)
                        || isVisible(closeRewardButton));
    }

    private boolean hasAnotherAdStageWithin(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), timeout);

        try {
            return wait.until(driver ->
                    isVisible(adTriangleButton) || isVisible(closeRewardButton));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isVisible(SelenideElement element) {
        try {
            return element.exists() && element.isDisplayed();
        } catch (Throwable ignored) {
            return false;
        }
    }
}