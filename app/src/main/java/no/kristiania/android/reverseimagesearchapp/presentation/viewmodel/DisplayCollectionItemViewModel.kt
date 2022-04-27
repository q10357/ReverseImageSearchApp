package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import javax.inject.Inject

@HiltViewModel
class DisplayCollectionItemViewModel @Inject constructor(
    private val dao: ImageDao
): ViewModel() {

    private val parentImageId = MutableLiveData<Long>()
    private var collectionMutableLiveData = MutableLiveData<CollectionItem>()
    var collectionItemLiveData = collectionMutableLiveData

    fun loadCollectionItem(id: Long){
        parentImageId.value = id
        viewModelScope.launch {
            collectionMutableLiveData.value = getCollectionItem(id)
        }
    }

    private fun getCollectionItem(parentId: Long): CollectionItem {
        val parent = getParentImage(parentId)
        val children = getChildImages(parentId)
         return CollectionItem(
            collectionName = parent.title,
            date = parent.dateAfter,
            parentImage = parent,
            childImages = children
        )
    }

    private fun getParentImage(id: Long): ParentImage {
        return dao.getParentImage(id)
    }

    private fun getChildImages(parentId: Long): List<ChildImage> {
        return dao.getParentsChildImages(parentId)
    }
}