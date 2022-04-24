package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectiomItemBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerBindingInterface
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerViewAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayCollectionItemViewModel

private const val ARG_PARENT_ID = "parent_id"
private const val TAG = "DisplayCollectionItem"

@AndroidEntryPoint
class DisplayCollectionItemFragment: Fragment(R.layout.fragment_display_collectiom_item),
    OnClickListener {

    private lateinit var binding: FragmentDisplayCollectiomItemBinding
    private lateinit var collectionItem: CollectionItem
    private val viewModel by viewModels<DisplayCollectionItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentId: Long = arguments?.getLong(ARG_PARENT_ID) as Long
        viewModel.loadCollectionItem(parentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayCollectiomItemBinding.bind(view)
        viewModel.collectionItemLiveData.observe(
            viewLifecycleOwner,
            {
                collectionItem = it
                binding.imageView.setImageBitmap(it.parentImage.bitmap)
                binding.rvContainer.apply {
                    layoutManager = GridLayoutManager(context, 3)
                    adapter = GenericRecyclerViewAdapter(
                        it.childImages,
                        R.layout.list_photo_gallery,
                        this@DisplayCollectionItemFragment,
                        createBindingInterface()
                    )
                }
            }
        )
    }

    private val clickListener: (Int, View) -> Unit = { x: Int, y: View -> onClick(x, y) }

    private fun createBindingInterface() =
        object : GenericRecyclerBindingInterface<ChildImage> {
            override fun bindData(instance: ChildImage, view: View)
            {
                val imageButton = view.findViewById<ImageButton>(R.id.item_recycler_view)
                imageButton.setImageBitmap(instance.bitmap)
            }
        }

    companion object{
        fun newInstance(parentImageId: Long): DisplayCollectionItemFragment {
            val args = Bundle().apply {
                putLong(ARG_PARENT_ID, parentImageId)
            }
            return DisplayCollectionItemFragment().apply {
                arguments = args
            }
        }
    }

    override fun onClick(position: Int, view: View) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(position: Int) {
        TODO("Not yet implemented")
    }
}