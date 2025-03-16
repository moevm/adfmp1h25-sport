package com.khl_app.storage

import android.util.Base64
import org.json.JSONObject

fun getUserIdFromToken(jwtToken: String): String? {
    try {
        val parts = jwtToken.split(".")
        if (parts.size != 3) return null

        val payload = parts[1]
        val normalizedPayload = payload.padEnd((payload.length + 3) / 4 * 4, '=')
        val decodedBytes = Base64.decode(normalizedPayload, Base64.URL_SAFE)
        val decodedPayload = String(decodedBytes)

        val jsonObject = JSONObject(decodedPayload)

        val identity = jsonObject.optString("sub") ?: return null
        val cleanIdentity = identity
            .replace("'", "\"")

        val identityJson = JSONObject(cleanIdentity)

        return identityJson.optString("id")
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}