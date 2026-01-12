package online.ebatel.common

object MemoryUtils {
    fun getUsedMemoryMB(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
    }

    fun getAllocatedMemoryMB(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() / 1024 / 1024
    }
}
