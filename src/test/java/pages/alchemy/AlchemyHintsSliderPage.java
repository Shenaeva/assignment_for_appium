package pages.alchemy;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.androidUIAutomator;

public class AlchemyHintsSliderPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");

    /**
     * Заголовок слайдера.
     * Главный признак, что экран "Ваши подсказки" открыт.
     */
    public final SelenideElement title =
            $(androidUIAutomator("new UiSelector().text(\"Ваши подсказки\")"));

    /**
     * Контейнер счетчика подсказок в шапке слайдера.
     * Используем его вместо локаторов, завязанных на конкретные числа "2" или "4".
     */
    public final SelenideElement hintsCounterContainer =
            $(androidUIAutomator("new UiSelector().className(\"android.view.View\").instance(9)"));

    /**
     * Заголовок блока получения подсказок.
     */
    public final SelenideElement getHintsTitle =
            $(androidUIAutomator("new UiSelector().text(\"Получить подсказки\")"));

    /**
     * Блок rewarded ad.
     */
    public final SelenideElement rewardedHintsText =
            $(androidUIAutomator("new UiSelector().text(\"2 подсказок\").instance(0)"));

    public final SelenideElement rewardedHintsDescription =
            $(androidUIAutomator("new UiSelector().text(\"За просмотр рекламы\")"));

    public final SelenideElement rewardedWatchText =
            $(androidUIAutomator("new UiSelector().text(\"Смотреть\")"));

    public final SelenideElement rewardedWatchButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(0)"));

    /**
     * Блок подсказок по таймеру.
     * В текущем тесте не используем, но оставляем на будущее.
     */
    public final SelenideElement timedHintsText =
            $(androidUIAutomator("new UiSelector().text(\"2 подсказок\").instance(1)"));

    public final SelenideElement timedHintsDescription =
            $(androidUIAutomator("new UiSelector().text(\"Каждые несколько минут\")"));

    public final SelenideElement timedCollectButton =
            $(androidUIAutomator("new UiSelector().className(\"android.widget.Button\").instance(1)"));

    public final SelenideElement timedCollectText =
            $(androidUIAutomator("new UiSelector().text(\"Забрать\")"));

    /**
     * Запасные хрупкие локаторы. Не использовать в основной логике.
     */
    // public final SelenideElement hintsCounterTwo =
    //         $(androidUIAutomator("new UiSelector().text(\"2\")"));
    //
    // public final SelenideElement hintsCounterFour =
    //         $(androidUIAutomator("new UiSelector().text(\"4\")"));

    public void shouldBeOpened() {
        title.shouldBe(Condition.visible, TIMEOUT);
    }

    public void clickWatchAdForHints() {
        rewardedWatchButton.shouldBe(Condition.visible, TIMEOUT).click();
    }

    public String getHintsCountText() {
        String rawText = hintsCounterContainer
                .shouldBe(Condition.visible, TIMEOUT)
                .getText();

        Matcher matcher = DIGITS_PATTERN.matcher(rawText);
        if (matcher.find()) {
            return matcher.group();
        }

        throw new IllegalStateException("Не удалось извлечь число подсказок из текста: " + rawText);
    }

    public int getHintsCount() {
        return Integer.parseInt(getHintsCountText());
    }

    /**
     * Временный техметод для отладки.
     * Можно вызвать в тесте, чтобы посмотреть, что реально возвращает контейнер.
     */
    public void printHintsCounterRawText() {
        String rawText = hintsCounterContainer
                .shouldBe(Condition.visible, TIMEOUT)
                .getText();
        System.out.println("HINTS RAW TEXT = [" + rawText + "]");
    }
}