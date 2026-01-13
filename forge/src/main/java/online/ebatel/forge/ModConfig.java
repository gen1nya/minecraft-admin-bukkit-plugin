package online.ebatel.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue WEBHOOK_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<String> WEBHOOK_URL;
    public static final ForgeConfigSpec.ConfigValue<String> SERVER_ID;

    static {
        BUILDER.push("webhook");

        WEBHOOK_ENABLED = BUILDER
                .comment("Enable chat forwarding to admin panel")
                .define("enabled", false);

        WEBHOOK_URL = BUILDER
                .comment("Admin panel base URL (e.g., https://mc-admin.example.com)")
                .define("url", "");

        SERVER_ID = BUILDER
                .comment("Server ID in admin panel")
                .define("serverId", "default");

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(Type.COMMON, SPEC);
    }

    public static String getWebhookUrl() {
        if (!WEBHOOK_ENABLED.get()) {
            return null;
        }

        String baseUrl = WEBHOOK_URL.get();
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }

        if (baseUrl.contains("/chat/webhook")) {
            return baseUrl;
        }

        String serverId = SERVER_ID.get();
        return baseUrl.replaceAll("/$", "") + "/api/servers/" + serverId + "/chat/webhook";
    }
}
