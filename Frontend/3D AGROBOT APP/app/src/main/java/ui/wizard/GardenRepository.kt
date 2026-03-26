package ui.wizard

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GardenRepository {

    private val baseUrl = "https://3d-agrobot-production.up.railway.app"

    fun getGardens(token: String): List<GardenData> {
        val url = URL("$baseUrl/garden/list")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val response = stream.bufferedReader().use { it.readText() }
        conn.disconnect()

        val list = mutableListOf<GardenData>()

        try {
            val root = JSONObject(response)

            if (root.has("gardens")) {
                val arr = root.getJSONArray("gardens")

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    list.add(
                        GardenData(
                            id = obj.getInt("id"),
                            garden_name = obj.optString("garden_name", "Unknown"),
                            garden_width = obj.optInt("garden_width", 0),
                            garden_height = obj.optInt("garden_height", 0),
                            path_width = obj.optInt("path_width", 0),
                            number_beds = obj.optInt("number_beds", 0),
                            plant = obj.optString("plant", "")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    fun createGarden(token: String, data: GardenData): Int {
        val url = URL("$baseUrl/garden/create")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.doOutput = true

        val body = JSONObject().apply {
            put("garden_name",   data.garden_name)
            put("garden_width",  data.garden_width)
            put("garden_height", data.garden_height)
            put("path_width",    data.path_width)
            put("number_beds",   data.number_beds)
            put("plant",         data.plant)
        }.toString()

        conn.outputStream.use { it.write(body.toByteArray()) }
        val code = conn.responseCode
        conn.disconnect()
        return code
    }

    fun editGarden(token: String, id: Int, data: GardenData): Int {
        val url = URL("$baseUrl/garden/edit/$id")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $token")
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.doOutput = true

        val body = JSONObject().apply {
            put("garden_name",   data.garden_name)
            put("garden_width",  data.garden_width)
            put("garden_height", data.garden_height)
            put("path_width",    data.path_width)
            put("number_beds",   data.number_beds)
            put("plant",         data.plant)
        }.toString()

        conn.outputStream.use { it.write(body.toByteArray()) }
        val code = conn.responseCode
        conn.disconnect()
        return code
    }
}