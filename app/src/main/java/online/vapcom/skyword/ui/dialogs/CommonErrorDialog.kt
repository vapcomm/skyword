/*
 * (c) VAP Communications Group, 2020
 */


package online.vapcom.skyword.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import online.vapcom.skyword.R
import online.vapcom.skyword.data.ErrorDescription

/**
 * Диалог общей нефатальной ошибки
 */
open class CommonErrorDialog(private val error: ErrorDescription) : DialogFragment() {

    var message = ""
    var whatToDo = ""

    constructor(firstMessage: String, error: ErrorDescription, whatToDoMessage: String) : this(error) {
        message = firstMessage
        whatToDo = whatToDoMessage
    }

    var isDescriptionShown = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(it.layoutInflater.inflate(R.layout.dialog_common_error, null))
            builder.setPositiveButton(getString(R.string.close)) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()

        if(message.isNotBlank()) {
            val msg = dialog?.findViewById(R.id.error_message) as TextView
            msg.text = message
        }

        if(error.code != 0 || error.desc.isNotBlank()) {

            val descr = dialog?.findViewById(R.id.description) as TextView
            descr.text = getString(R.string.error_description, error.code, error.desc)
            descr.visibility = View.GONE

            // добавляем иконку V справа
            val msg = dialog?.findViewById(R.id.error_message) as TextView
            msg.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0)

            msg.setOnClickListener {
                if(isDescriptionShown) {
                    val desc = dialog?.findViewById(R.id.description) as TextView
                    desc.visibility = View.GONE

                    (it as TextView).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0)
                    isDescriptionShown = false
                } else {
                    val desc = dialog?.findViewById(R.id.description) as TextView
                    desc.visibility = View.VISIBLE

                    (it as TextView).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0)
                    isDescriptionShown = true
                }
            }
        }

        if(whatToDo.isNotBlank()) {
            val todo = dialog?.findViewById(R.id.what_to_do) as TextView
            todo.text = whatToDo
        }

        // отправка email по кнопке "Сообщить об ошибке"
        val report = dialog?.findViewById(R.id.report) as Button
        report.setOnClickListener {
            Toast.makeText(activity, "Это тестовое задание, поэтому об ошибке никому не скажем", Toast.LENGTH_LONG).show()
            //sendErrorReport(requireContext(), root, error)
        }
    }
}
