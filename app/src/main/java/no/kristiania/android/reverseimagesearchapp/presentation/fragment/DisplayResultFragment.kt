package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding

private const val PARENT_IMAGE_DATA = "parent_image_data"

@AndroidEntryPoint
class DisplayResultFragment: Fragment(R.layout.fragment_display_results) {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Here", "We here")
        binding = FragmentDisplayResultsBinding.bind(view)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = layoutManager
        Log.i("Hey girl", "Hey girl")
    }

    companion object {
        fun newInstance(image: UploadedImage?): DisplayResultFragment{
            val args = Bundle().apply {
                putParcelable(PARENT_IMAGE_DATA, image)
            }

            return DisplayResultFragment().apply {
                args
            }
        }
    }
}