package tests.vk;

import config.MobileApp;
import core.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.vk.CommonPage;
import pages.vk.VkHomePage;
import pages.vk.VkPlayerPage;
import utils.NetworkUtils;

public class VkVideoPlaybackNegativeTest extends TestBase {

    private final CommonPage commonPage = new CommonPage();
    private final VkHomePage vkHomePage = new VkHomePage();
    private final VkPlayerPage vkPlayerPage = new VkPlayerPage();

    @Override
    protected MobileApp getApp() {
        return MobileApp.VK_VIDEO;
    }

    @Test
    @DisplayName("VK Видео: видео не воспроизводится без сети")
    void videoShouldNotPlayWithoutInternet() {
        try {
            commonPage.skipAuthIfPresent();
            vkHomePage.openFirstVideo();

            NetworkUtils.disableInternet();

            vkPlayerPage.clickPlayIfVisible();

            VkPlayerPage.PlaybackState playbackState =
                    vkPlayerPage.detectPlaybackStateForNegativeScenario();

            boolean noInternetVisible = vkPlayerPage.isNoInternetErrorVisible();
            boolean loaderVisible = vkPlayerPage.isLoaderVisible();
            boolean notPlaying = playbackState == VkPlayerPage.PlaybackState.NOT_PLAYING;

            Assertions.assertTrue(
                    noInternetVisible || loaderVisible || notPlaying,
                    "Ожидали негативный сценарий без сети: плашку ошибки, лоадер или состояние NOT_PLAYING"
            );
        } finally {
            NetworkUtils.enableInternet();
        }
    }
}