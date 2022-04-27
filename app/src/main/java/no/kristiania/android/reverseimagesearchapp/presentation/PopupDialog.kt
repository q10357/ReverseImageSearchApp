package no.kristiania.android.reverseimagesearchapp.presentation

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import no.kristiania.android.reverseimagesearchapp.R

class PopupDialog(
    private val type: DialogType
): DialogFragment() {
    //We use this interface to deliever action events to clients
    private lateinit var listener: DialogListener

    interface DialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            //builder.setView(inflater.inflate(R.layout.tryagain_popup, null)) if we want to custom
            builder.setMessage(type.title)
                .setPositiveButton(type.posText) { dialog, id ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.cancel) { dialog, id ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogListener
        }catch(e: ClassCastException){
            throw ClassCastException()
        }
    }
}

enum class DialogType(val title: String, val posText: String) {
    ERROR("Something Happened", "Try Again"),
    INSERT("A title for your collection", "Save"),
    DELETE("Are you sure you want to delete?", "Yes")
}