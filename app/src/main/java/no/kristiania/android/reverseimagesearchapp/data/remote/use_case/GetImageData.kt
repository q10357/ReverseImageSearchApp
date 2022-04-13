package no.kristiania.android.reverseimagesearchapp.data.remote.use_case

import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import okhttp3.MultipartBody
import javax.inject.Inject


class GetImageData @Inject constructor (
    private val repository: ReverseImageSearchRepository
) {

    suspend operator fun invoke(body: MultipartBody.Part): Resource<String> {
        return repository.getUploadedImageUrl(body)
    }

    suspend operator fun invoke(url: String){
        return  repository.getReverseImageSearchResults(url)
    }

}