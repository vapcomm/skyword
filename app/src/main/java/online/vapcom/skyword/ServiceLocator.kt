/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.runBlocking
import online.vapcom.skyword.data.DictRepository
import online.vapcom.skyword.data.DictRepositoryImpl

/**
 * Синглетон для хранения сервисов в приложении
 */
object ServiceLocator {

    @Volatile
    var dictRepository: DictRepository? = null
        @VisibleForTesting set

    /**
     * Возвращает репозиторий словаря
     */
    fun provideDictRepository(): DictRepository {
        synchronized(this) {
            return dictRepository ?:
                DictRepositoryImpl().also { dictRepository = it }
        }
    }

    private val lock = Any()    // для блокировки сброса

    /**
     * Сброс всех ресурсов, только для тестов
     */
    @VisibleForTesting
    fun reset() {
        synchronized(lock) {
            runBlocking {
                // здесь чистить DAO и кэши
            }

            dictRepository = null
        }
    }
}