package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter

private const val TAG = "DisplayCollection"
class DisplayCollectionFragment : Fragment() {

    val list = mutableListOf<CollectionRecyclerItem>()
    var adapter: CollectionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dummyCollection()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_display_collection,container,false )
        val recyclerView: RecyclerView = view.findViewById(R.id.collection_recycler_view)

        //on click listener for when you press a collection
        var onItemClickListener = View.OnClickListener { Log.i(TAG, "CLICK ITEM") }


        val adapter = CollectionAdapter(list,onItemClickListener)
        recyclerView.adapter = adapter
        Log.i(TAG, "IS THE VIEW HERE")
        return view
    }


    private fun dummyCollection() {
        for (i in 0..10) {
            val lol =
                CollectionRecyclerItem("ur${i}srs", "na${i}me", "time is $i", R.drawable.ic_logo)
            list.add(lol)
            Log.i(TAG, lol.toString())
        }
    }
    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }
}