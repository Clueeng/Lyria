package cat.psychward.dbus.utils

import com.sun.net.httpserver.HttpsParameters
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class HttpUtil {
    companion object {
        fun request(literalUrl: String): WebResponse {
            try {
                val url = URI.create(literalUrl).toURL()
                val conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "GET"
                conn.connectTimeout = 15_000
                conn.readTimeout = 15_000

                val responseCode = conn.responseCode
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = WebResponse(responseCode, reader.readText())

                reader.close()
                conn.disconnect()

                return response
            } catch (e: Exception) {
                return WebResponse(-1, "Exception: ${e.message}")
            }
        }

        fun toUrl(baseUrl: String, parameters: HashMap<String, String>): String {
            val query = parameters.entries.joinToString("&") { (key, value) ->
                "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=" +
                        URLEncoder.encode(value, StandardCharsets.UTF_8)
            }
            return "$baseUrl?$query"
        }
    }
}
class WebResponse(val code: Int, val body: String)