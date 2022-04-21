package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem

class CollectionAdapter(
    var collection: List<CollectionRecyclerItem>,
    val onItemClickLister: View.OnClickListener
): RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    inner class CollectionViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindCollectionItem(collectionRecyclerItem: CollectionRecyclerItem){


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {


        val collectionInformation: CollectionRecyclerItem

        holder.itemView.setTag(position)
        /*
        holder.itemView.apply {
            val image = findViewById<ImageView>(R.id.recycler_image)
            val text = findViewById<TextView>(R.id.recycler_text)
            val imageBtn = findViewById<ImageButton>(R.id.recycler_image_button)
            image.setImageResource(collection[position].image)
            text.text = collection[position].collectionName


        }
         */
    }

    override fun getItemCount(): Int {
        return collection.size
    }
}