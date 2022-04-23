package no.kristiania.android.reverseimagesearchapp.core.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Log
import kotlin.math.roundToInt

private const val TAG = "ImageUtils"
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
