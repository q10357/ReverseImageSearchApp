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
    //the collection view model makes a call for each item with and id
    //the viewModel then launches a coroutine to get the item which is stored in mutableLivedata
    //we use this to to save resources and use livedata. the livedata will keep all the data even though the
    //fragment will be destroyed
    fun loadCollectionItem(id: Long){
        parentImageId.value = id
        viewModelScope.launch {
            collectionMutableLiveData.value = getCollectionItem(id)
        }
    }
    //creates collection items for mutablelivedata by calling the database
    //data was Date( ..) before, but changed it to string in the last minute when we had some
    //trouble with the conversion, but it should work the same
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
    //calls to the data access object which makes an extra layer beetween the database
    private fun getParentImage(id: Long): ParentImage {
        return dao.getParentImage(id)
    }

    private fun getChildImages(parentId: Long): List<ChildImage> {
        return dao.getParentsChildImages(parentId)
    }
}