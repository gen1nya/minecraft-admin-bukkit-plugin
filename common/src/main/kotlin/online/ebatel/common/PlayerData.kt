package online.ebatel.common

data class PlayerData(
    val name: String?,
    val uuid: String,
    val isOp: Boolean,
    val isOnline: Boolean,
    val isBanned: Boolean,
    val gameMode: String
)
