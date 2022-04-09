package no.kristiania.android.reverseimagesearchapp.data.local

import androidx.room.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.local.relations.UploadedImageWithReverseImageSearchResults
import java.util.*

private const val TABLE_NAME = "uploadedimage"

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(imageEntity: UploadedImage)

    @Insert
    suspend fun insertChildImage(child: ReverseImageSearchItem)

    @Query("SELECT * FROM $TABLE_NAME WHERE id=(:id)")
    fun getImage(id: Long): UploadedImage

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getImages(): List<UploadedImage>

    @Query("SELECT COUNT(*) FROM $TABLE_NAME")
    fun getDataCount(): Int

    @Transaction
    @Query("SELECT * FROM $TABLE_NAME WHERE id=(:id)")
    suspend fun getParentImageWithChildren(id: UUID): List<UploadedImageWithReverseImageSearchResults>


}