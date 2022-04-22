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
            val list = getParentImages()
            Log.i(TAG, "THIS IS IT $list")
        }
    }

    fun initCollection() {
        viewModelScope.launch {
            var counter = 0
            var collection: MutableList<CollectionItem> = mutableListOf()
            val parentImages = async {
                getParentImages().forEach {
                    collection[counter].parentImage = it
                    collection[counter].date = it.date
                }
            }
            val getCollection = async { parentImages.await().forEach{
                getChildImages(it.id)
            } }
            val childImages = async { getChildImages()}
        }
    }

    private fun getParentImages(): List<ParentImage> {
        return dao.getAllParentImages()
    }

    private fun getChildImages(id: Long): List<ChildImage> {
        return dao.getParentsChildImages(id)
    }
}