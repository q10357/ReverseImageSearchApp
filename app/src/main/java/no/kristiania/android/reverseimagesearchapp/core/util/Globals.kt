package no.kristiania.android.reverseimagesearchapp.core.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickListener
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun getScaledBitmap(bitmap: Bitmap, scaling: Int): Bitmap {
    // Read in the dimensions of the image on disk
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    val scaledWidth = bitmap.width / scaling
    val scaledHeight = bitmap.height / scaling

    return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false )
}

fun createFileFromBitmap(bitmap: Bitmap, file: File){
    val outStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream)
    val bitmapData = outStream.toByteArray()
    file.writeBytes(bitmapData)
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream)
    return outStream.toByteArray()
}

//Global function to check if something is initialized
inline fun isInit(f: () -> Unit): Boolean {
    try {
        f()
    } catch (e: UninitializedPropertyAccessException) {
        return false
    }
    return true
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    val bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            uri
        )
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    return bitmap!!
}

fun getMultiPartBody(file: File, uploadCallback: ProgressRequestBody.UploadCallback): MultipartBody.Part {
    val builder: MultipartBody.Builder = MultipartBody.Builder()
    builder.setType(MultipartBody.FORM)

    val fileBody = ProgressRequestBody(
        file,
        "image",
        uploadCallback
    )
    Log.i("FILEUTIL", "FIle name: ${file.name}")

    return MultipartBody.Part.createFormData(
        "image", file.name, fileBody
    )
}

fun fileToBitmap(file: File): Bitmap{
    return BitmapFactory.decodeFile(file.path)
}

fun File.writeBytes(array: ByteArray): Unit = FileOutputStream(this).use {it.write(array)}

fun getBitmap(context: Context, id: Int?, uri: String?, decoder: (Context, Int?, String?) -> Bitmap): Bitmap {
    return decoder(context, id, uri)
}

fun Activity.getSize(): Point {
    val metrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display?.getRealMetrics(metrics)
    } else {
        windowManager.defaultDisplay.getRealMetrics(metrics)
    }
    return Point(metrics.widthPixels, metrics.heightPixels)
}
