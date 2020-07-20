/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui.states

import online.vapcom.skyword.data.ErrorDescription

/**
 * Класс для получения ответов и индикаций от ViewModel в WordDetailsFragment
 */
sealed class DetailsState {
    object Ready : DetailsState()            // модель готова к работе, данные загружены
    class ShowError(val error: ErrorDescription) : DetailsState()     // ошибка взаимодействия с сервером
    object UnknownMeaning: DetailsState()    // значение солова не найдено, похоже на проблемы в бэке
}
