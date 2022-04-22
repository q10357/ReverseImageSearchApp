package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectionBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerBindingInterface
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerViewAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayCollectionViewModel

private const val TAG = "DisplayCollection"

@AndroidEntryPoint
class DisplayCollectionFragment : Fragment(R.layout.fragment_display_collection), OnClickListener {

    private val viewModel by viewModels<DisplayCollectionViewModel>()
    private lateinit var binding: FragmentDisplayCollectionBinding

    var list = mutableListOf<CollectionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel

        binding = FragmentDisplayCollectionBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayCollectionBinding.bind(view)

        val collectionFragment = this
        viewModel.collection.observe(
            viewLifecycleOwner
        ) {
            binding.collectionRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 1)
                adapter = GenericRecyclerViewAdapter(
                    it,
                    R.layout.list_collection,
                    clickListener,
                    createBindingInterface()
                )
            }
        }
    }

    private val clickListener: (Int, View) -> Unit = { x: Int, y: View -> onClick(x, y) }

    private fun createBindingInterface() =
        object : GenericRecyclerBindingInterface<CollectionItem> {
            override fun bindData(
                instance: CollectionItem,
                view: View,
            ) {
                view.findViewById<TextView>(R.id.recycler_text).apply {
                    this.text = instance.collectionName
                }
                val imageView = view.findViewById<ImageView>(R.id.recycler_image)
                imageView.setImageBitmap(instance.parentImage.bitmap)
            }
        }

    companion object {
        fun newInstance() = DisplayCollectionFragment()
    }

    override fun onClick(position: Int, view: View) {
        Log.i(TAG, "WE ARE PRESSING!")
    }
}