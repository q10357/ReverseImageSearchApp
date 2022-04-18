package no.kristiania.android.reverseimagesearchapp.data.remote.api

import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ReverseImageSearchApi {

    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): String

    @GET("bing")
    suspend fun fetchResultPhotoData(@Query("url") url: String): List<ResultImageDto>

    @GET
    fun fetchUrlBytes(@Url url: String): Response<ResponseBody>

}