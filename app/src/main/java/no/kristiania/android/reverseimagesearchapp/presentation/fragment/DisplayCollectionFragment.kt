package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayCollectionViewModel

private const val TAG = "DisplayCollection"

@AndroidEntryPoint
class DisplayCollectionFragment : Fragment(R.layout.fragment_display_collection), OnClickCollectionListener {

    private val viewModel by viewModels<DisplayCollectionViewModel>()
    var list = mutableListOf<CollectionItem>()
    private lateinit var binding: FragmentDisplayCollectionBinding
//    private lateinit var adapter:
//            GenericRecyclerViewAdapter<CollectionItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        Log.i(TAG, "JESUS CHRIST")

        //insertDummyDataToCollectionList()
        binding = FragmentDisplayCollectionBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "HERE WE ARE")
        binding = FragmentDisplayCollectionBinding.bind(view)

        val collectionFragment = this
        viewModel.collection.observe(
            viewLifecycleOwner
        ){
            Log.i(TAG, "ARE WE HERE???")
            binding.collectionRecyclerView.apply {
                layoutManager = GridLayoutManager(context,1)
                Log.i(TAG, "list size in viewModel: ${it.size}")
                adapter = CollectionAdapter(it, collectionFragment)
                Log.i(TAG,"HALLO ")
            }
        }
    }

//    private fun insertDummyDataToCollectionList() {
//        for (i in 0..10) {
//            val lol =
//                CollectionItem("na${i}me", "time is $i", R.drawable.ic_collection)
//            list.add(lol)
//            Log.i(TAG, lol.toString())
//        }
//    }
    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }

    override fun onClickCollection(collectionItem: CollectionItem) {
        Log.i(TAG, "you pressed an item")
    }
}