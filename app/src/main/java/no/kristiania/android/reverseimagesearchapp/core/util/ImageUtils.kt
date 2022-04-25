package no.kristiania.android.reverseimagesearchapp.core.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import no.kristiania.android.reverseimagesearchapp.R

private const val TAG = "ImageUtils"

fun Activity.scaleBitmap(image: Bitmap): Bitmap? {
    val size = getSize()
    val width = size.x.toFloat()
    val height = size.y.toFloat()

    var scalingFactor: Float = if (height >= width) {
        (width / height)
    } else {
        (height / width)
    }

    if (image.height >= image.width) {
        scalingFactor *= image.width.toFloat() / image.height.toFloat()
    } else if (image.width >= image.height) {
        scalingFactor *= image.height.toFloat() / image.width.toFloat()
    }

    val newWidth = image.width.toFloat() / scalingFactor
    val newHeight = image.height.toFloat() / scalingFactor

    return scaleBitmap(image, newWidth, newHeight)
}

fun scaleBitmap(bitmapToScale: Bitmap?, newWidth: Float, newHeight: Float): Bitmap? {
    if (bitmapToScale == null) return null
    //get the original width and height
    val width = bitmapToScale.width
    val height = bitmapToScale.height
    // create a matrix for the manipulation
    val matrix = Matrix()

// resize the bit map
    matrix.postScale(newWidth / width, newHeight / height)

// recreate the new Bitmap and set it back
    return Bitmap.createBitmap(bitmapToScale,
        0,
        0,
        bitmapToScale.width,
        bitmapToScale.height,
        matrix,
        true)
}

fun inflatePhoto(image: Bitmap, activity: Activity, context: Context) {
    val builder = AlertDialog.Builder(context)

    val inflater = activity.layoutInflater
    val screenLayout = inflater.inflate(R.layout.image_popout, null)
    val imageView = screenLayout.findViewById<ImageView>(R.id.image_id)

    val bitmap = activity.scaleBitmap(image)
    imageView.setImageBitmap(bitmap)
    with(builder) {
        setNeutralButton("done") { dialog, which -> }
    }
        .setView(screenLayout)
        .show()
}
