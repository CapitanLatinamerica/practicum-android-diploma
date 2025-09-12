package ru.practicum.android.diploma

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.TextAppearanceSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            text.lines().forEachIndexed { lineIndex, origLine ->
                val line = origLine.trim()
                if (line.isNotBlank()) {
                    if (lineIndex > 0) append("\n")
                    if (line.endsWith(":")) { // Если строка заканчивается двоеточием
                        append(line) // НЕ добавляем префикс, а применяем стиль TextMedium16
                    } else if (line.startsWith("- ")) {
                        // Если строка начинается с "- ", убираем и добавляем префикс
                        val modifiedLine = line.removePrefix("- ")
                        // форматируем эту строку с префиксом
                        processLine(modifiedLine, formattingContext)
                    } else {
                        // Иначе обычный процесс с префиксом
                        processLine(line, formattingContext)
                    }
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

    // Метод для форматирования описания (без префикса для сплошного текста)
    fun formatDescriptionTextWithPaint(
        context: Context,
        text: String,
        paint: TextPaint,
        availableWidth: Int
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder()
        val lines = text.lines()
        var previousLineWasHeader = false

        lines.forEachIndexed { index, originalLine ->
            val line = originalLine.trim()

            if (line.isNotBlank()) {
                processLine(
                    LineProcessingParams(
                        context = context,
                        spannable = spannable,
                        line = line,
                        index = index,
                        paint = paint,
                        availableWidth = availableWidth,
                        previousLineWasHeader = previousLineWasHeader
                    )
                )
                previousLineWasHeader = line.endsWith(":")
            }
        }
        return spannable
    }

    // Метод для форматирования навыков (всегда с префиксом)
    fun formatSkillsTextWithPaint(
        text: String,
        paint: TextPaint,
        availableWidth: Int,
        prefix: String = " • "
    ): String {
        return autoFormatTextWithPaint(text, paint, availableWidth, prefix)
    }

    private fun processLine(params: LineProcessingParams) {
        addExtraNewlineIfNeeded(
            spannable = params.spannable,
            line = params.line,
            index = params.index,
            previousLineWasHeader = params.previousLineWasHeader
        )

        when {
            params.line.endsWith(":") -> processHeaderLine(
                context = params.context,
                spannable = params.spannable,
                line = params.line
            )

            params.line.startsWith("- ") -> processListItem(
                spannable = params.spannable,
                line = params.line,
                paint = params.paint,
                availableWidth = params.availableWidth
            )

            else -> processRegularLine(
                spannable = params.spannable,
                line = params.line,
                paint = params.paint,
                availableWidth = params.availableWidth
            )
        }
    }

    private fun addExtraNewlineIfNeeded(
        spannable: SpannableStringBuilder,
        line: String,
        index: Int,
        previousLineWasHeader: Boolean
    ) {
        if (line.endsWith(":") && index > 0 && !previousLineWasHeader) {
            spannable.append("\n")
        }
        if (index > 0) {
            spannable.append("\n")
        }
    }

    private fun processHeaderLine(
        context: Context,
        spannable: SpannableStringBuilder,
        line: String
    ) {
        val start = spannable.length
        spannable.append(line)
        val end = spannable.length
        spannable.setSpan(
            TextAppearanceSpan(context, R.style.TextMedium16),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun processListItem(
        spannable: SpannableStringBuilder,
        line: String,
        paint: TextPaint,
        availableWidth: Int
    ) {
        val cleanLine = line.removePrefix("- ")
        val formattedText = autoFormatTextWithPaint(
            text = cleanLine,
            paint = paint,
            availableWidth = availableWidth,
            prefix = " • "
        )
        spannable.append(formattedText)
    }

    private fun processRegularLine(
        spannable: SpannableStringBuilder,
        line: String,
        paint: TextPaint,
        availableWidth: Int
    ) {
        val formattedText = autoFormatTextWithPaint(
            text = line,
            paint = paint,
            availableWidth = availableWidth,
            prefix = ""
        )
        spannable.append(formattedText)
    }
}

// Data class для группировки параметров форматирования
private data class FormattingContext(
    val prefix: String,
    val indent: String,
    val paint: TextPaint,
    val availableWidth: Int
)

private data class LineProcessingParams(
    val context: Context,
    val spannable: SpannableStringBuilder,
    val line: String,
    val index: Int,
    val paint: TextPaint,
    val availableWidth: Int,
    val previousLineWasHeader: Boolean
)
