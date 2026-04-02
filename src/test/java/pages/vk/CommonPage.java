package pages.vk;

import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;

import java.time.Duration;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.Selenide.$;

public class CommonPage {

    private final SelenideElement skipAuthButton =
            $(By.id("com.vk.vkvideo:id/fast_login_tertiary_btn"));

    private final SelenideElement enteringPhoneOrEmail =
            $(By.id("com.vk.vkvideo:id/enter_email_or_phone"));

    private final SelenideElement loginButton =
            $(By.id("com.vk.vkvideo:id/login_btn"));

    private final SelenideElement createAccount =
            $(By.id("com.vk.vkvideo:id/create_account_btn"));

    public void skipAuthIfPresent() {
        WebDriver driver = getWebDriver();

        boolean appeared = WaitUtils.waitUntilVisible(driver, skipAuthButton, Duration.ofSeconds(5));
        if (!appeared) {
            return;
        }

        skipAuthButton.click();

        boolean disappeared = WaitUtils.waitUntilInvisible(driver, skipAuthButton, Duration.ofSeconds(3));
        if (!disappeared && driver instanceof AndroidDriver androidDriver) {
            WaitUtils.tapCenter(androidDriver, skipAuthButton);
            WaitUtils.waitUntilInvisible(driver, skipAuthButton, Duration.ofSeconds(5));
        }
    }
}