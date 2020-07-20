/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

/**
 * Подробности значения слова
 */
data class MeaningDetails(
    val id: String = "",            // ID значения
    val pos: PartOfSpeech = PartOfSpeech.UNKNOWN,      // часть речи
    val text: String = "",          // слово (фраза) от которого берётся это значение
    val transcription: String = "", // транскрипция слова
    val translation: String = "",   // перевод (note не используем)
    val mnemonics: String = "",     // пример фразы для запоминания слова с тегами для выделения слов
    val imageURL: String = ""       // URL картинки
)
