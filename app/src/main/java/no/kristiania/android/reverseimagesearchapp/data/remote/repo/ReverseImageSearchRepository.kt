package no.kristiania.android.reverseimagesearchapp.data.remote.repo

import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import okhttp3.MultipartBody

interface ReverseImageSearchRepository {

    suspend fun getUploadedImageUrl(body: MultipartBody.Part): Resource<String>
}