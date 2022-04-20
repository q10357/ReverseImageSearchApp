package no.kristiania.android.reverseimagesearchapp.data.local.entity

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

data class CollectionRecyclerItem(
    var urlOnServer: String? = null,
    var collectionName: String? = null,
    var date: String,
    var image: Int
)
