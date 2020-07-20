/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui.states

/**
 * Базовый класс для взаимодействия ViewModel и активити
 * Взят из примера https://github.com/antoniolg/androidmvvm
 */
sealed class ScreenState<out T> {
    object Loading : ScreenState<Nothing>()
    class Render<T>(val renderState: T) : ScreenState<T>()
}