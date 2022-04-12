package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

data class UploadedImage(
    val title: String,
    val uri: String,
    val bitmap: Bitmap,
    val date: Date = Calendar.getInstance().time,
    val id: UUID = UUID.randomUUID()
) : ImageItem {

    val photoFileName
        get() = "IMG_$id.png"

}