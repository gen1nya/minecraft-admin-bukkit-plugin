package online.ebatel.fabric

import java.util.concurrent.ConcurrentLinkedDeque

/**
 * Rolling TPS estimate computed from real tick durations (System.nanoTime deltas at END of each
 * server tick). Same algorithm as the Forge TpsTracker, but driven by Fabric's ServerTickEvents
 * so it stays free of any version-specific MinecraftServer tick-time API.
 */
object TpsTracker {
    private const val SAMPLE_SIZE_1M = 20 * 60        // 1 minute
    private const val SAMPLE_SIZE_5M = 20 * 60 * 5    // 5 minutes
    private const val SAMPLE_SIZE_15M = 20 * 60 * 15  // 15 minutes

    private val tickTimes = ConcurrentLinkedDeque<Long>()

    @Volatile
    private var lastTickTime = System.nanoTime()

    fun onServerTick() {
        val currentTime = System.nanoTime()
        val tickDuration = currentTime - lastTickTime
        lastTickTime = currentTime

        tickTimes.addLast(tickDuration)
        while (tickTimes.size > SAMPLE_SIZE_15M) {
            tickTimes.pollFirst()
        }
    }

    fun getTps1m(): Double = calculateTps(SAMPLE_SIZE_1M)
    fun getTps5m(): Double = calculateTps(SAMPLE_SIZE_5M)
    fun getTps15m(): Double = calculateTps(SAMPLE_SIZE_15M)

    private fun calculateTps(sampleSize: Int): Double {
        val samples = tickTimes.toTypedArray()
        if (samples.isEmpty()) return 20.0

        val actualSampleSize = minOf(sampleSize, samples.size)
        val startIndex = samples.size - actualSampleSize

        var sum = 0.0
        for (i in startIndex until samples.size) {
            sum += samples[i]
        }

        val avgTickTimeNanos = sum / actualSampleSize
        val avgTickTimeSeconds = avgTickTimeNanos / 1_000_000_000.0

        val tps = if (avgTickTimeSeconds > 0) 1.0 / avgTickTimeSeconds else 20.0
        return minOf(20.0, tps)
    }
}
