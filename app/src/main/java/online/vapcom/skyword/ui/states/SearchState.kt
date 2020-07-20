/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui.states

import online.vapcom.skyword.data.ErrorDescription
import online.vapcom.skyword.data.DictionaryWord

/**
 * Класс для получения ответов и индикаций от ViewModel в SearchFragment
 */
sealed class SearchState {
    object Ready : SearchState()            // модель готова к работе
    class ShowError(val error: ErrorDescription) : SearchState()     // ошибка взаимодействия с сервером
    class FoundTranslations(val translations: List<DictionaryWord>) : SearchState()    // найденные переводы слова
    object UnknownWord : SearchState()      // слово не найдено
}
