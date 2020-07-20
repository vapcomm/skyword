/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

open class BaseViewModel : ViewModel() {

    companion object {
        const val FRAGMENT_CHANGE_ANIMATION_DURATION = 500L   //  время анимации смены фрагментов типа slide_in, мс
    }

    private val viewModelJob = Job()        // общий job для всех корутин этой viewModel
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)   // контекст корутин

    /**
     * Завершает все корутины когда эта viewModel завершает работу
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Делает задержку работы корутины, если со startTime прошло меньше времени, чем FRAGMENT_CHANGE_ANIMATION_DURATION
     */
    protected suspend fun delayForAnimation(startTime: Long) {
        val leftTime = FRAGMENT_CHANGE_ANIMATION_DURATION - (System.currentTimeMillis() - startTime)
        if(leftTime > 10L) {
            delay(leftTime)
        }
    }

}