package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter

private const val TAG = "DisplayCollection"
class DisplayCollectionFragment : Fragment() {

    val list = mutableListOf<CollectionRecyclerItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dummyCollection()
        val adapter = CollectionAdapter(list)
        val recycler = view?.findViewById<RecyclerView>(R.id.collection_recycler_view)
        recycler?.adapter = adapter
        recycler?.layoutManager = LinearLayoutManager(context)


        Log.i(TAG, "ARE WE HERE YET?")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        return inflater.inflate(
            R.layout.fragment_display_collection,
            container,
            false
        )


    }


    private fun dummyCollection() {
        for (i in 0..10) {
            val lol =
                CollectionRecyclerItem("ur${i}srs", "na${i}me", "time is $i", R.drawable.ic_logo)
            list.add(lol)
        }
    }
    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }
}