package no.kristiania.android.reverseimagesearchapp.data.remote.repo

import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call

interface ReverseImageSearchRepository {

    suspend fun getUploadedImageUrl(body: MultipartBody.Part): String

    suspend fun getReverseImageSearchResults(url: String): List<ResultImageDto>

    suspend fun fetchBytes(url: String): Call<ResponseBody>
}