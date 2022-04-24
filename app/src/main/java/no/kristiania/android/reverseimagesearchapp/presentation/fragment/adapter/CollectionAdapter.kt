package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.databinding.CollectionItemBinding
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem

class CollectionAdapter(
    var collection: List<CollectionItem>,
    private val clickListener: OnClickCollectionListener
) : RecyclerView.Adapter<CardViewHolder>() {

    //size of the arraylist of tiems
    override fun getItemCount(): Int {
        return collection.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindCollection(collection[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val fromLayout = LayoutInflater.from(parent.context)
        val binding = CollectionItemBinding.inflate(fromLayout,parent,false)
        return CardViewHolder(binding,clickListener)
    }
}