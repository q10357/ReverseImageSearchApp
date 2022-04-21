package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionItem
import no.kristiania.android.reverseimagesearchapp.databinding.CollectionItemBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener

class CollectionAdapter(
    var collection: List<CollectionItem>,
    private val clickListener: OnClickCollectionListener
)
    : RecyclerView.Adapter<CardViewHolder>() {


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