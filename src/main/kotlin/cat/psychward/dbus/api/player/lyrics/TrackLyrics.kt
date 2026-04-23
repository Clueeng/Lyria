package cat.psychward.dbus.api.player.lyrics

import cat.psychward.dbus.api.data.Timestamp
import cat.psychward.dbus.api.data.TrackMetadata
import cat.psychward.dbus.api.data.fromMillis
import cat.psychward.dbus.utils.HttpUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TrackLyrics(val metadata: TrackMetadata) {
    // https://lrclib.net/api/get?artist_name=Borislav+Slavov&track_name=I+Want+to+Live&album_name=Baldur%27s+Gate+3+(Original+Game+Soundtrack)&duration=233
    var lyrics: Map<Timestamp, String> = emptyMap()
    var plainLyrics: List<String> = emptyList()
    init {
        val requestUrl = HttpUtil.toUrl("https://lrclib.net/api/get",
            hashMapOf(
                "duration" to metadata.trackLength.toInt().toString(),
                "track_name" to metadata.name,
                "artist_name" to metadata.artists[0],
                "album_name" to metadata.album,
            )
        )
        println("trying $requestUrl")
        val response = HttpUtil.request(requestUrl)
        lyrics = if (response.code == 200) {
            parseSyncedLyrics(response.body)
        } else {
            emptyMap()
        }
        plainLyrics = if (response.code == 200) {
            parsePlainLyrics(response.body)
        }else {
            emptyList()
        }
    }

    private fun parsePlainLyrics(responseBody: String): List<String> {
        val json = Json.parseToJsonElement(responseBody).jsonObject

        return json["plainLyrics"]
            ?.jsonPrimitive
            ?.content
            ?.lines()
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    private fun parseSyncedLyrics(responseBody: String): Map<Timestamp, String> {
        val json = Json.parseToJsonElement(responseBody).jsonObject
        val syncedLyrics = json["syncedLyrics"]
            ?.jsonPrimitive
            ?.content
            ?: return emptyMap()

        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]\s*(.*)""")

        return syncedLyrics
            .lines()
            .mapNotNull { line ->
                val match = regex.matchEntire(line) ?: return@mapNotNull null

                val (minutes, seconds, centiseconds, text) = match.destructured

                val totalMillis =
                    minutes.toLong() * 60_000 +
                            seconds.toLong() * 1_000 +
                            centiseconds.toLong() * 10.0

                fromMillis(totalMillis) to text
            }
            .toMap()
    }

    fun getCurrentLyric(timestamp: Timestamp): String? {
        if(lyrics.isEmpty()) return null
        return lyrics
            .entries
            .sortedBy { it.key.nanoseconds }
            .lastOrNull { it.key.nanoseconds <= timestamp.nanoseconds }
            ?.value
    }
}