package online.ebatel.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import online.ebatel.forge.commands.PlayerListCommand;
import online.ebatel.forge.commands.ServerStatsCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WebAdminMod.MOD_ID)
public class WebAdminMod {
    public static final String MOD_ID = "webadminplugin";
    public static final Logger LOGGER = LogManager.getLogger();

    private WebhookClient webhookClient;
    private ChatEventHandler chatEventHandler;

    public WebAdminMod() {
        ModConfig.register();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TpsTracker.class);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("WebAdminPlugin (Forge) enabled!");

        // Setup chat webhook
        if (ModConfig.WEBHOOK_ENABLED.get()) {
            String webhookUrl = ModConfig.getWebhookUrl();
            if (webhookUrl != null) {
                webhookClient = new WebhookClient(LOGGER);
                chatEventHandler = new ChatEventHandler(webhookClient);
                MinecraftForge.EVENT_BUS.register(chatEventHandler);
                LOGGER.info("Chat webhook enabled: {}", webhookUrl);
            } else {
                LOGGER.warn("Webhook URL is not configured. Chat forwarding disabled.");
            }
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (chatEventHandler != null) {
            MinecraftForge.EVENT_BUS.unregister(chatEventHandler);
        }
        if (webhookClient != null) {
            webhookClient.shutdown();
        }
        LOGGER.info("WebAdminPlugin (Forge) disabled!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PlayerListCommand.register(event.getDispatcher());
        ServerStatsCommand.register(event.getDispatcher());
        LOGGER.info("WebAdminPlugin commands registered!");
    }
}
