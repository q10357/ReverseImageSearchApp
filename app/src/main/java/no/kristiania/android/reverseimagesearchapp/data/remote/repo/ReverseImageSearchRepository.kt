package no.kristiania.android.reverseimagesearchapp.data.remote.repo

import kotlinx.coroutines.flow.Flow
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface ReverseImageSearchRepository {

    suspend fun getUploadedImageUrl(body: MultipartBody.Part): String

    suspend fun getReverseImageSearchResults(url: String): List<ResultImageDto>

    fun fetchBytes(url: String): Response<ResponseBody>
}