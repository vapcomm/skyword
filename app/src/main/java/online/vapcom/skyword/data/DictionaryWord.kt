/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

/**
 * Данные по слову, полученные из словаря
 */
data class DictionaryWord(
    val id: String,
    val text: String,               // найденные слово или фраза
    val meanings: List<Meaning>     // список значений этого слова с переводами
)

/**
 * Значение слова/фразы
 */
data class Meaning(
    val id: String,
    val pos: PartOfSpeech,      // часть речи
    val translation: String,    // перевод (note не используем)
    val imageURL: String        // URL большой картинки
)

/**
 * Части речи, которые могут быть в словаре
 */
enum class PartOfSpeech {
    UNKNOWN,
    NOUN,
    VERB,
    ADJECTIVE,
    ADVERB,
    PREPOSITION,
    PRONOUN,
    CARDINAL_NUMBER,
    CONJUNCTION,
    INTERJECTION,
    ARTICLE,
    ABBREVIATION,
    PARTICLE,
    ORDINAL_NUMBER,
    MODAL_VERB,
    PHRASE,
    IDIOM;

    companion object {
        private val serverPOSMap: Map<String, PartOfSpeech> = mapOf(
            "n" to NOUN,
            "v" to VERB,
            "j" to ADJECTIVE,
            "r" to ADVERB,
            "prp" to PREPOSITION,
            "prn" to PRONOUN,
            "crd" to CARDINAL_NUMBER,
            "cjc" to CONJUNCTION,
            "exc" to INTERJECTION,
            "det" to ARTICLE,
            "abb" to ABBREVIATION,
            "x" to PARTICLE,
            "ord" to ORDINAL_NUMBER,
            "md" to MODAL_VERB,
            "ph" to PHRASE,
            "phi" to IDIOM
        )

        fun fromServerPOSCode(partOfSpeechCode: String): PartOfSpeech {
            return serverPOSMap[partOfSpeechCode] ?: UNKNOWN
        }

    }

}