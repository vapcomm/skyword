/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_network_error.view.*
import online.vapcom.skyword.R

/**
 * Диалог сетевой ошибки
 */
open class NetworkErrorDialog(private val description: String) : DialogFragment() {

    var message = ""
    private var whatToDo = ""

    constructor(firstMessage: String, desc: String, whatToDoMessage: String) : this(desc) {
        message = firstMessage
        whatToDo = whatToDoMessage
    }

    private var isDescriptionShown = false

    private lateinit var root: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            root = it.layoutInflater.inflate(R.layout.dialog_network_error, null)
            builder.setView(root)
            builder.setPositiveButton(getString(R.string.close)) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()

        if(message.isNotBlank()) {
            root.error_message.text = message
        } else {
            root.error_message.visibility = View.GONE
        }

        if(description.isNotBlank()) {

            root.description.text = description
            root.description.visibility = View.GONE

            // добавляем иконку V справа
            root.error_message.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0)

            root.error_message.setOnClickListener {
                if(isDescriptionShown) {
                    root.description.visibility = View.GONE

                    (it as TextView).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more, 0)
                    isDescriptionShown = false
                } else {
                    root.description.visibility = View.VISIBLE

                    (it as TextView).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less, 0)
                    isDescriptionShown = true
                }
            }
        } else root.description.visibility = View.GONE

        if(whatToDo.isNotBlank()) {
            root.what_to_do.text = whatToDo
        } else root.what_to_do.visibility = View.GONE
    }
}
