package no.kristiania.android.reverseimagesearchapp.core.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun getScaledBitmap(bitmap: Bitmap): Bitmap {
    // Read in the dimensions of the image on disk
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    val scaledWidth = bitmap.width / 5
    val scaledHeight = bitmap.height / 5

    return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false )
}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = cursor.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

fun createFileFromBitmap(bitmap: Bitmap, file: File){
    val outStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream)
    val bitmapData = outStream.toByteArray()
    file.writeBytes(bitmapData)
}

//Global function to check if something is initialized
inline fun wasInit(f: () -> Unit): Boolean {
    try {
        f()
    } catch (e: UninitializedPropertyAccessException) {
        return false
    }
    return true
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap {
    var bitmap = if (Build.VERSION.SDK_INT < 28) {
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

//fun getMultiPartBody(file: File): MultipartBody.Part {
//    val builder: MultipartBody.Builder = MultipartBody.Builder()
//    builder.setType(MultipartBody.FORM)
//
//    val requestFile: RequestBody = RequestBody.create(
//        MediaType.parse(
//            "multipart/form-data"
//        ), file
//    )
//
//    return MultipartBody.Part.createFormData(
//        "image", file.name, requestFile
//    )
//}

fun File.writeBytes(array: ByteArray): Unit = FileOutputStream(this).use {it.write(array)}
