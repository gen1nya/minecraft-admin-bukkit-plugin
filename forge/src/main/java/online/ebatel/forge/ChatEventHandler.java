package online.ebatel.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChatEventHandler {
    private final WebhookClient webhookClient;

    public ChatEventHandler(WebhookClient webhookClient) {
        this.webhookClient = webhookClient;
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        String webhookUrl = ModConfig.getWebhookUrl();
        if (webhookUrl == null) {
            return;
        }

        ServerPlayer player = event.getPlayer();
        String playerName = player.getName().getString();
        String playerUuid = player.getUUID().toString();
        String message = event.getMessage().getString();

        webhookClient.sendChatMessage(webhookUrl, playerName, playerUuid, message);
    }
}
