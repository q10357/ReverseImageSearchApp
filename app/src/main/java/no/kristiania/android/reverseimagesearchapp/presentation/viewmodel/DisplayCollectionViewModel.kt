package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import javax.inject.Inject

private const val TAG = "DisplayCollection"

@HiltViewModel
class DisplayCollectionViewModel @Inject constructor(
    private val dao: ImageDao,
) : ViewModel() {

    init {
        viewModelScope.launch {
            initCollection()
        }
    }

    private fun initCollection() {
        viewModelScope.launch {
            var collection: MutableList<CollectionItem> = mutableListOf()
            val parentImages = mutableListOf<ParentImage>()
            var childImages = mutableListOf<ChildImage>()
            getParentImages().forEach {
                Log.i(TAG, "WE ARE HERE !! ${it.id}")
                parentImages.add(it)
                childImages = getChildImages(it.id) as MutableList<ChildImage>
                val collectionItem = CollectionItem(
                    collectionName = it.title,
                    date = it.date,
                    parentImage = it,
                    childImages = childImages
                )
                collection.add(collectionItem)
                Log.i(TAG, "This is or list size: ${collection.size}")
            }
        }
    }

    private fun getParentImages(): List<ParentImage> {
        return dao.getAllParentImages()
    }

    private fun getChildImages(id: Long): List<ChildImage> {
        return dao.getParentsChildImages(id)
    }
}