package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem

class CollectionAdapter(
    var collection: List<CollectionRecyclerItem>
): RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
       // holder.itemView.
    }

    override fun getItemCount(): Int {
        return collection.size
    }
}