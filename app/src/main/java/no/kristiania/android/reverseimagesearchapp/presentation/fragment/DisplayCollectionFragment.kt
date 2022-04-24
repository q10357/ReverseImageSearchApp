package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.CollectionAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener.OnClickCollectionListener
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayCollectionViewModel

private const val TAG = "DisplayCollection"
private const val ARG_PARENT_ID = "parent_id"


@AndroidEntryPoint
class DisplayCollectionFragment : Fragment(R.layout.fragment_display_collection),
    OnClickCollectionListener {
    private val viewModel by viewModels<DisplayCollectionViewModel>()
    private lateinit var binding: FragmentDisplayCollectionBinding
    private lateinit var collection: List<CollectionItem>
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCollectionSelected(parentId: Long)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayCollectionBinding.bind(view)
        val collectionFragment = this

        viewModel.collection.observe(
            viewLifecycleOwner
        ) {
            collection = it
            binding.collectionRecyclerView.apply {
                layoutManager = GridLayoutManager(context,1)
                adapter = CollectionAdapter(it, collectionFragment)
            }
        }
    }
//
//    private fun createBindingInterface() =
//        object : GenericRecyclerBindingInterface<CollectionItem> {
//            override fun bindData(
//                instance: CollectionItem,
//                view: View,
//            ) {
//                view.findViewById<TextView>(R.id.recycler_text).apply {
//                    this.text = instance.collectionName
//                }
//
//                val imageView = view.findViewById<ImageView>(R.id.recycler_image)
//                imageView.setImageBitmap(instance.parentImage.bitmap)
//            }
//        }

    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }

    //OnDetach is called when the fragment is removed from it's hosting activity
    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "We are detached")
        callbacks = null
    }

    //OnAttach is called when the fragment is attached to it,s hosting activity,
    //The activity provides us the context, and we let the callback = context as Callbacks
    //Then we will be able to notify context (Activity) via callbacks
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "We are attached")
        callbacks = context as Callbacks?
    }

    override fun onClickCollection(position: Int) {
        Log.i(TAG, "This is callback value ${callbacks}")
        callbacks?.onCollectionSelected(collection[position].parentImage.id)
    }
}