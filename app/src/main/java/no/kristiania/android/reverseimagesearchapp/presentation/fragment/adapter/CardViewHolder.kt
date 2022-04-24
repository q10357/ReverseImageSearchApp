package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.content.DialogInterface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.databinding.CollectionItemBinding
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem

class CardViewHolder(
    private val collectionItemBinding: CollectionItemBinding,
    private val onClickListener: OnClickCollectionListener
) : RecyclerView.ViewHolder(collectionItemBinding.root), View.OnClickListener, View.OnLongClickListener {

    //function that binds the xml-file variables and replaces it with the
    //information we get from the CollectionItem data class object
    fun bindCollection(collectionItem: CollectionItem){
        collectionItemBinding.cardView.setOnClickListener(this)
        collectionItemBinding.cardView.setOnLongClickListener(this)

        collectionItemBinding.recyclerImage.setImageBitmap(collectionItem.parentImage.bitmap)
        collectionItemBinding.recyclerText.text = collectionItem.collectionName
        collectionItemBinding.recyclerDateText.text = collectionItem.date.toString()
        collectionItemBinding.recyclerCountText.text = collectionItem.childImages.size.toString()
        //setting button listener on the arrow to open up the collection
    }

    override fun onClick(p0: View?) {
        onClickListener.onClickCollection(layoutPosition)
    }

    override fun onLongClick(p0: View?): Boolean {
        onClickListener.onLongClickCollection(layoutPosition)
        return true
    }
}