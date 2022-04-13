package no.kristiania.android.reverseimagesearchapp.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ReverseImageSearchItem(
    val link: String,
    val thumbnailLink: String,
    val parentImageId: Long = 0L
): ImageItem {
    override fun toString(): String {
        return "Link: $link\nThumbnailLink: $thumbnailLink"
    }
}