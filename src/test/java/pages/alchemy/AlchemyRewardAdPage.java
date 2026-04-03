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
    private static final Duration LOG_INTERVAL = Duration.ofSeconds(2);

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

    // Те же зоны подсказок, по которым у тебя определяется готовность игрового экрана
    private final SelenideElement hintsAreaPrimary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(4)"));

    private final SelenideElement hintsAreaSecondary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(5)"));

    private final SelenideElement hintsAreaTertiary =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(6)"));

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
        long nextLogAt = System.currentTimeMillis();

        while (System.currentTimeMillis() < deadline) {
            if (System.currentTimeMillis() >= nextLogAt) {
                logCurrentState();
                nextLogAt = System.currentTimeMillis() + LOG_INTERVAL.toMillis();
            }

            // 1. Если уже вернулся игровой экран — выходим без дополнительных действий
            if (isGameScreenBack()) {
                System.out.println("[AlchemyRewardAdPage] Game screen is back -> finish flow without extra click");
                return;
            }

            // 2. Если появился точный крестик — закрываем и выходим
            if (isStableVisible(finalCloseRewardButton)) {
                System.out.println("[AlchemyRewardAdPage] Found exact final close button -> click");
                finalCloseRewardButton.click();
                return;
            }

            // 3. Если появился точный треугольник — жмем только его
            if (isStableVisible(exactAdTriangleButton)) {
                System.out.println("[AlchemyRewardAdPage] Found exact triangle button -> click");
                exactAdTriangleButton.click();
                sleep(AFTER_TRIANGLE_CLICK_PAUSE);
                continue;
            }

            // 4. Ничего точного не нашли — просто ждем
            sleep(POLL_INTERVAL);
        }

        throw new IllegalStateException(
                "Не удалось завершить rewarded ad flow: не дождались точного крестика/треугольника " +
                        "или возврата на игровой экран в пределах " + AD_FLOW_TIMEOUT.getSeconds() + " секунд"
        );
    }

    private boolean isGameScreenBack() {
        return isVisible(hintsAreaPrimary) || isVisible(hintsAreaSecondary) || isVisible(hintsAreaTertiary);
    }

    private void logCurrentState() {
        boolean exactTriangleVisible = isVisible(exactAdTriangleButton);
        boolean fallbackTriangleVisible = isVisible(adTriangleButton);
        boolean exactCloseVisible = isVisible(finalCloseRewardButton);
        boolean fallbackCloseVisible = isVisible(closeRewardButton);
        boolean rewardTitleVisible = isVisible(rewardReceivedTitle);
        boolean gameScreenBack = isGameScreenBack();

        System.out.println(
                "[AlchemyRewardAdPage] state: " +
                        "rewardTitle=" + rewardTitleVisible +
                        ", exactTriangle=" + exactTriangleVisible +
                        ", fallbackTriangle=" + fallbackTriangleVisible +
                        ", exactClose=" + exactCloseVisible +
                        ", fallbackClose=" + fallbackCloseVisible +
                        ", gameScreenBack=" + gameScreenBack
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