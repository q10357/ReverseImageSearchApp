package no.kristiania.android.reverseimagesearchapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage

data class UploadedImageWithReverseImageSearchResults(
    @Embedded val parentImage: UploadedImage,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent_image_id"
    )
    val searchResults: List<ReverseImageSearchItem>

)