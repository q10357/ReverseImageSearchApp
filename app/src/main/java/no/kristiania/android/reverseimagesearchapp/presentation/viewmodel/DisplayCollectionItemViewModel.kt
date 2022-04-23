package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage
import javax.inject.Inject

@HiltViewModel
class DisplayCollectionItemViewModel @Inject constructor(
    private val dao: ImageDao
): ViewModel() {

    fun loadParentImage(id: Long): ParentImage {
        return dao.getParentImage(id)
    }

    fun getChildImages(parentId: Long): List<ChildImage> {
        return dao.getParentsChildImages(parentId)
    }
}