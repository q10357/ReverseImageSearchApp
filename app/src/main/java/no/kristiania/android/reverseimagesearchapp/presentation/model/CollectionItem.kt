package no.kristiania.android.reverseimagesearchapp.presentation.model

import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage

data class CollectionItem(
    var collectionName: String,
    var date: String,
    var parentImage: ParentImage,
    var childImages: List<ChildImage>
) {
    override fun toString(): String {
        return "CollectionRecyclerItem(collectionName=$collectionName, date='$date', image=$parentImage)"
    }
}
