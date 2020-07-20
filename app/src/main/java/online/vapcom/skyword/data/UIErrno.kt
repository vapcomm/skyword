/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

/**
 * Коды ошибок, передаваемых в ErrorDescription, для отображения пользователю
 * или выполнения каких-либо действий на уровне UI
 */
enum class UIErrno(val errno: Int) {
    //NO_ERROR(0),
    UNKNOWN_PHONE_NUMBER(-2), // неизвестный номер телефона при логине или запросе SMS
    CONNECTION_ERROR(-3),     // ошибки установления соединения, в т.ч. отказ в соединении (Connection Refused)
    CLIENT_ERROR(-4),         // 400 ошибки
    SERVER_ERROR(-5),         // 500 ошибки
    TIMEOUT(-8),              // таймаут запроса
    DATA_NOT_FOUND(-9)        // данные не найдены
}
