/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.network

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import online.vapcom.skyword.data.*

private const val TAG = "SchWordCll"
private const val ERROR_MODULE_NUM = 51     // номер модуля в кодах ошибок, см. ErrorDescription

/**
 * Запрос поиска слова в словаре
 */
class SearchWordCall(private val word: String): BaseServerCall() {

    // ответ на верхний уровень
    var foundWords: List<DictionaryWord> = emptyList()

    /**
     * Выполняет запрос на сервер, должна запускаться в IO-нити
     */
    fun execute() {
        if(word.isBlank()) {
            success = false
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 12),"Empty word")
            return
        }
        executeGetRequest("$SEARCH_WORD_URL?search=$word")
    }

    /**
     * Ответ, приходящий от сервера.
     * Пример JSON см. в docs/api_readme.txt
     */
    @JsonClass(generateAdapter = true)
    data class ServerWord(val id: String = "", val text: String = "", val meanings: List<ServerMeaning> = emptyList())

    @JsonClass(generateAdapter = true)
    data class ServerMeaning(val id: String = "", val partOfSpeechCode: String = "",
                             val translation: ServerTranslation = ServerTranslation(), val imageUrl: String = "")

    @JsonClass(generateAdapter = true)
    data class ServerTranslation(val text: String = "") // note игнорируем

    override fun parseJson(moshi: Moshi, json: String) {
        // сервер присылает список слов на верхнем уровне
        val listType = Types.newParameterizedType(List::class.java, ServerWord::class.java)
        val jsonAdapter: JsonAdapter<List<ServerWord>> = moshi.adapter(listType)

        val r = jsonAdapter.fromJson(json)
        if (r == null) {
            Log.e(TAG, "Error: unable to parse server response: '$json'")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 10),"Unable to parse server response")
        } else {
            foundWords = r.map { sw ->
                DictionaryWord(sw.id, sw.text, sw.meanings.map { sm ->
                    Meaning(
                        id = sm.id,
                        pos = PartOfSpeech.fromServerPOSCode(sm.partOfSpeechCode),
                        translation = sm.translation.text,
                        imageURL = addSchemeToURL(sm.imageUrl)  //NOTE: с сервера URL приходят урезанные, без схемы
                    )
                })
            }

            success = true
        }
    }

}