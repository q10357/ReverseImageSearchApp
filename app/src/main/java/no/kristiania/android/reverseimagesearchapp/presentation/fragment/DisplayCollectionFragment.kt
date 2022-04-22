package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerViewAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayCollectionViewModel

private const val TAG = "DisplayCollection"

@AndroidEntryPoint
class DisplayCollectionFragment : Fragment(), OnClickCollectionListener {

    private val viewModel by viewModels<DisplayCollectionViewModel>()
    val list = mutableListOf<CollectionItem>()
    private lateinit var binding: FragmentDisplayCollectionBinding
    private lateinit var adapter:
            GenericRecyclerViewAdapter<CollectionItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel

        //insertDummyDataToCollectionList()
        binding = FragmentDisplayCollectionBinding.inflate(layoutInflater)
        val collectionFragment = this
        binding.collectionRecyclerView.apply {
            layoutManager = GridLayoutManager(context,1)
            adapter = CollectionAdapter(list, collectionFragment)
            Log.i(TAG,"HALLO ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //todo IS THIS WHY NO WORK

        return binding.root
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