package no.kristiania.android.reverseimagesearchapp.data.local.entity
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class ReverseImageSearchItem(
    val link: String = "",
    val thumbnailLink: String = "",
    var bitmap: Bitmap? = null,
    val parentImageId: Long = 0L,
    var chosenByUser: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readLong(),
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(link)
        parcel.writeString(thumbnailLink)
        parcel.writeParcelable(bitmap, flags)
        parcel.writeLong(parentImageId)
        parcel.writeByte(if (chosenByUser) 1 else 0)
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