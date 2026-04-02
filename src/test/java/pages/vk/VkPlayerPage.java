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

    private final SelenideElement shareSheetTitle =
            $(By.xpath("//*[contains(@text,'Поделиться')]"));

    public void clickPlayIfVisible() {
        if (playButton.exists() && playButton.isDisplayed()) {
            playButton.click();
        }
    }

    private final SelenideElement noInternetLayout =
            $(By.id("com.vk.vkvideo:id/center_layout"));

    private final SelenideElement noInternetTitle =
            $(By.xpath("//android.widget.TextView[@resource-id='com.vk.vkvideo:id/title' and @text='Нет интернета — проверьте подключение']"));

    private final SelenideElement loader =
            $(By.id("com.vk.vkvideo:id/progress_view"));

    public String getCurrentProgressText() {
        WebDriver driver = getWebDriver();

        for (int attempt = 0; attempt < 3; attempt++) {
            boolean controlsVisible = ensureControlsVisible();
            if (!controlsVisible) {
                continue;
            }

            boolean timerVisible = WaitUtils.waitUntilVisible(driver, currentProgress, Duration.ofSeconds(2));
            if (!timerVisible) {
                continue;
            }

            try {
                String text = currentProgress.getText();
                if (!text.isBlank()) {
                    return text;
                }
            } catch (Exception ignored) {
                // оверлей успел скрыться между wait и getText
            }
        }

        throw new AssertionError("Не удалось стабильно получить текст таймера воспроизведения");
    }

    public PlaybackState detectPlaybackState() {
        String before = getCurrentProgressText();
        int beforeSeconds = extractCurrentSeconds(before);

        boolean progressed = WaitUtils.waitUntil(getWebDriver(), Duration.ofSeconds(8), d -> {
            try {
                String after = getCurrentProgressText();
                int afterSeconds = extractCurrentSeconds(after);
                return afterSeconds > beforeSeconds;
            } catch (Exception e) {
                return false;
            }
        });

        return progressed ? PlaybackState.PLAYING : PlaybackState.NOT_PLAYING;
    }

    private boolean ensureControlsVisible() {
        WebDriver driver = getWebDriver();

        if (areControlsVisible()) {
            return true;
        }

        if (driver instanceof AndroidDriver androidDriver) {
            WaitUtils.tapByCoordinates(
                    androidDriver,
                    androidDriver.manage().window().getSize().getWidth() / 2,
                    (int) (androidDriver.manage().window().getSize().getHeight() * 0.18)
            );
        }

        if (shareSheetTitle.exists() && shareSheetTitle.isDisplayed() && driver instanceof AndroidDriver androidDriver) {
            androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        }

        return WaitUtils.waitUntil(driver, Duration.ofSeconds(5), d -> areControlsVisible());
    }

    private boolean areControlsVisible() {
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

    public PlaybackState detectPlaybackStateForNegativeScenario() {
        WebDriver driver = getWebDriver();

        // 1. Явная плашка отсутствия интернета
        boolean noInternetAppeared = WaitUtils.waitUntil(driver, Duration.ofSeconds(5), d -> isNoInternetErrorVisible());
        if (noInternetAppeared) {
            return PlaybackState.NOT_PLAYING;
        }

        // 2. Controls не появились — тоже негативный исход
        boolean controlsVisible = ensureControlsVisible();
        if (!controlsVisible) {
            return PlaybackState.NOT_PLAYING;
        }

        // 3. Таймер не появился — тоже негативный исход
        boolean timerVisible = WaitUtils.waitUntilVisible(driver, currentProgress, Duration.ofSeconds(3));
        if (!timerVisible) {
            return PlaybackState.NOT_PLAYING;
        }

        // 4. Таймер появился, но время не идет
        String before = currentProgress.getText();
        int beforeSeconds = extractCurrentSeconds(before);

        boolean progressed = WaitUtils.waitUntil(driver, Duration.ofSeconds(6), d -> {
            if (!currentProgress.exists() || !currentProgress.isDisplayed()) {
                return false;
            }
            String after = currentProgress.getText();
            int afterSeconds = extractCurrentSeconds(after);
            return afterSeconds > beforeSeconds;
        });

        return progressed ? PlaybackState.PLAYING : PlaybackState.NOT_PLAYING;
    }

    public boolean isNoInternetErrorVisible() {
        return (noInternetLayout.exists() && noInternetLayout.isDisplayed())
                || (noInternetTitle.exists() && noInternetTitle.isDisplayed());
    }

    public boolean isLoaderVisible() {
        return loader.exists() && loader.isDisplayed();
    }



    public enum PlaybackState {
        PLAYING,
        NOT_PLAYING
    }
}