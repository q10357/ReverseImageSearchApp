package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "UploadingImageGetUrl"
class GetUploadedImageUrl @Inject constructor(
    private val repository: ReverseImageSearchRepository
) {

    operator fun invoke(body: MultipartBody.Part): Flow<Resource<String>> = flow {
        var url = ""

        try {
            Log.i(TAG, "IN TRY BLOCK")
            url = repository.getUploadedImageUrl(body)
            emit(Resource.success(data = url))

        } catch (e: HttpException) {
            e.printStackTrace()
            Log.i(TAG, "THIS IS CODE: ${e.code()}, ${e.message()}")
            emit(
                Resource.error(
                    message = "Something went wrong...\nTry again?",
                    data = "${e.code()}"
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
            emit(
                Resource.error(
                    message = "Connection Error"
                )
            )
        }
    }
}