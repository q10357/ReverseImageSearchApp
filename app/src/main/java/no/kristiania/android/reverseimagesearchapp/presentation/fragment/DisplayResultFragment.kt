package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnPhotoListener
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerViewAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerBindingInterface
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.observer.DisplayResultObserver
import no.kristiania.android.reverseimagesearchapp.presentation.service.ThumbnailDownloader
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayResultViewModel
import java.io.File


private const val PARENT_IMAGE_DATA = "parent_image_data"
private const val TAG = "DisplayResultImages"

@AndroidEntryPoint
class DisplayResultFragment : Fragment(R.layout.fragment_display_results), OnPhotoListener {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding
    private lateinit var thumbnailDownloader: ThumbnailDownloader<ImageButton>
    private lateinit var observer: DisplayResultObserver<ImageButton>
    private lateinit var adapter:
            GenericRecyclerViewAdapter<ReverseImageSearchItem>
    private var bitmap: Bitmap? = null
    private var imageCount: Int = 0
    private var collectionName = ""

    //Temporary containers before sending to db, on users request
    private var resultItems = mutableListOf<ReverseImageSearchItem>()

    private val viewModel by viewModels<DisplayResultViewModel>()
    private var parentImage: UploadedImage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentImage = arguments?.getParcelable(PARENT_IMAGE_DATA) as UploadedImage?

        var counter = 0
        thumbnailDownloader =
            ThumbnailDownloader(Handler(Looper.getMainLooper()), null)
            { holder, bitmap ->
                val drawable = BitmapDrawable(resources, bitmap)
                if (counter < resultItems.size) {
                    resultItems[counter].bitmap = bitmap
                    holder.setImageBitmap(bitmap)
                    counter++
                }
            }

        observer = DisplayResultObserver(
            this.thumbnailDownloader,
            requireActivity(),
        )

        lifecycle.addObserver(observer)

        observer._resultItems.observe(
            this
        ) {
            resultItems = it as MutableList<ReverseImageSearchItem>
            Log.i(TAG, "List size: ${resultItems.size}")
            adapter = GenericRecyclerViewAdapter(it, R.layout.list_results_gallery, this, createBindingInterface())
            photoRecyclerView.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayResultsBinding.bind(view)

        //overlayImage?.findViewById<ImageView>(R.id.overlay_image)

        if (parentImage != null && bitmap == null) {
            val file = File(requireContext().cacheDir, parentImage!!.photoFileName)
            Log.i(TAG, "Size of file: ${file.length()}")
            bitmap = BitmapFactory.decodeFile(file.path)
        }

        bitmap?.let { binding.parentImageView.setImageBitmap(it) }


        binding.buttonSave.setOnClickListener {
            if (imageCount <= 0) {
                Toast.makeText(requireContext(), "No pictures selected", Toast.LENGTH_SHORT).show()
            } else {
                addCollectionToDb()
            }
        }

        photoRecyclerView = binding.rvList.also {
            it.apply {
                layoutManager = GridLayoutManager(context, 3)
            }
        }
    }

    private fun createBindingInterface() =
        object: GenericRecyclerBindingInterface<ReverseImageSearchItem>{
            override fun bindData(
                instance: ReverseImageSearchItem,
                view: View
            ) {
                val imageButton = view.findViewById<ImageButton>(R.id.item_recycler_view)
                thumbnailDownloader.queueThumbnail(imageButton, instance.link)
            }
        }

    private fun addCollectionToDb() {
        val chosenImages = resultItems.filter { it.chosenByUser }
        viewModel

        showPopupForSaving()
        CoroutineScope(IO).launch {
            val parentId = withContext(IO) {
                viewModel.saveParentImage(parentImage!!)
            }
            withContext(IO) {
                chosenImages.forEach { it.parentImageId = parentId }
                for (i in chosenImages) {
                    viewModel.saveChildImage(i)
                }
            }
        }
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

    private inner class PhotoHolder(
        private val itemImageButton: ImageButton,
        private val onPhotoListener: OnPhotoListener,
    ) :
        RecyclerView.ViewHolder(itemImageButton), View.OnClickListener {
        init {
            itemImageButton.setOnClickListener(this)
        }

        val setBitmap: (Drawable) -> Unit = itemImageButton::setImageDrawable

        override fun onClick(view: View) {
            onPhotoListener.onPhotoClick(layoutPosition, view)
        }
    }

    private fun treatOnClick(isChosen: Boolean): Drawable? {
        return when (isChosen) {
            true -> ResourcesCompat.getDrawable(resources, R.drawable.highlight, null)
            false -> ColorDrawable(Color.TRANSPARENT)
        }
    }

    private fun showPopupForSaving() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val popupLayout = inflater.inflate(R.layout.save_collection_popup, null)
        val editText = popupLayout.findViewById<EditText>(R.id.new_collection_name)
        val list = arrayListOf<String>()


        //make a popup which the user names collection of the parent image
        with(builder) {
            setTitle("Name your collection")
            setPositiveButton("OK") { dialog, which ->
                //list.add(editText.text.toString())
                Toast.makeText(requireContext(), editText.text.toString(), Toast.LENGTH_SHORT)
                    .show()
                //parentImage?.collectionName = editText.text.toString()
                editText.text.toString()
                parentImage!!.title = editText.text.toString()
            }
            setNegativeButton("cancel") { dialog, which ->
                Toast.makeText(requireContext(), "Cancel the popout", Toast.LENGTH_SHORT).show()

            }
            setView(popupLayout)
            show()

        }
    }

    override fun onPhotoClick(position: Int, view: View) {
        Log.i(TAG, "Photo clicked, check if add or remove")
        resultItems[position].apply {
            when (this.chosenByUser) {
                true -> this.chosenByUser = false.also { imageCount-- }
                false -> this.chosenByUser = true.also { imageCount++ }
            }
        }.also {
            view.background = treatOnClick(it.chosenByUser)
        }
        Log.i(TAG, "Number of images chosen: $imageCount")
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer.onDestroyView(this)
    }
}