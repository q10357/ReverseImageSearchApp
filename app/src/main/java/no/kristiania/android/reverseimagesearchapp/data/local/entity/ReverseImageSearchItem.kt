package no.kristiania.android.reverseimagesearchapp.data.local.entity
import android.graphics.Bitmap

data class ReverseImageSearchItem(
    val link: String = "",
    val thumbnailLink: String = "",
    var bitmap: Bitmap? = null,
    val parentImageId: Long = 0L
): ImageItem {
    override fun toString(): String {
        return "Link: $link\nThumbnailLink: $thumbnailLink"
    }
}