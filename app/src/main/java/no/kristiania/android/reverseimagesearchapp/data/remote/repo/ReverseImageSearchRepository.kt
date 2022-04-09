package no.kristiania.android.reverseimagesearchapp.data.remote.repo

import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import okhttp3.MultipartBody

interface ReverseImageSearchRepository {

    suspend fun getUploadedImageUrl(image: MultipartBody.Part): Resource<String>
}