/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import coil.api.load
import com.google.android.material.textfield.TextInputLayout
import online.vapcom.skyword.R
import kotlin.math.roundToInt


/**
 * Включет отображение текста ошибки в TextInputLayout. Пример:
 *
 * app:errorText="@{viewModel.isPassportDateError ? @string/passport_input_error_data : null}"
 *
 * Проблема в том, что error нельзя поставить из XML, поэтому приходится делать байдинг адаптер.
 * Взято здесь: https://stackoverflow.com/questions/37075871/can-i-bind-an-error-message-to-a-textinputlayout
 */
@BindingAdapter("errorText")
fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

/**
 * Адаптер для коротких списков, которые отображаются через LinearLayout и помещаются целиком на экран.
 *
 * <LinearLayout
 *     ....
 *     android:orientation="vertical"
 *     app:entries="@{viewModel.docsForSign}"
 *     app:layout="@{@layout/docs_for_sign_list_item}"
 *     app:entryClickListener="@{clickListener}"
 * />
 *
 * В лейауте, который предаётся в app:layout должны быть определены переменные:
 * data - здесь её присваивается значение элемента списка.
 * clickListener - обработчик нажатия на элемент или его внутренний виджет
 *
 * Взято из https://medium.com/androiddevelopers/android-data-binding-list-tricks-ef3d5630555e
 */
@BindingAdapter("entries", "layout", "entryClickListener")
fun <T> setEntries(viewGroup: ViewGroup, entries: List<T>?, layoutId: Int, listener: View.OnClickListener) {
    viewGroup.removeAllViews()
    // Log.e("BINDING===", "entries[${entries?.size}]: $entries")

    if (entries != null && entries.isNotEmpty()) {
        val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        entries.forEach { entry ->
            //Log.e("BINDING===", "entry: $entry")
            val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, viewGroup, true)
            binding.setVariable(BR.data, entry)
            binding.setVariable(BR.clickListener, listener)
            binding.root.tag = entry
        }
    }
}

/**
 * Упрощённый вариант без clickListener
 */
@BindingAdapter("entries", "layout")
fun <T> setEntries(viewGroup: ViewGroup, entries: List<T>?, layoutId: Int) {
    viewGroup.removeAllViews()
    //Log.e("BINDING===", "entries[${entries?.size}]: $entries")
    if (entries != null && entries.isNotEmpty()) {
        val inflater = viewGroup.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        entries.forEach { entry ->
            //Log.e("BINDING===", "entry: $entry")
            val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, viewGroup, true)
            binding.setVariable(BR.data, entry)
        }
    }
}

/**
 * Загружает изображение
 * При ошибке загрузки подставляются картинки ошибки и вопросика
 * Если URL пустой, то отображает image_load_fallback
 */
@BindingAdapter("imageURL")
fun loadImage(imageView: ImageView, url: String) {
    if(url.isNotEmpty()) {
        imageView.load(url) {
            Log.i("IMAGEURL..", ">>>>>> loadImage: URL: '$url'")
            crossfade(true)
            placeholder(R.drawable.image_load_placeholder)
            error(R.drawable.image_load_error)
            fallback(R.drawable.image_load_fallback)
        }
    } else imageView.setImageResource(R.drawable.image_load_fallback)
}


/**
 * Адаптер layout_marginBottom
 * отсюда https://stackoverflow.com/a/34835249
 */
@BindingAdapter("android:layout_marginBottom")
fun setBottomMargin(view: View, bottomMargin: Float) {
    val layoutParams = view.layoutParams as MarginLayoutParams
    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                            layoutParams.rightMargin, bottomMargin.roundToInt())
    view.layoutParams = layoutParams
}

