package tests.vk;

import core.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.vk.CommonPage;
import pages.vk.VkHomePage;
import pages.vk.VkPlayerPage;

public class VkVideoPlaybackTest extends TestBase {

    private final CommonPage commonPage = new CommonPage();
    private final VkHomePage vkHomePage = new VkHomePage();
    private final VkPlayerPage vkPlayerPage = new VkPlayerPage();

    @Test
    @DisplayName("VK Видео: видео воспроизводится")
    void videoShouldPlay() {
        commonPage.skipAuthIfPresent();
        vkHomePage.openFirstVideo();
        vkPlayerPage.clickPlayIfVisible();

        VkPlayerPage.PlaybackState playbackState = vkPlayerPage.detectPlaybackState();

        Assertions.assertEquals(
                VkPlayerPage.PlaybackState.PLAYING,
                playbackState,
                "Ожидали, что видео будет воспроизводиться"
        );
    }
}