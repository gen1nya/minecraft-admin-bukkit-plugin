package online.ebatel.common

import com.google.gson.Gson

object JsonSerializer {
    private val gson = Gson()

    fun toJson(data: Any): String = gson.toJson(data)

    fun serializePlayerList(players: List<PlayerData>): String = gson.toJson(players)

    fun serializeServerStats(stats: ServerStats): String = gson.toJson(stats)
}
