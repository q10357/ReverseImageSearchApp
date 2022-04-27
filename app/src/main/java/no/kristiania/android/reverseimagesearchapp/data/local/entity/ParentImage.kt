package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import java.util.*

data class ParentImage(
    val id: Long,
    val title: String,
    val bitmap: Bitmap,
    val date: Date,
    val dateAfter: String
)