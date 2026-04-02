package config;

public final class AppResolver {

    private AppResolver() {
    }

    public static MobileApp resolve() {
        String app = System.getProperty("app", "vk").toLowerCase();

        return switch (app) {
            case "vk" -> MobileApp.VK_VIDEO;
            case "alchemy" -> MobileApp.ALCHEMY;
            default -> throw new IllegalArgumentException("Unknown app: " + app);
        };
    }
}