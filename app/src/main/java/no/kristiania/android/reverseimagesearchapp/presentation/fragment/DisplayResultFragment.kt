package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayResultViewModel

private const val PARENT_IMAGE_DATA = "parent_image_data"
private const val TAG = "DisplayResultImages"

@AndroidEntryPoint
class DisplayResultFragment: Fragment(R.layout.fragment_display_results) {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding
    private val viewModel by viewModels<DisplayResultViewModel>()
    private var parentImage: UploadedImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentImage = arguments?.getParcelable(PARENT_IMAGE_DATA) as UploadedImage?
        Log.i(TAG, "${parentImage?.urlOnServer}")
        parentImage?.let { viewModel.fetchSearchResults(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayResultsBinding.bind(view)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = layoutManager
        binding.displayResultsImageView.setImageBitmap(parentImage?.bitmap)

    }

    companion object {
        fun newInstance(image: UploadedImage?): DisplayResultFragment{
            val args = Bundle().apply {
                putParcelable(PARENT_IMAGE_DATA, image)
                Log.i(TAG, "${image?.urlOnServer}")
            }

            return DisplayResultFragment().apply {
                arguments = args
            }
        }
    }
}