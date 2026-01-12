package online.ebatel.forge;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.ConcurrentLinkedDeque;

public class TpsTracker {
    private static final int SAMPLE_SIZE_1M = 20 * 60;       // 1 minute
    private static final int SAMPLE_SIZE_5M = 20 * 60 * 5;   // 5 minutes
    private static final int SAMPLE_SIZE_15M = 20 * 60 * 15; // 15 minutes

    private static final ConcurrentLinkedDeque<Long> tickTimes = new ConcurrentLinkedDeque<>();
    private static long lastTickTime = System.nanoTime();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        long currentTime = System.nanoTime();
        long tickDuration = currentTime - lastTickTime;
        lastTickTime = currentTime;

        tickTimes.addLast(tickDuration);

        while (tickTimes.size() > SAMPLE_SIZE_15M) {
            tickTimes.pollFirst();
        }
    }

    public static double getTps1m() {
        return calculateTps(SAMPLE_SIZE_1M);
    }

    public static double getTps5m() {
        return calculateTps(SAMPLE_SIZE_5M);
    }

    public static double getTps15m() {
        return calculateTps(SAMPLE_SIZE_15M);
    }

    private static double calculateTps(int sampleSize) {
        Long[] samples = tickTimes.toArray(new Long[0]);
        if (samples.length == 0) return 20.0;

        int actualSampleSize = Math.min(sampleSize, samples.length);
        int startIndex = samples.length - actualSampleSize;

        double sum = 0;
        for (int i = startIndex; i < samples.length; i++) {
            sum += samples[i];
        }

        double avgTickTimeNanos = sum / actualSampleSize;
        double avgTickTimeSeconds = avgTickTimeNanos / 1_000_000_000.0;

        double tps = avgTickTimeSeconds > 0 ? 1.0 / avgTickTimeSeconds : 20.0;
        return Math.min(20.0, tps);
    }
}
