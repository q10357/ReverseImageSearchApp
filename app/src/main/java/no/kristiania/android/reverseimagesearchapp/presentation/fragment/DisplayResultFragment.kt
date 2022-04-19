package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding
import no.kristiania.android.reverseimagesearchapp.presentation.service.ThumbnailDownloader
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayResultViewModel
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.SharedViewModel

private const val PARENT_IMAGE_DATA = "parent_image_data"
private const val TAG = "DisplayResultImages"

@AndroidEntryPoint
class DisplayResultFragment : Fragment(R.layout.fragment_display_results) {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    private val viewModel by viewModels<DisplayResultViewModel>()

    //We have scoped this viewmodel to be shared across our activity,
    //Hence by activityViewModels
    private val mainViewModel by activityViewModels<SharedViewModel>()
    private var parentImage: UploadedImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentImage = arguments?.getParcelable(PARENT_IMAGE_DATA) as UploadedImage?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayResultsBinding.bind(view)

        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler, mainViewModel.mService) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }

        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)

        photoRecyclerView = binding.rvList.also {
            it.apply {
                layoutManager = GridLayoutManager(context, 3)
            }
        }

        mainViewModel.resultItems.observe(
            viewLifecycleOwner, {
                photoRecyclerView.adapter = PhotoAdapter(it)
            }
        )
    }

    companion object {
        fun newInstance(image: UploadedImage?): DisplayResultFragment {
            val args = Bundle().apply {
                putParcelable(PARENT_IMAGE_DATA, image)
                Log.i(TAG, "${image?.urlOnServer}")
            }
            return DisplayResultFragment().apply {
                arguments = args
            }
        }
    }

    private inner class PhotoHolder(private val itemImageButton: ImageButton) :
        RecyclerView.ViewHolder(itemImageButton) {
        val bindDrawable: (Drawable) -> Unit = itemImageButton::setImageDrawable
    }

    private inner class PhotoAdapter(private val galleryItems: List<ReverseImageSearchItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(
                R.layout.list_results_gallery,
                parent,
                false
            ) as ImageButton
            view.setOnClickListener {
                val highlight = ResourcesCompat.getDrawable(resources, R.drawable.highlight, null)
                view.background = highlight
            }
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_file_download,
            ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.link)
        }

        override fun getItemCount(): Int = galleryItems.size
    }

    override fun onDestroy() {
        super.onDestroy()
        parentImage = null
    }

//    private fun startService(){
//        val serviceIntent = Intent(requireActivity(), ResultImageService::class.java)
//        requireActivity().startService(serviceIntent)
//        bindService()
//    }
//
//    private fun bindService(){
//        val serviceIntent = Intent(requireActivity(), ResultImageService::class.java)
//        requireActivity().bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
//    }
}