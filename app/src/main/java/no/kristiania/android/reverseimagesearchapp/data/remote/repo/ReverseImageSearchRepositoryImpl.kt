package no.kristiania.android.reverseimagesearchapp.data.remote.repo

import android.util.Log
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.local.sqlLite.ImageRepositoryDao
import no.kristiania.android.reverseimagesearchapp.data.remote.api.ReverseImageSearchApi
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "MainActivityTAG"

class ReverseImageSearchRepositoryImpl @Inject constructor(
    private val api: ReverseImageSearchApi,
) : ReverseImageSearchRepository {

    override suspend fun getUploadedImageUrl(body: MultipartBody.Part): Resource<String> {
        var response = Resource.loading(data = "")
        var url = ""
        Log.i("HEYHEY", "n here now")

        try {
            url = api.uploadImage(
                body,
                RequestBody.create(MediaType.parse("multipart/form-data"), "Image from device")
            )
        }catch (e: HttpException){
            e.printStackTrace()
            response = Resource.error(
                msg = "Oopsie... something went wrong, try again?",
                data = "${e.code()}"
            )
        }catch (e: IOException){
            e.printStackTrace()
            response =
                Resource.error(
                    msg = "You got dat wifi straight?"
                )
        }

        if (url.isNotBlank())
            response = (Resource.success(data = url))

        return response
    }

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