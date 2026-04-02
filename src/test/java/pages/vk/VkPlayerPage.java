package pages.vk;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class VkPlayerPage {

    private final SelenideElement playButton =
            $(By.id("com.vk.vkvideo:id/video_play_button"));

    private final SelenideElement currentProgress =
            $(By.id("com.vk.vkvideo:id/current_progress"));

    private final SelenideElement seekBar =
            $(By.id("com.vk.vkvideo:id/seek_bar"));

    private final SelenideElement skipBackButton =
            $(By.id("com.vk.vkvideo:id/button_skip_back"));

    private final SelenideElement skipForwardButton =
            $(By.id("com.vk.vkvideo:id/button_skip_forward"));

    private final SelenideElement fullscreenButton =
            $(By.id("com.vk.vkvideo:id/fullscreen"));

    // системное окно шеринга
    private final SelenideElement shareSheetTitle =
            $(By.xpath("//*[contains(@text,'Поделиться')]"));

    public void clickPlayIfVisible() {
        if (playButton.exists() && playButton.isDisplayed()) {
            playButton.click();
        }
    }

    public String getCurrentProgressText() {
        boolean controlsVisible = ensureControlsVisible();
        if (!controlsVisible) {
            throw new AssertionError("Не появились элементы управления видео");
        }

        boolean timerVisible = WaitUtils.waitUntilVisible(getWebDriver(), currentProgress, Duration.ofSeconds(5));
        if (!timerVisible) {
            throw new AssertionError("Не появился таймер воспроизведения");
        }

        return currentProgress.getText();
    }

    public PlaybackState detectPlaybackState() {
        String before = getCurrentProgressText();
        int beforeSeconds = extractCurrentSeconds(before);

        boolean progressed = WaitUtils.waitUntil(getWebDriver(), Duration.ofSeconds(8), d -> {
            String after = getCurrentProgressText();
            int afterSeconds = extractCurrentSeconds(after);
            return afterSeconds > beforeSeconds;
        });

        return progressed ? PlaybackState.PLAYING : PlaybackState.NOT_PLAYING;
    }

    private boolean ensureControlsVisible() {
        WebDriver driver = getWebDriver();

        // 1. Если элементы уже видны
        if (areControlsVisible(driver)) {
            return true;
        }

        // 2. Один tap в свободную верхнюю зону видео
        if (driver instanceof AndroidDriver androidDriver) {
            WaitUtils.tapByCoordinates(
                    androidDriver,
                    androidDriver.manage().window().getSize().getWidth() / 2,
                    (int) (androidDriver.manage().window().getSize().getHeight() * 0.18)
            );
        }

        // 3. Если вылез share sheet — закрыть
        if (shareSheetTitle.exists() && shareSheetTitle.isDisplayed() && driver instanceof AndroidDriver androidDriver) {
            androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        }

        // 4. Ожидание controls повторно
        return WaitUtils.waitUntil(driver, Duration.ofSeconds(5), d -> areControlsVisible(d));
    }

    private boolean areControlsVisible(WebDriver driver) {
        return (currentProgress.exists() && currentProgress.isDisplayed())
                || (seekBar.exists() && seekBar.isDisplayed())
                || (skipBackButton.exists() && skipBackButton.isDisplayed())
                || (skipForwardButton.exists() && skipForwardButton.isDisplayed())
                || (fullscreenButton.exists() && fullscreenButton.isDisplayed())
                || (playButton.exists() && playButton.isDisplayed());
    }

    private int extractCurrentSeconds(String progressText) {
        if (progressText == null || progressText.isBlank()) {
            return -1;
        }

        String[] parts = progressText.split("/");
        if (parts.length == 0) {
            return -1;
        }

        String currentPart = parts[0].trim();

        Pattern hmsPattern = Pattern.compile("^(\\d+):(\\d{1,2}):(\\d{2})$");
        Matcher hmsMatcher = hmsPattern.matcher(currentPart);
        if (hmsMatcher.matches()) {
            int hours = Integer.parseInt(hmsMatcher.group(1));
            int minutes = Integer.parseInt(hmsMatcher.group(2));
            int seconds = Integer.parseInt(hmsMatcher.group(3));
            return hours * 3600 + minutes * 60 + seconds;
        }

        Pattern msPattern = Pattern.compile("^(\\d{1,2}):(\\d{2})$");
        Matcher msMatcher = msPattern.matcher(currentPart);
        if (msMatcher.matches()) {
            int minutes = Integer.parseInt(msMatcher.group(1));
            int seconds = Integer.parseInt(msMatcher.group(2));
            return minutes * 60 + seconds;
        }

        return -1;
    }

    public enum PlaybackState {
        PLAYING,
        NOT_PLAYING
    }
}