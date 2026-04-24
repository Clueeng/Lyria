package cat.psychward.dbus.api.data

data class LyricsSnapshot(
    val synced: List<LyricLine>,
    val plain: List<String>
) {
    fun currentAt(seconds: Double): LyricLine? {
        return synced.lastOrNull { it.timestamp <= seconds }
    }

    fun nextAt(seconds: Double): LyricLine? {
        return synced.firstOrNull { it.timestamp > seconds }
    }

    fun windowAt(
        seconds: Double,
        before: Int = 2,
        after: Int = 1
    ): List<LyricLine> {
        if (synced.isEmpty()) return emptyList()

        val currentIndex = synced.indexOfLast {
            it.timestamp <= seconds
        }

        if (currentIndex == -1) {
            return synced.take(after + 1)
        }

        val start = (currentIndex - before).coerceAtLeast(0)
        val end = (currentIndex + after + 1).coerceAtMost(synced.size)

        return synced.subList(start, end)
    }
}