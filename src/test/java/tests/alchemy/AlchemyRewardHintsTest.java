package tests.alchemy;

import config.MobileApp;
import core.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.alchemy.AlchemyGamePage;
import pages.alchemy.AlchemyHintsSliderPage;
import pages.alchemy.AlchemyMainMenuPage;
import pages.alchemy.AlchemyRewardAdPage;

public class AlchemyRewardHintsTest extends TestBase {

    private final AlchemyMainMenuPage mainMenuPage = new AlchemyMainMenuPage();
    private final AlchemyGamePage gamePage = new AlchemyGamePage();
    private final AlchemyHintsSliderPage hintsSliderPage = new AlchemyHintsSliderPage();
    private final AlchemyRewardAdPage rewardAdPage = new AlchemyRewardAdPage();

    @Override
    protected MobileApp getApp() {
        return MobileApp.ALCHEMY;
    }

    @Test
    @DisplayName("Алхимия: получение подсказок за просмотр рекламы")
    void shouldIncreaseHintsAfterRewardedAd() {
        mainMenuPage.clickPlay();

        gamePage.openHintsSlider();
        hintsSliderPage.shouldBeOpened();

        int beforeCount = hintsSliderPage.getHintsCount();

        hintsSliderPage.clickWatchAdForHints();

        rewardAdPage.waitForAdOpened();
        rewardAdPage.waitForRewardReceivedScreen();
        rewardAdPage.closeRewardScreen();

        hintsSliderPage.shouldBeOpened();

        int afterCount = hintsSliderPage.getHintsCount();

        Assertions.assertTrue(
                afterCount > beforeCount,
                "Количество подсказок не увеличилось. Было: " + beforeCount + ", стало: " + afterCount
        );

        Assertions.assertEquals(
                4,
                afterCount,
                "После rewarded ad количество подсказок должно быть равно 4, но стало: " + afterCount
        );
    }
}