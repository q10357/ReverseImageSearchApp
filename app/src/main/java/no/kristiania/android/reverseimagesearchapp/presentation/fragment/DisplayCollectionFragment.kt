package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.DialogType
import no.kristiania.android.reverseimagesearchapp.presentation.PopupDialog
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
    private var positionDeletion: Int = 0
    private var callbacks: Callbacks? = null

    //Callbacks to main, so that it can launch DisplayCollectionItemFragment when
    //A collection is clicked
    interface Callbacks {
        fun onCollectionSelected(parentId: Long)
        fun onCollectionDelete(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initCollection()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayCollectionBinding.bind(view)
        observeCollectionItems()
    }

    //We observe the collectionItems retrieved from the database in the ViewModel
    private fun observeCollectionItems() {
        viewModel.collection.observe(
            viewLifecycleOwner
        ) {
            collection = it
            binding.collectionRecyclerView.apply {
                layoutManager = GridLayoutManager(context,1)
                adapter = CollectionAdapter(it, this@DisplayCollectionFragment)
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
        callbacks = null
    }

    //OnAttach is called when the fragment is attached to it,s hosting activity,
    //The activity provides us the context, and we let the callback = context as Callbacks
    //Then we will be able to notify context (Activity) via callbacks
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    private fun emptyRecycler(){
        binding.collectionRecyclerView.adapter = null
    }

    fun deleteCollectionItem(position: Int){
        Log.i(TAG, "We are here now...")
        val id = collection[position].parentImage.id
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                viewModel.apply {
                    deleteCollectionItem(id)
                    initCollection()
                }
            }
            observeCollectionItems()
        }
    }

    override fun onClickCollection(position: Int) {
        callbacks?.onCollectionSelected(collection[position].parentImage.id)
    }

    //OnLongClicked will trigger a popup view that asks if you want to delete the collection
    //The popup is launched in Main, if users says yes, MainActivity calls the DeleteCollection method
    override fun onLongClickCollection(position: Int) {
        callbacks?.onCollectionDelete(position)
    }
}