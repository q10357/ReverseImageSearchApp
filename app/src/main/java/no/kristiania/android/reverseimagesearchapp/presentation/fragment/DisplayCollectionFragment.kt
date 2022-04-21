package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem
private const val TAG = "DisplayCollection"
class DisplayCollectionFragment : Fragment() {

    val list = mutableListOf<CollectionRecyclerItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dummyCollection()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_display_collection_recycler_view,
            container,
            false
        )
    }


    fun dummyCollection() {
        for (i in 0..10) {
            val lol =
                CollectionRecyclerItem("ur${i}srs", "na${i}me", "time is $i", R.drawable.ic_logo)
            list.add(lol)
        }
    }
}