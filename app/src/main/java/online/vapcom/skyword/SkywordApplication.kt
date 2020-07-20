/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword

import android.app.Application
import online.vapcom.skyword.data.DictRepository

//private const val TAG = "SkywordApp"

/**
 * Главный класс приложения, нужен для создания ServiceLocator
 */
class SkywordApplication: Application() {

    val dictRepository: DictRepository
        get() = ServiceLocator.provideDictRepository()

}
