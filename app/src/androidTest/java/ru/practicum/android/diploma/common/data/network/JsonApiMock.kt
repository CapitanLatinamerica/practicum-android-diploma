package ru.practicum.android.diploma.common.data.network

import androidx.test.platform.app.InstrumentationRegistry

object JsonApiMock {
    fun getResponseFromFile(filePath: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        try {
            val inputStream = context.assets.open(filePath)
            val content = inputStream.bufferedReader().use { it.readText() }
            return content
        } catch (e: Exception) {
            throw e
        }
    }
}
