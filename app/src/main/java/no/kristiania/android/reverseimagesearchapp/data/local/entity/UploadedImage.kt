package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.util.*


data class UploadedImage(
    val title: String?,
    var bitmap: Bitmap?,
    var urlOnServer: String? = null,
    var parentId: Int = 0,
    val id: Int = Random().nextInt(1000)
) : ImageItem, Parcelable {

    val photoFileName
        get() = "IMG_$id.png"

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        null,
        parcel.readString(),
        parcel.readInt(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(urlOnServer)
        parcel.writeInt(id)
        parcel.writeInt(parentId ++)
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