package cat.psychward.dbus.api.player.lyrics

import cat.psychward.dbus.api.data.LyricLine
import cat.psychward.dbus.api.data.LyricsSnapshot
import cat.psychward.dbus.api.data.Timestamp
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.utils.HttpUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object LyricsService {

    private val cache = mutableMapOf<String, LyricsSnapshot>()

    fun fetch(track: TrackMetadata): LyricsSnapshot {
        val key = "${track.name}:${track.artists.first()}"

        cache[key]?.let { return it }

        val response = HttpUtil.request(
            HttpUtil.toUrl(
                "https://lrclib.net/api/get",
                hashMapOf(
                    "duration" to track.trackLength.toInt().toString(),
                    "track_name" to track.name,
                    "artist_name" to track.artists.first(),
                    "album_name" to track.album
                )
            )
        )

        if (response.code != 200) {
            return LyricsSnapshot(emptyList(), emptyList())
        }

        val snapshot = parse(response.body)
        cache[key] = snapshot

        return snapshot
    }

    private fun parseSyncedLyrics(raw: String?): List<LyricLine> {
        if (raw.isNullOrBlank()) {
            return emptyList()
        }

        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]\s*(.*)""")

        return raw
            .lines()
            .mapNotNull { line ->
                val match = regex.matchEntire(line.trim())
                    ?: return@mapNotNull null

                val (minutes, seconds, centiseconds, text) = match.destructured

                val totalSeconds =
                    minutes.toDouble() * 60 +
                            seconds.toDouble() +
                            (centiseconds.toDouble() / 100)

                LyricLine(
                    timestamp = totalSeconds,
                    text = text.trim()
                )
            }
            .sortedBy { it.timestamp }
    }

    private fun parse(body: String): LyricsSnapshot {
        val json = Json.parseToJsonElement(body).jsonObject

        val synced = parseSyncedLyrics(
            json["syncedLyrics"]?.jsonPrimitive?.content
        )

        val plain = json["plainLyrics"]
            ?.jsonPrimitive
            ?.content
            ?.lines()
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return LyricsSnapshot(synced, plain)
    }
}