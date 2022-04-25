package no.kristiania.android.reverseimagesearchapp.data.remote.repo
import android.util.Log
import no.kristiania.android.reverseimagesearchapp.core.util.inBound
import no.kristiania.android.reverseimagesearchapp.core.util.provider.Constants
import no.kristiania.android.reverseimagesearchapp.data.remote.api.ReverseImageSearchApi
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "NetworkRepo"

class ReverseImageSearchRepositoryImpl @Inject constructor(
    private val api: ReverseImageSearchApi,
) : ReverseImageSearchRepository {
    var counter = 0

    override suspend fun getUploadedImageUrl(body: MultipartBody.Part): String {
        return api.uploadImage(
                body,
                RequestBody.create(MediaType.parse("multipart/form-data"), "Image from device")
            )
    }

    override suspend fun getReverseImageSearchResults(url: String): List<ResultImageDto> {
        counter++
        if(!inBound(counter, Constants.fetchUrls.size)) counter = 0
        Log.i(TAG, "Now searching with ${Constants.fetchUrls[counter]}")
//        return api.fetchResultPhotoData(Constants.fetchUrls[counter], url)
       return api.fetchResultPhotoData(url)
    }

    override suspend fun fetchBytes(url: String): Call<ResponseBody> {
        return api.fetchUrlBytes(url)
    }

//    override suspend fun getReverseImageSearchResults(url: String): List<ResultImageDto> {
//        emit(Resource.loading())
//
//        try{
//            val response = api.fetchResultPhotoData(url)
//            Log.i(TAG, "THIS IS RESPONSE $response")
//        }catch (e: HttpException){
//            e.printStackTrace()
//            response = Resource.error(
//                msg = "Oopsie... something went wrong, try again?",
//                data = "${e.code()}"
//            )
//        }catch (e: IOException){
//            e.printStackTrace()
//            response =
//                Resource.error(
//                    msg = "You got dat wifi straight?"
//                )
//        }
//    }

//    override fun getUploadedImageUrl(image: MultipartBody.Part): Flow<Resource<String>> = flow {
//        var url = ""
//        try {
//            url = api.uploadImage(
//                image,
//                RequestBody.create(MediaType.parse("multipart/form-data"), "Image from device")
//            )
//            Log.i(TAG, "$url")
//        } catch (e: HttpException) {
//            e.printStackTrace()
//            emit(
//                Resource.error(
//                    msg = "Oopsie... something went wrong, try again?"
//                )
//            )
//        } catch (e: IOException) {
//            e.printStackTrace()
//            emit(
//                Resource.error(
//                    msg = "You got dat wifi straight?"
//                )
//            )
//        }
//
//        if (url.isNotBlank())
//            emit(Resource.success(data = url))
//    }

//
//    suspend fun uploadImage(image: MultipartBody.Part) {
//        val request: Call<String> = api.uploadImage(image)
//
//        request.enqueue(object: Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                Log.d(TAG, "Response received ${response.body()}")
//
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.e(TAG, "Error")
//            }
//        })
//    }
}