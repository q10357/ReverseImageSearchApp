package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem

class CollectionAdapter(
    var collection: List<CollectionRecyclerItem>
): RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}