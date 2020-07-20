/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fatal_error.view.*
import online.vapcom.skyword.R
import online.vapcom.skyword.data.ErrorDescription
import online.vapcom.skyword.data.UIErrno
import online.vapcom.skyword.ui.dialogs.CommonErrorDialog
import online.vapcom.skyword.ui.dialogs.NetworkErrorDialog

private const val TAG = "BaseFragm."


/**
 * Базовый класс фрагментов, обеспечивает основную обработку ошибок
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var root: View

    /**
     * В этой функции фрагмент может прятать progress и другие виджеты,
     * должен показать fatal_error.
     * Вызывается до отображения фатальной ошибки.
     */
    abstract fun onShowFatalError()

    /**
     * Отображает нефатальную ошибку диалогом или дополнительными виджетами,
     * вызывается, если BaseFragment не смог обработать ошибку (не знает таких кодов)
     * @return true - если ошибка была здесь обработана,
     *         false - если нет
     */
    abstract fun showCommonError(error: ErrorDescription) : Boolean

    /**
     * Обработчик ошибок, прилетающих с нижних уровней
     */
    protected fun showError(error: ErrorDescription) {
        Log.e(TAG, "Error: $error")

        if(!error.fatal) {
            if(!showKnownError(error)) {
                // базовый класс не знает такой ошибки, отдаём потомку
                if(!showCommonError(error)) {
                    // потомок тоже не знает такой ошбики, показываем общий диалог
                    CommonErrorDialog(getString(R.string.try_again), error,
                        getString(R.string.call_support)).show(parentFragmentManager, "cmnerr")
                }
            }

            return
        }

        onShowFatalError()

        root.more.setOnClickListener {
            if( root.description.visibility != View.VISIBLE) {
                root.description.visibility = View.VISIBLE
                root.more.text = getString(R.string.hide)
            } else {
                root.description.visibility = View.GONE
                root.more.text = getString(R.string.more)
            }
        }

        root.description.text = getString(R.string.error_description, error.code, error.desc)

        // отправка email по кнопке "Сообщить об ошибке"
        root.report.setOnClickListener {
            Toast.makeText(activity, "Это тестовое задание, поэтому об ошибке никому не скажем", Toast.LENGTH_LONG).show()
            //sendErrorReport(requireContext(), root, error)
        }
    }

    /**
     * Отображает известные ошибки.
     * @return true - если ошибка обработана, false - надо отдавать на обработку потомку
     */
    private fun showKnownError(error: ErrorDescription) : Boolean {
        when(error.code) {
            UIErrno.CONNECTION_ERROR.errno -> NetworkErrorDialog(getString(R.string.connection_error), error.desc,
                getString(R.string.check_network_connection)).show(parentFragmentManager, "neterr")

            UIErrno.CLIENT_ERROR.errno -> NetworkErrorDialog(getString(R.string.client_problem), error.desc,
                getString(R.string.retry_or_update)).show(parentFragmentManager, "clterr")

            UIErrno.SERVER_ERROR.errno -> NetworkErrorDialog(getString(R.string.server_side_problem), error.desc,
                getString(R.string.our_problem)).show(parentFragmentManager, "srverr")

            UIErrno.TIMEOUT.errno -> NetworkErrorDialog(getString(R.string.no_server_response), error.desc,
                getString(R.string.try_later)).show(parentFragmentManager, "timeout")

            // не знаем, как обрабатывать ошибку здесь, отдаём потомку
            else -> return false
        }

        return true
    }

    /**
     * Обработчик нажатия железной кнопки Назад. Вызывается в MainActivity
     * @return true - если нажатие самостоятельно обработано фрагментом, false - действие по умолчанию
     */
    open fun onBackPressed() = false

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }


    /**
     * Прячет софтовую клавиатуру.
     *
     * Вариант с рекомендациями по поиску токена окна от https://stackoverflow.com/a/17789187
     */
    protected fun hideKeyboard() {
        val windowToken = view?.rootView?.windowToken
        windowToken?.let { wt ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(wt, 0)
        }
    }

}