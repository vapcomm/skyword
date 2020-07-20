/*
 * (c) VAP Communications Group, 2020
 */


package online.vapcom.skyword.data

/**
 * Ответы от репозитория
 */
sealed class RepoReply {

    /**
     * Ошибка при обработке запроса
     */
    class Error(val error: ErrorDescription) : RepoReply() {
        override fun toString(): String {
            return "RepoReply: Error: $error"
        }
    }

    /**
     * Список найденных слов
     */
    class FoundWords(val words: List<DictionaryWord>) : RepoReply()

    /**
     * Слово не найдено
     */
    object UnknownWord: RepoReply()

    class WordMeaningDetails(val meaningDetails: MeaningDetails) : RepoReply()

    /**
     * Значение слова не найдено
     */
    object UnknownMeaning: RepoReply()

    /**
     * Операция завершена успешно
     */
    //object Success : RepoReply()

}
