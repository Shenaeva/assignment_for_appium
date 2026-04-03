package tests;

import config.MobileApp;
import core.TestBase;
import org.junit.jupiter.api.Test;

public class EnvironmentSmokeTest extends TestBase {

    @Override
    protected MobileApp getApp() {
        return MobileApp.VK_VIDEO;
    }

    @Test
    void appShouldStart() {
        System.out.println("Приложение успешно стартовало");
    }
}