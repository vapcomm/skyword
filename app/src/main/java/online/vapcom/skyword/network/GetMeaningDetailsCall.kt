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

private const val TAG = "GetWordDCl"
private const val ERROR_MODULE_NUM = 52     // номер модуля в кодах ошибок, см. ErrorDescription

/**
 * Запрос данных значения слова
 */
class GetMeaningDetailsCall(private val meaningID: String): BaseServerCall() {

    // ответ на верхний уровень
    var hasDetails: Boolean = false
    var meaningDetails: MeaningDetails = MeaningDetails()

    /**
     * Выполняет запрос на сервер, должна запускаться в IO-нити
     */
    fun execute() {
        if(meaningID.isBlank()) {
            success = false
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 12),"Empty word ID")
            return
        }
        executeGetRequest("$MEANING_DETAILS_URL?ids=$meaningID")
    }

    /**
     * Ответ, приходящий от сервера.
     * Пример JSON см. в docs/api_readme.txt
     */
    @JsonClass(generateAdapter = true)
    data class ServerMeaning(val id: String = "",                               // ID значения
                             val partOfSpeechCode: String = "",
                             val text: String = "",                             // слово, к которому относится запрашиваемое значение
                             val transcription: String = "",                    // транскрипция слова
                             val translation: ServerTranslation = ServerTranslation(),
                             val _mnemonics: String? = "",                      // пример фразы для запоминания слова, может быть null
                             val images: List<ServerImageUrl> = emptyList()     // массив URL картинок
    ) {
        val mnemonics: String = _mnemonics ?: ""
    }

    @JsonClass(generateAdapter = true)
    data class ServerTranslation(val text: String = "") // note игнорируем

    @JsonClass(generateAdapter = true)
    data class ServerImageUrl(val url: String = "")

    override fun parseJson(moshi: Moshi, json: String) {
        // сервер присылает список значений слов на верхнем уровне
        val listType = Types.newParameterizedType(List::class.java, ServerMeaning::class.java)
        val jsonAdapter: JsonAdapter<List<ServerMeaning>> = moshi.adapter(listType)

        val r = jsonAdapter.fromJson(json)
        if (r == null) {
            Log.e(TAG, "Error: unable to parse server response: '$json'")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 10),"Unable to parse server response")
        } else {
            if(r.isNotEmpty()) {
                // берём только первое значение из списка
                val sm = r[0]
                if(sm.id != meaningID) {    // ответ не на то, что запрашивали
                    Log.e(TAG, "Error: wrong meaning ID in response: ${sm.id}, requested: $meaningID")
                    errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 11),
                        "Wrong meaning ID in response: ${sm.id}, requested: $meaningID")
                    success = false
                    return
                }

                meaningDetails = MeaningDetails(
                    id = sm.id,
                    pos = PartOfSpeech.fromServerPOSCode(sm.partOfSpeechCode),
                    text = sm.text,
                    transcription = sm.transcription,
                    translation = sm.translation.text,
                    imageURL = addSchemeToURL(if(sm.images.isEmpty()) "" else sm.images[0].url)
                )
                hasDetails = true
            } else {
                hasDetails = false
            }

            success = true
        }
    }
}