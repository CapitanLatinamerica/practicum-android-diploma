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

    // Метод для форматирования списка описания, требований, условий, навыков
    fun autoFormatTextWithPaint(
        text: String,
        paint: TextPaint,
        availableWidth: Int,
        prefix: String = " • "
    ): String {
        val indent = " ".repeat(prefix.length)
        val formattingContext = FormattingContext(prefix, indent, paint, availableWidth)

        return buildString {
            text.lines().forEachIndexed { lineIndex, line ->
                if (line.isNotBlank()) {
                    if (lineIndex > 0) append("\n")
                    processLine(line.trim(), formattingContext)
                }
            }
        }
    }

    private fun StringBuilder.processLine(
        line: String,
        context: FormattingContext
    ) {
        val words = line.split(" ")
        var currentLine = StringBuilder(context.prefix)
        var currentLineWidth = context.paint.measureText(context.prefix)

        words.forEach { word ->
            currentLineWidth = processWord(
                word = word,
                currentLine = currentLine,
                currentLineWidth = currentLineWidth,
                context = context
            )
        }

        appendCurrentLineIfNotEmpty(currentLine)
    }

    private fun StringBuilder.processWord(
        word: String,
        currentLine: StringBuilder,
        currentLineWidth: Float,
        context: FormattingContext
    ): Float {
        var newLineWidth = currentLineWidth
        val wordWidth = context.paint.measureText(" $word")

        return if (shouldBreakLine(newLineWidth, wordWidth, context, currentLine)) {
            breakLine(currentLine, word, context)
            context.paint.measureText("${context.indent}$word")
        } else {
            addWordToCurrentLine(word, currentLine, newLineWidth, context.paint)
        }
    }

    private fun shouldBreakLine(
        currentLineWidth: Float,
        wordWidth: Float,
        context: FormattingContext,
        currentLine: StringBuilder
    ): Boolean {
        return currentLineWidth + wordWidth > context.availableWidth &&
            currentLine.length > context.prefix.length
    }

    private fun StringBuilder.breakLine(
        currentLine: StringBuilder,
        word: String,
        context: FormattingContext
    ) {
        append(currentLine.toString())
        append("\n")
        currentLine.clear()
        currentLine.append(context.indent).append(word)
    }

    private fun addWordToCurrentLine(
        word: String,
        currentLine: StringBuilder,
        currentLineWidth: Float,
        paint: TextPaint
    ): Float {
        var newLineWidth = currentLineWidth

        if (currentLine.length > " • ".length) {
            currentLine.append(" ")
            newLineWidth += paint.measureText(" ")
        }

        currentLine.append(word)
        newLineWidth += paint.measureText(word)

        return newLineWidth
    }

    private fun StringBuilder.appendCurrentLineIfNotEmpty(currentLine: StringBuilder) {
        if (currentLine.isNotEmpty()) {
            append(currentLine.toString())
        }
    }


}

// Data class для группировки параметров форматирования
private data class FormattingContext(
    val prefix: String,
    val indent: String,
    val paint: TextPaint,
    val availableWidth: Int
)
