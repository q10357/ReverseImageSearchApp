package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionItem
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter

private const val TAG = "DisplayCollection"
class DisplayCollectionFragment : Fragment(), OnClickCollectionListener {

    val list = mutableListOf<CollectionItem>()
    private lateinit var binding: FragmentDisplayCollectionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        insertDummyDataToCollectionList()
        binding = FragmentDisplayCollectionBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        val collectionFragment = this
        binding.collectionRecyclerView.apply {
            layoutManager = GridLayoutManager(context,3)
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
        binding.root
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    private fun insertDummyDataToCollectionList() {
        for (i in 0..10) {
            val lol =
                CollectionItem("ur${i}srs", "na${i}me", "time is $i", R.drawable.ic_logo)
            list.add(lol)
            Log.i(TAG, lol.toString())
        }
    }
    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }

    override fun onClickCollection(collectionItem: CollectionItem) {
        Log.i(TAG, "you pressed an item")
    }
}