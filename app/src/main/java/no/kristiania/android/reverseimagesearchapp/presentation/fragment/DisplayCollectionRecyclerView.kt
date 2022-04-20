package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.CollectionRecyclerItem
import java.time.LocalDate
import java.time.LocalDateTime


class DisplayCollectionRecyclerView : Fragment() {

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


    fun dummyCollection(){
        for(i in 0..10){
            val lol = CollectionRecyclerItem("ur${i}srs", "na${i}me","time is $i",R.drawable.ic_logo)
            list.add(lol)
        }
    }

    /*
    data class CollectionRecyclerItem(
    var urlOnServer: String? = null,
    var collectionName: String? = null,
    var date: LocalDate,
    var bitmap: Bitmap? = null
)
     */
}