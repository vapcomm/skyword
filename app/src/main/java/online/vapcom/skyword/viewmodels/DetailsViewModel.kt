/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import online.vapcom.skyword.data.DictRepository
import online.vapcom.skyword.data.MeaningDetails
import online.vapcom.skyword.data.RepoReply
import online.vapcom.skyword.ui.states.DetailsState
import online.vapcom.skyword.ui.states.ScreenState
import online.vapcom.skyword.utils.singleArgViewModelFactory

private const val TAG = "DetailsVM."

/**
 * Модель подробного описания найденного слова
 */
class DetailsViewModel(protected val dictRepository: DictRepository) : BaseViewModel() {

    companion object {
        val FACTORY = singleArgViewModelFactory(::DetailsViewModel)
    }

    /**
     * Текущее состояние
     */
    private lateinit var _state: MutableLiveData<ScreenState<DetailsState>>
    val state: LiveData<ScreenState<DetailsState>>
        get() {
            if (!::_state.isInitialized) {
                _state = MutableLiveData()
                _state.value = ScreenState.Loading
            }
            return _state
        }

    var wordID: String = ""

    private val _meaningDetails: MutableLiveData<MeaningDetails> = MutableLiveData(MeaningDetails())
    val meaningDetails: LiveData<MeaningDetails>
        get() = _meaningDetails

    /**
     * Запускает загрузку подробных данных по найденному слову
     */
    fun setup(wordID: String) {
        this.wordID = wordID
        loadMeaningDetails(wordID)
    }


    /**
     * Загружает подробные данные по значению слова
     */
    private fun loadMeaningDetails(meaningID: String) {
        Log.i(TAG, "++++ loadWordDetails: meaningID: '$meaningID'")

        uiScope.launch {
            _state.value = ScreenState.Loading

            val reply = dictRepository.getMeaningDetails(meaningID)
            Log.i(TAG, "++++ loadWordDetails: reply: $reply")

            when(reply){
                is RepoReply.WordMeaningDetails -> {
                    _meaningDetails.value = reply.meaningDetails
                    _state.value = ScreenState.Render(DetailsState.Ready)
                }
                is RepoReply.UnknownMeaning -> {
                    _state.value = ScreenState.Render(DetailsState.UnknownMeaning)
                }
                is RepoReply.Error -> {
                    _state.value = ScreenState.Render(DetailsState.ShowError(reply.error))
                }
                else -> Log.e(TAG, "Error: unknown reply: $reply")
            }
        }
    }


}
