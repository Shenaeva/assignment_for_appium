package pages.vk;

import com.codeborne.selenide.ElementsCollection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class VkHomePage {

    private final By videoCardLocator = By.id("com.vk.vkvideo:id/content");

    public void openFirstVideo() {
        WebDriver driver = getWebDriver();

        boolean cardsAppeared = WaitUtils.waitUntil(driver, Duration.ofSeconds(20), d -> {
            ElementsCollection cards = $$(videoCardLocator);
            return !cards.isEmpty();
        });

        if (!cardsAppeared) {
            throw new AssertionError("Не появились карточки видео на главном экране");
        }

        $$(videoCardLocator).first().click();
    }
}