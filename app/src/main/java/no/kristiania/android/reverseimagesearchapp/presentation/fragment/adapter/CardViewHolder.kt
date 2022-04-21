package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionItem
import no.kristiania.android.reverseimagesearchapp.databinding.CollectionItemBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener

class CardViewHolder(
    private val collectionItemBinding: CollectionItemBinding,
    private val onClickListener: OnClickCollectionListener
) : RecyclerView.ViewHolder(collectionItemBinding.root) {

    fun bindCollection(collectionItem: CollectionItem){
        collectionItemBinding.recyclerImage.setImageResource(collectionItem.image)
        collectionItemBinding.recyclerText.text = collectionItem.collectionName
        //setting button listener on the arrow to open up the collection
        collectionItemBinding.recyclerImageButton.setOnClickListener {
            onClickListener.onClickCollection(collectionItem)
        }
    }
}