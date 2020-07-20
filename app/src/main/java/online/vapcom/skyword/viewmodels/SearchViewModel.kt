/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.launch
import online.vapcom.skyword.data.DictRepository
import online.vapcom.skyword.data.DictionaryWord
import online.vapcom.skyword.data.RepoReply
import online.vapcom.skyword.ui.states.ScreenState
import online.vapcom.skyword.ui.states.SearchState
import online.vapcom.skyword.utils.singleArgViewModelFactory

private const val TAG = "SearchVM.."

/**
 * Модель поиска слова в словаре
 */
class SearchViewModel(protected val dictRepository: DictRepository) : BaseViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::SearchViewModel)
    }

    /**
     * Текущее состояние процесса поиска
     */
    private lateinit var _state: MutableLiveData<ScreenState<SearchState>>
    val state: LiveData<ScreenState<SearchState>>
        get() {
            if (!::_state.isInitialized) {
                _state = MutableLiveData()
                _state.value = ScreenState.Render(SearchState.Ready)
            }
            return _state
        }

    val wordForSearch: MutableLiveData<String> = MutableLiveData("")

    // Флаг валидности ведённых данных, для разблокировки кнопки перевода
    // заодно меняем состояния неизвестности слова и очищаем список найденных, если слово почистили
    val isDataValid: LiveData<Boolean> = Transformations.map(wordForSearch) {
        _unknownWord.value = false
        if(it.isBlank())
            _foundWords.value = emptyList()
        it.isNotBlank()
    }

    /**
     * Флаг того, что слово не найдено
     */
    private val _unknownWord: MutableLiveData<Boolean> = MutableLiveData(false)
    val unknownWord: LiveData<Boolean>
        get() = _unknownWord

    /**
     * Список найденных слов
     */
    private val _foundWords: MutableLiveData<List<DictionaryWord>> = MutableLiveData()
    val foundWords: LiveData<List<DictionaryWord>>
        get() = _foundWords

    /**
     * Запускает поиск слова из wordForSearch
     */
    fun searchWord() {
        Log.i(TAG, "++++ searchWord: '${wordForSearch.value ?: "----"}'")
        uiScope.launch {
            _state.value = ScreenState.Loading

            val reply = dictRepository.searchWord(wordForSearch.value ?: "")
            Log.i(TAG, "++++ searchWord: reply: $reply")

            when(reply){
                is RepoReply.FoundWords -> {
                    Log.i(TAG, "++++ searchWord: FoundWords: ${reply.words}")

                    _foundWords.value = reply.words
                    _state.value = ScreenState.Render(SearchState.FoundTranslations(reply.words))
                }
                is RepoReply.UnknownWord -> {
                    _unknownWord.value = true
                    _state.value = ScreenState.Render(SearchState.UnknownWord)
                }
                is RepoReply.Error -> {
                    _state.value = ScreenState.Render(SearchState.ShowError(reply.error))
                }
                else -> Log.e(TAG, "Error: unknown reply: $reply")
            }
        }
    }

}
