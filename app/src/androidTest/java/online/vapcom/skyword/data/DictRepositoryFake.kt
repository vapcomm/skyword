/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

import android.util.Log

private const val TAG = "DictReFake"
/**
 * Тестовый репозиторий словаря
 */
class DictRepositoryFake : DictRepository {

    override suspend fun searchWord(word: String): RepoReply {
        Log.w(TAG, "==== searchWord: '$word'")

        return when(word) {
            "rain" -> RepoReply.FoundWords(listOf(
                DictionaryWord("1", "rain", listOf(Meaning("10", PartOfSpeech.NOUN, "дождь", ""))),
                DictionaryWord("2", "rain cloud", listOf(Meaning("20", PartOfSpeech.NOUN, "туча", "")))
            ))

            "error" -> RepoReply.Error(ErrorDescription(false, 10, "Test error"))
            "unknown" -> RepoReply.UnknownWord
            else -> RepoReply.Error(ErrorDescription(false, 11, "Unsupported word"))
        }
    }

    override suspend fun getMeaningDetails(meaningID: String): RepoReply {
        //TODO: возвращать разные значения в зависимости от meaningID
        return RepoReply.UnknownMeaning
    }

}
