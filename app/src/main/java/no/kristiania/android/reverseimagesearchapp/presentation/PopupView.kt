package no.kristiania.android.reverseimagesearchapp.presentation

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.annotation.Nullable
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage

object PopupView {
    fun showDialogueWindow(
        type: DialogType,
        message: String,
        f: () -> Unit,
        context: Context,
        inflater: LayoutInflater,
        @Nullable image: UploadedImage?
    ) {
        val builder = AlertDialog.Builder(context)
        val popupLayout = inflater.inflate(type.layoutId, null)
        var posBtnText = "Try Again?"
        var isEditText: Boolean = false

        if(type == DialogType.INSERT) true.apply { posBtnText = "Nice" }.also { isEditText = true }

        with(builder) {
            setTitle(message)
            setPositiveButton(posBtnText) { dialog, _ ->
                if(isEditText) requestTitleToCollection(popupLayout, image!!)
                f()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                Log.i("PopUp", "User Cancelled")
            }
            setView(popupLayout)
            show()
        }
    }

    private fun requestTitleToCollection(popupLayout: View, image: UploadedImage) {
        val editText = popupLayout.findViewById<EditText>(R.id.new_collection_name)
        val text = editText.text.toString()
        Log.i("PopUp", "This is text ${text}")
        image.title = text
    }
}

enum class DialogType(val layoutId: Int) {
    ERROR(R.layout.tryagain_popup),
    INSERT(R.layout.save_collection_popup),
    LONGCLICK(R.layout.image_popout)
}