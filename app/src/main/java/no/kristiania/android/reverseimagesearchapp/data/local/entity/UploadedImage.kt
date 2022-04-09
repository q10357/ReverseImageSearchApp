package no.kristiania.android.reverseimagesearchapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "uploadedimage")
data class UploadedImage(
    val title: String,
    val uri: String,
    //@Ignore val bitmap: Bitmap?,
    val date: Date = Calendar.getInstance().time,
    //@Ignore val imageUrlOnServer: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0L
) : ImageItem {

    val photoFileName
        get() = "IMG_$id.png"

}