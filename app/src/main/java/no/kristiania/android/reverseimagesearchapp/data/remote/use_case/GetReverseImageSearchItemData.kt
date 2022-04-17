package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.toReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

class GetReverseImageSearchItemData @Inject constructor(
    private val repository: ReverseImageSearchRepository
) {
    operator fun invoke(url: String): Flow<Resource<List<ReverseImageSearchItem>>> = flow {
        try {
            emit(Resource.loading())
            val imageSearchItemsRAW = repository.getReverseImageSearchResults(url)
            val imageSearchItems = repository.getReverseImageSearchResults(url).map {
                it.toReverseImageSearchItem()
            }
            Log.i(TAG, "This is items: ${imageSearchItemsRAW}")
            emit(Resource.success(data = imageSearchItems))

        } catch (e: HttpException) {
            e.printStackTrace()
            emit(
                Resource.error(
                    msg = "${e.code()}"
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
            emit(
                Resource.error(
                    msg = "You got dat wifi straight?"
                )
            )
        }
    }
}