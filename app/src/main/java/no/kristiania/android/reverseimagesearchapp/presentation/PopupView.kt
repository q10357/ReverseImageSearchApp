package no.kristiania.android.reverseimagesearchapp.presentation

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.getSize
import no.kristiania.android.reverseimagesearchapp.core.util.scaleBitmap
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage

object PopupView {
    fun showDialogueWindow(
        type: DialogType,
        message: String,
        @Nullable f: () -> Unit,
        context: Context,
        inflater: LayoutInflater,
    ) {
        val builder = AlertDialog.Builder(context)
        val popupLayout = inflater.inflate(type.layoutId, null)
        var posBtnText = "Try Again?"

        if(type == DialogType.ERROR) true.apply { posBtnText = "Try Again?" }

        with(builder) {
            setTitle(message)
            setPositiveButton(posBtnText) { dialog, _ ->
                f()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                Log.i("PopUp", "User Cancelled")
                dialog.cancel()
            }
            setView(popupLayout)
            show()
        }
    }

    fun inflatePhoto(
        image: Bitmap?,
        context: Context,
        activity: Activity,
        layoutInflater: LayoutInflater
    ) {
        val builder = AlertDialog.Builder(context)
        val size = activity.getSize()
        val width = size.x
        val height = size.y

        val screenLayout = layoutInflater.inflate(R.layout.image_popout, null)
        val imageView = screenLayout.findViewById<ImageView>(R.id.image_id)
        val scalingFactor = width / height
        val scaledHeight = (image!!.height) / scalingFactor
        val bitmap = scaleBitmap(image, width.toFloat(), scaledHeight.toFloat())
        imageView.setImageBitmap(bitmap)
        with(builder) {
            setNeutralButton("done") { dialog, which -> }
        }
            .setView(screenLayout)
            .show()
    }

//    private fun requestTitleToCollection(popupLayout: View, image: UploadedImage) {
//        val editText = popupLayout.findViewById<EditText>(R.id.new_collection_name)
//        val text = editText.text.toString()
//        Log.i("PopUp", "This is text $text")
//        image.title = text
//    }
}

enum class DialogType(val layoutId: Int) {
    ERROR(R.layout.tryagain_popup),
}