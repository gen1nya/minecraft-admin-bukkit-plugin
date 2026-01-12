package online.ebatel.common

data class ServerStats(
    val version: String,
    val onlinePlayers: Int,
    val memoryUsedMB: Long,
    val memoryAllocatedMB: Long,
    val tps1m: Double,
    val tps5m: Double,
    val tps15m: Double
)
