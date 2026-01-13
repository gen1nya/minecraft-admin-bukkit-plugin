package online.ebatel.forge;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebhookClient {
    private static final Gson GSON = new Gson();
    private final Logger logger;
    private final ExecutorService executor;

    public WebhookClient(Logger logger) {
        this.logger = logger;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void sendChatMessage(String webhookUrl, String player, String playerUuid, String message) {
        executor.submit(() -> {
            try {
                ChatPayload payload = new ChatPayload(player, playerUuid, message);
                String json = GSON.toJson(payload);

                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();
                if (responseCode < 200 || responseCode >= 300) {
                    logger.warn("Webhook request failed with status: {}", responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                logger.warn("Failed to send webhook: {}", e.getMessage());
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

    private static class ChatPayload {
        final String player;
        final String playerUuid;
        final String message;

        ChatPayload(String player, String playerUuid, String message) {
            this.player = player;
            this.playerUuid = playerUuid;
            this.message = message;
        }
    }
}
