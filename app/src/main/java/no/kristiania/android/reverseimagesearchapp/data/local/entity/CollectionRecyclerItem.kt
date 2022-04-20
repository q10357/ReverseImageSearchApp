package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import java.util.*
import kotlin.collections.ArrayList

data class CollectionRecyclerItem(
    var urlOnServer: String? = null,
    var collectionName: String? = null,
    var date: Date? = null,
    var bitmap: Bitmap? = null
)

 public fun dummyCollection(): ArrayList<CollectionRecyclerItem>{
     val list : List<CollectionRecyclerItem>
     val CollectionRecyclerItem

 }