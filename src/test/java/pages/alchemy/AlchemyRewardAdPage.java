package pages.alchemy;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyRewardAdPage {

    private static final Duration AD_FLOW_TIMEOUT = Duration.ofSeconds(120);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(400);
    private static final Duration AFTER_TRIANGLE_CLICK_PAUSE = Duration.ofMillis(800);
    private static final Duration STABILITY_CHECK_INTERVAL = Duration.ofMillis(250);

    private final SelenideElement hintsSliderTitle =
            $(androidUIAutomator("new UiSelector().text(\"Ваши подсказки\")"));

    private final SelenideElement rewardReceivedTitle =
            $(androidUIAutomator("new UiSelector().text(\"НАГРАДА ПОЛУЧЕНА\")"));

    // Оставляю старый локатор, но не использую для клика
    private final SelenideElement adTriangleButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(2)"));

    // Точный локатор треугольника
    private final SelenideElement exactAdTriangleButton = $x(
            "//android.widget.RelativeLayout[@content-desc=\"pageIndex: 1\"]" +
                    "/android.widget.FrameLayout/android.widget.FrameLayout" +
                    "/android.view.ViewGroup/android.view.ViewGroup" +
                    "/android.view.ViewGroup/android.view.ViewGroup[2]" +
                    "/android.view.ViewGroup[1]/android.view.ViewGroup" +
                    "/android.view.ViewGroup[2]/android.widget.ImageView"
    );

    // Оставляю старый локатор, но не использую как основной для клика
    private final SelenideElement closeRewardButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.ImageView\").instance(1)"));

    // Точный локатор финального крестика
    private final SelenideElement finalCloseRewardButton = $x(
            "//android.widget.RelativeLayout[@content-desc=\"pageIndex: 3\"]" +
                    "/android.widget.FrameLayout/android.widget.FrameLayout" +
                    "/android.view.ViewGroup/android.view.ViewGroup" +
                    "/android.view.ViewGroup[1]/android.widget.ImageView"
    );

    public boolean didAdFlowStartWithin(Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < deadline) {
            if (!isVisible(hintsSliderTitle)
                    || isVisible(rewardReceivedTitle)
                    || isVisible(exactAdTriangleButton)
                    || isVisible(finalCloseRewardButton)) {
                return true;
            }

            sleep(POLL_INTERVAL);
        }

        return false;
    }

    public void finishRewardedAdFlow() {
        long deadline = System.currentTimeMillis() + AD_FLOW_TIMEOUT.toMillis();

        while (System.currentTimeMillis() < deadline) {
            // 1. Приоритетно всегда ищем крестик.
            // Если он появился - сразу закрываем.
            if (isStableVisible(finalCloseRewardButton)) {
                finalCloseRewardButton.click();
                return;
            }

            // 2. Если крестика нет, но появился точный треугольник - жмем только его.
            if (isStableVisible(exactAdTriangleButton)) {
                exactAdTriangleButton.click();
                sleep(AFTER_TRIANGLE_CLICK_PAUSE);
                continue;
            }

            // 3. Если ни крестика, ни треугольника нет - ничего не жмем.
            sleep(POLL_INTERVAL);
        }

        throw new IllegalStateException(
                "Не удалось завершить rewarded ad flow: не дождались крестика или треугольника " +
                        "в пределах " + AD_FLOW_TIMEOUT.getSeconds() + " секунд"
        );
    }

    private boolean isStableVisible(SelenideElement element) {
        if (!isVisible(element)) {
            return false;
        }

        sleep(STABILITY_CHECK_INTERVAL);
        return isVisible(element);
    }

    private boolean isVisible(SelenideElement element) {
        try {
            return element.exists() && element.isDisplayed();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ожидание рекламного flow было прервано", e);
        }
    }
}