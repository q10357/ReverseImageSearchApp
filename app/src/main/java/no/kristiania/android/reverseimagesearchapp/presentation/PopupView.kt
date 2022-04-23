package no.kristiania.android.reverseimagesearchapp.presentation

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import no.kristiania.android.reverseimagesearchapp.R

object PopupView {
    fun showDialogueWindow(
        type: DialogType,
        message: String,
        f: () -> Unit,
        context: Context,
        inflater: LayoutInflater,
    ) {
        val builder = AlertDialog.Builder(context)
        val popupLayout = inflater.inflate(type.layoutId, null)

        with(builder) {
            setTitle(message)
            setPositiveButton("Try again") { dialog, _ ->
                f()
            }
            setNegativeButton("Cancel") { dialog, _ ->

            }
            setView(popupLayout)
            show()
        }

    }
}

enum class DialogType(val layoutId: Int) {
    ERROR(R.layout.tryagain_popup),
    INSERT(R.layout.save_collection_popup),
    LONGCLICK(R.layout.image_popout)
}