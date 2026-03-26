package ui.wizard

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginData {
    fun register(
        email: String,
        password: String
    ): String {
        val url = URL("http://10.196.140.20:5000/auth/login")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }.toString()


        conn.outputStream.write(body.toByteArray())

        val response = conn.inputStream.bufferedReader().readText()
        conn.disconnect()

        return response
    }
}