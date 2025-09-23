package ru.practicum.android.diploma

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.TextAppearanceSpan

object ToolsText {
    /**
     * Автоматически форматирует текст с использованием переноса строк и префиксов.
     * Используется для списков (требования, условия, навыки).
     *
     * Логика:
     * - строки, заканчивающиеся на ":" — заголовки;
     * - строки, начинающиеся с "- " или заканчивающиеся на ";" — элементы списка;
     * - остальные строки — обычный текст.
     */
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
                    } else if (line.startsWith("- ") || line.endsWith(";")) {
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

    // Обрабатывает одну строку текста: добавляет префикс (•), переносит по ширине
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

    /**
     * Обрабатывает слово: решает, поместить ли его в текущую строку
     * или перенести на новую строку
     */
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

    // Форматирует многострочный текст для описаний вакансии
    fun formatDescriptionTextWithPaint(
        context: Context,
        text: String,
        paint: TextPaint,
        availableWidth: Int
    ): SpannableStringBuilder {
        val spannable = SpannableStringBuilder()
        val lines = text.lines()
        var previousLineWasHeader = false
        val contextState = ContextState()

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
                        previousLineWasHeader = previousLineWasHeader,
                        contextState = contextState
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

    /**
     * Определяет, как обрабатывать конкретную строку:
     * - заголовок
     * - элемент списка (•)
     * - обычный текст
     * Учитывает контекст (находимся ли мы внутри списка)
     */
    private fun processLine(params: LineProcessingParams) {
        addExtraNewlineIfNeeded(
            spannable = params.spannable,
            line = params.line,
            index = params.index,
            previousLineWasHeader = params.previousLineWasHeader
        )

        val line = params.line

        when {
            line.endsWith(":") -> {
                processHeaderLine(params.context, params.spannable, line)
                params.contextState.insideList = false
            }

            // Элемент списка: "- ", ";", либо "." если продолжается блок списка
            line.startsWith("- ") || line.endsWith(";") ||
                line.endsWith(".") && params.contextState.insideList -> {
                val cleanLine = when {
                    line.startsWith("- ") -> line.removePrefix("- ")
                    else -> line
                }
                processListItem(params.spannable, cleanLine, params.paint, params.availableWidth)
                params.contextState.insideList = true
            }

            else -> {
                processRegularLine(params.spannable, line, params.paint, params.availableWidth)
                params.contextState.insideList = false
            }
        }
    }

    // Добавляет дополнительный перенос строки
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

    // Обрабатывает заголовок: применяет стиль TextMedium16
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

    // Обрабатывает элемент списка: добавляет префикс • и форматирует переносы
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

    // Обрабатывает обычный текст без префикса
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

// Контекст форматирования одной строки
private data class FormattingContext(
    val prefix: String,
    val indent: String,
    val paint: TextPaint,
    val availableWidth: Int
)

// Параметры для обработки строки в Spannable
private data class LineProcessingParams(
    val context: Context,
    val spannable: SpannableStringBuilder,
    val line: String,
    val index: Int,
    val paint: TextPaint,
    val availableWidth: Int,
    val previousLineWasHeader: Boolean,
    val contextState: ContextState
)

// Проверяет, нужно ли переносить слово на новую строку
private fun shouldBreakLine(
    currentLineWidth: Float,
    wordWidth: Float,
    context: FormattingContext,
    currentLine: StringBuilder
): Boolean {
    return currentLineWidth + wordWidth > context.availableWidth &&
        currentLine.length > context.prefix.length
}

// Добавляет слово в текущую строку
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

// Добавляет текущую строку в итоговый текст, если она не пустая
private fun StringBuilder.appendCurrentLineIfNotEmpty(currentLine: StringBuilder) {
    if (currentLine.isNotEmpty()) {
        append(currentLine.toString())
    }
}

// Переносит слово на новую строку с учётом отступа
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

// Флаговое состояние контекста: находимся ли мы внутри списка
private class ContextState(
    var insideList: Boolean = false
)

