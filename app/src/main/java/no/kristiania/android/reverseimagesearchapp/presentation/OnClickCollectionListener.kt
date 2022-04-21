package no.kristiania.android.reverseimagesearchapp.presentation

import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionItem

interface OnClickCollectionListener {
    fun onClickCollection(collectionItem: CollectionItem)
}