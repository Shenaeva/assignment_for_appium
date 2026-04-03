package config;

public enum MobileApp {
    VK_VIDEO("com.vk.vkvideo", "com.vk.video.screens.main.MainActivity"),
    ALCHEMY("com.ilyin.alchemy", "com.ilyin.app_google_core.GoogleAppActivity");

    private final String appPackage;
    private final String appActivity;

    MobileApp(String appPackage, String appActivity) {
        this.appPackage = appPackage;
        this.appActivity = appActivity;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }
}