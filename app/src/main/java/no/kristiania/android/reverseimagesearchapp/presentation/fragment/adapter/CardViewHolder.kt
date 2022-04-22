//package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter
//
//import androidx.recyclerview.widget.RecyclerView
//import no.kristiania.android.reverseimagesearchapp.databinding.CollectionItemBinding
//import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
//
//import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener
//
//class CardViewHolder(
//    private val collectionItemBinding: CollectionItemBinding,
//    private val onClickListener: OnClickCollectionListener
//) : RecyclerView.ViewHolder(collectionItemBinding.root) {
//
//    //function that binds the xml-file variables and replaces it with the
//    //information we get from the CollectionItem data class object
//    fun bindCollection(collectionItem: CollectionItem){
//        //collectionItemBinding.recyclerImage.setImageResource(collectionItem.parentImage)
//        collectionItemBinding.recyclerText.text = collectionItem.collectionName
//        //setting button listener on the arrow to open up the collection
//        collectionItemBinding.recyclerImageButton.setOnClickListener {
//            onClickListener.onClickCollection(collectionItem)
//        }
//    }
//}