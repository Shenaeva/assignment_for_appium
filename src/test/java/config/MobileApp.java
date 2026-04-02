package config;

public enum MobileApp {
    VK_VIDEO("com.vk.vkvideo"),
    ALCHEMY("com.ilyin.alchemy");

    private final String appPackage;

    MobileApp(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppPackage() {
        return appPackage;
    }
}