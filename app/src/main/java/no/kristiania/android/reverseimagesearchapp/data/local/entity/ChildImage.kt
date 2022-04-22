package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap

class ChildImage(
    val id: Long,
    val bitmap: Bitmap,
    val parentId: Long
)