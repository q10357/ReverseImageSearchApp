package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import okhttp3.MultipartBody
import javax.inject.Inject


class GetUploadedImageUrl @Inject constructor (
    private val repository: ReverseImageSearchRepository
) {

    suspend operator fun invoke(image: MultipartBody.Part): Resource<String> {
        return repository.getUploadedImageUrl(image)
    }

}