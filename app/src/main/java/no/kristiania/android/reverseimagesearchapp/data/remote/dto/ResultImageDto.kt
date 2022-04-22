package no.kristiania.android.reverseimagesearchapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem

data class ResultImageDto(
    //The decision of having this data transfer object was mainly based on
    //what we felt gave a more readable code
    //We store all the data here, even the not necessary data,
    //We will later make these objects into the objects we use for our database
    @SerializedName("current_date")
    val currentDate: String,
    val description: String,
    val domain: String,
    val identifier: String,
    @SerializedName("image_link")
    val imageLink: String,
    val name: String,
    @SerializedName("store_link")
    val storeLink: String,
    @SerializedName("thumbnail_link")
    val thumbnailLink: String,
    @SerializedName("tracking_id")
    val trackingId: String
)

fun ResultImageDto.toReverseImageSearchItem(): ReverseImageSearchItem {
    return ReverseImageSearchItem(
        link = imageLink,
        thumbnailLink = thumbnailLink
    )
}
