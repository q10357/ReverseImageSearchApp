package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class GetUploadedImageUrl @Inject constructor(
    private val repository: ReverseImageSearchRepository
) {

    operator fun invoke(body: MultipartBody.Part): Flow<Resource<String>> = flow {
        emit(Resource.loading())
        var url = ""

        try {
            url = repository.getUploadedImageUrl(body)
            emit(Resource.success(data = url))

        } catch (e: HttpException) {
            e.printStackTrace()
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