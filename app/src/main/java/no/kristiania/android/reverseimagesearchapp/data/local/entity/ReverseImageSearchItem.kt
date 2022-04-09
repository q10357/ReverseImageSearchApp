package no.kristiania.android.reverseimagesearchapp.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reversesearchresult")
class ReverseImageSearchItem(
    val link: String = "",
    @ColumnInfo(name = "thumbnail_link")
    val thumbnailLink: String = "",
    @ColumnInfo(name = "parent_image_id")
    val parentImageId: Long,
    //@Ignore val uri: String,
    @PrimaryKey(autoGenerate = true) val id: Long
): ImageItem {
    override fun toString(): String {
        return "Link: $link\nThumbnailLink: $thumbnailLink"
    }
}