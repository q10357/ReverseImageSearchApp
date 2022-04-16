package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class UploadedImage(
    val title: String?,
    var bitmap: Bitmap,
    //val date: Date = Calendar.getInstance().time,
    var urlOnServer: String? = null,
    val id: UUID? = UUID.randomUUID()
) : ImageItem, Parcelable {

    val photoFileName
        get() = "IMG_$id.png"

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Bitmap::class.java.classLoader)!!,
        parcel.readString(),
        null
    ) {
    }

    fun Parcel.readDate(): Date? {
        val long = readLong()
        return if (long != - 1L) Date(long) else null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeParcelable(bitmap, flags)
        parcel.writeString(urlOnServer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UploadedImage> {
        override fun createFromParcel(parcel: Parcel): UploadedImage {
            return UploadedImage(parcel)
        }

        override fun newArray(size: Int): Array<UploadedImage?> {
            return arrayOfNulls(size)
        }
    }
}