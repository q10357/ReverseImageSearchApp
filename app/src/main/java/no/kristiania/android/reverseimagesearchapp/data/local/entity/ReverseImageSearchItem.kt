package no.kristiania.android.reverseimagesearchapp.data.local.entity
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class ReverseImageSearchItem(
    val link: String = "",
    val thumbnailLink: String = "",
    var parentImageId: Long = 0L,
    var chosenByUser: Boolean = false,
    var bitmap: Bitmap? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun toString(): String {
        return "Link: $link\nThumbnailLink: $thumbnailLink"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(link)
        parcel.writeString(thumbnailLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReverseImageSearchItem> {
        override fun createFromParcel(parcel: Parcel): ReverseImageSearchItem {
            return ReverseImageSearchItem(parcel)
        }

        override fun newArray(size: Int): Array<ReverseImageSearchItem?> {
            return arrayOfNulls(size)
        }
    }
}