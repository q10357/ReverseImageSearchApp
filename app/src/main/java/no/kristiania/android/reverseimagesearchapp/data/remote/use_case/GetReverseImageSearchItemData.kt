package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.toReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

class GetReverseImageSearchItemData @Inject constructor(
    private val repository: ReverseImageSearchRepository
) {
    suspend operator fun invoke(url: String): Resource<List<ReverseImageSearchItem>> {
        Log.i(TAG, "WE ARE HERE")
        try {
            val imageSearchItems = repository.getReverseImageSearchResults(url).map {
                it.toReverseImageSearchItem()
            }
            Log.i(TAG, "This is items: imageSearchItems")
            return Resource.success(data = imageSearchItems)

        } catch (e: HttpException) {
            e.printStackTrace()
            return Resource.error(
                message = "Somehing went wrong...\nTry again?"
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return Resource.error(
                message = "Connection Error"

            )
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun fetchPhoto(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val response: Response<ResponseBody> = repository.fetchBytes(url).execute()
            bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
            Log.i(
                TAG, "Decoded bitmap=$bitmap from Response=$response\n" +
                        "ResponseBody=${response.body()}"
            )
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }
}