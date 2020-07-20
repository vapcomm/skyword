/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import online.vapcom.skyword.network.GetMeaningDetailsCall
import online.vapcom.skyword.network.SearchWordCall

private const val TAG = "DictRepo.."
private const val ERROR_MODULE_NUM = 10     // номер модуля в кодах ошибок, см. ErrorDescription
// последний использованный код ошибки: 10

/**
 * Рабочая реализация репозитория словаря
 */
class DictRepositoryImpl: DictRepository{

    /**
     * Поиск слова в словаре
     */
    override suspend fun searchWord(word: String): RepoReply = withContext(Dispatchers.IO) {
        Log.w(TAG, ">>> searchWord: '$word'")

        //TODO: поиск слова в кэше

        val call = SearchWordCall(word.trim())
        call.execute()
        if (call.success) {
            if(call.foundWords.isEmpty())
                RepoReply.UnknownWord
            else RepoReply.FoundWords(call.foundWords)
            //TODO: добавить слово в кэш
        } else {
            RepoReply.Error(call.errorDescription)
        }
    }

    /**
     * Загрузка подробных данных значения слова по его ID
     */
    override suspend fun getMeaningDetails(meaningID: String): RepoReply = withContext(Dispatchers.IO) {
        Log.w(TAG, ">>> getMeaningDetails: ID: '$meaningID'")

        val call = GetMeaningDetailsCall(meaningID)
        call.execute()
        if (call.success) {
            if(call.hasDetails) //TODO: кэшировать полученные данные
                RepoReply.WordMeaningDetails(call.meaningDetails)
            else RepoReply.UnknownWord
        } else {
            RepoReply.Error(call.errorDescription)
        }
    }
}
