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

        int gameBeforeCount = gamePage.getHintsCount();

        gamePage.openHintsSlider();
        hintsSliderPage.shouldBeOpened();

        int sliderBeforeCount = hintsSliderPage.getHintsCount();

        Assertions.assertEquals(
                gameBeforeCount,
                sliderBeforeCount,
                "Количество подсказок на игровом экране и в слайдере должно совпадать до просмотра рекламы"
        );

        hintsSliderPage.clickWatchAdUntilFlowStarts(rewardAdPage);
        rewardAdPage.finishRewardedAdFlow();

        gamePage.waitUntilReady();

        int gameAfterCount = gamePage.getHintsCount();

        gamePage.openHintsSlider();
        hintsSliderPage.shouldBeOpened();

        int sliderAfterCount = hintsSliderPage.getHintsCount();

        Assertions.assertEquals(
                gameBeforeCount + 2,
                gameAfterCount,
                "Количество подсказок на игровом экране должно увеличиться на 2. Было: "
                        + gameBeforeCount + ", стало: " + gameAfterCount
        );

        Assertions.assertEquals(
                sliderBeforeCount + 2,
                sliderAfterCount,
                "Количество подсказок в слайдере должно увеличиться на 2. Было: "
                        + sliderBeforeCount + ", стало: " + sliderAfterCount
        );

        Assertions.assertEquals(
                gameAfterCount,
                sliderAfterCount,
                "Количество подсказок на игровом экране и в слайдере должно совпадать после просмотра рекламы"
        );
    }
}