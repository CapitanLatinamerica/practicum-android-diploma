package ru.practicum.android.diploma

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.widget.TextView
import android.text.TextPaint

object Tools {

    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

    fun <T> debounce(
        delayMillis: Long,
        coroutineScope: CoroutineScope,
        useLastParam: Boolean,
        action: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            if (useLastParam) {
                debounceJob?.cancel()
            }
            if (debounceJob?.isCompleted != false || useLastParam) {
                debounceJob = coroutineScope.launch {
                    delay(delayMillis)
                    action(param)
                }
            }
        }
    }

    //Метод для форматирования списка описания, требований, условий, навыков
    fun autoFormatTextWithPaint(
        text: String,
        paint: TextPaint,
        availableWidth: Int,
        prefix: String = " • "
    ): String {
        val indent = " ".repeat(prefix.length)

        return buildString {
            text.lines().forEachIndexed { lineIndex, line ->
                if (line.isNotBlank()) {
                    val trimmedLine = line.trim()
                    if (lineIndex > 0) append("\n")

                    val words = trimmedLine.split(" ")
                    var currentLine = StringBuilder(prefix)
                    var currentLineWidth = paint.measureText(prefix)

                    words.forEach { word ->
                        val wordWidth = paint.measureText(" $word")

                        if (currentLineWidth + wordWidth > availableWidth && currentLine.length > prefix.length) {
                            append(currentLine.toString())
                            append("\n")
                            currentLine = StringBuilder(indent).append(word)
                            currentLineWidth = paint.measureText("$indent$word")
                        } else {
                            if (currentLine.length > prefix.length) {
                                currentLine.append(" ")
                                currentLineWidth += paint.measureText(" ")
                            }
                            currentLine.append(word)
                            currentLineWidth += paint.measureText(word)
                        }
                    }

                    if (currentLine.isNotEmpty()) {
                        append(currentLine.toString())
                    }
                }
            }
        }
    }

    // Новая удобная функция для TextView
    fun TextView.formatTextWithBullets(
        textResource: Int,
        prefix: String = " • "
    ) {
        val originalText = context.getString(textResource)

        // Ждем когда layout будет готов для получения реальных размеров экрана
        this.post {
            val paint = this.paint
            val availableWidth = this.width - this.paddingLeft - this.paddingRight

            val formattedText = autoFormatTextWithPaint(
                text = originalText,
                paint = paint,
                availableWidth = availableWidth,
                prefix = prefix
            )

            this.text = formattedText
        }
    }
}
