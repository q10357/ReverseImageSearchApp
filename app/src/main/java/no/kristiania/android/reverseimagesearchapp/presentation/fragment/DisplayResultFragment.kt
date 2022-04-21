package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.wasInit
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding
import no.kristiania.android.reverseimagesearchapp.presentation.OnPhotoListener
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import no.kristiania.android.reverseimagesearchapp.presentation.service.ThumbnailDownloader
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayResultViewModel
import java.io.File


private const val PARENT_IMAGE_DATA = "parent_image_data"
private const val TAG = "DisplayResultImages"

@AndroidEntryPoint
class DisplayResultFragment : Fragment(R.layout.fragment_display_results), OnPhotoListener {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    private var mBound = false
    private var mService: ResultImageService? = null
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
            ThumbnailDownloader(Handler(Looper.getMainLooper()), mService)
        { holder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            if (counter < resultItems.size) {
                resultItems[counter].bitmap = bitmap
                holder.setBitmap(drawable)
                counter++
            }
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)

        viewModel
        viewModel.getBinder().observe(this, Observer {
            if (it != null) {
                mService = it.getService()
                serviceInit()
            } else {
                mService = null
            }
        })
    }

    private fun serviceInit() {
        thumbnailDownloader.service = mService

            mService!!.resultItems.observe(
            viewLifecycleOwner
        ) {
            Log.i(TAG, "This is list size ${it.size}")
            for (i in it)
                i?.let { it1 -> resultItems.add(it1) }
            photoRecyclerView.adapter = PhotoAdapter(resultItems, this)
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
                showPopupForSaving()
            }
        }

        photoRecyclerView = binding.rvList.also {
            it.apply {
                layoutManager = GridLayoutManager(context, 3)
            }
        }
    }

    private fun addCollectionToDb() {
        val chosenImages = resultItems.filter { it.chosenByUser }
        viewModel

        CoroutineScope(IO).launch {
            val parentId = withContext(IO) {
                viewModel.saveParentImage(parentImage!!)
            }
            withContext(IO) {
                chosenImages.forEach { it.parentImageId = parentId }
                for (i in chosenImages) {
                    viewModel.saveChildImage(i,collectionName)

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

    class PhotoHolder(
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

    private inner class PhotoAdapter(
        private val resultItems: List<ReverseImageSearchItem>,
        private val onPhotoListener: OnPhotoListener,
    ) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(
                R.layout.list_results_gallery,
                parent,
                false
            ) as ImageButton
            return PhotoHolder(view, onPhotoListener)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = resultItems[position]
            val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_file_download,
            ) ?: ColorDrawable()
            holder.setBitmap(placeholder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.link)
        }

        override fun getItemCount(): Int = resultItems.size
    }

    override fun onPhotoClick(position: Int, view: View) {
        Log.i(TAG, "Photo clicked, check if add or remove")
//        val highlight = ResourcesCompat.getDrawable(resources, R.drawable.highlight, null)
//        view.background = highlight

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



     private fun showPopupForSaving(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val popupLayout = inflater.inflate(R.layout.save_collection_popup,null)
        val editText = popupLayout.findViewById<EditText>(R.id.new_collection_name)
        val list = arrayListOf<String>()



        //make a popup which the user names collection of the parent image
        with(builder){
            setTitle("Name your collection")
            setPositiveButton("OK") { dialog, which ->
                //list.add(editText.text.toString())
                Toast.makeText(requireContext(), editText.text.toString(), Toast.LENGTH_SHORT).show()
                //parentImage?.collectionName = editText.text.toString()
                collectionName = editText.text.toString()
            }
            setNegativeButton("cancel"){ dialog, which ->
                Toast.makeText(requireContext(), "Cancel the popout", Toast.LENGTH_SHORT).show()

            }
            setView(popupLayout)
            show()

        }
    }

    private fun treatOnClick(isChosen: Boolean): Drawable? {
        return when (isChosen) {
            true -> ResourcesCompat.getDrawable(resources, R.drawable.highlight, null)
            false -> ColorDrawable(Color.TRANSPARENT)
        }
    }

    private fun bindService() {
        val serviceIntent = Intent(this.requireActivity(), ResultImageService::class.java)
        requireActivity().bindService(serviceIntent,
            viewModel.getConnection(),
            Context.BIND_AUTO_CREATE)
        mBound = true
    }

    override fun onResume() {
        super.onResume()
        bindService()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.getBinder().value != null) {
            requireActivity().unbindService(viewModel.getConnection())
            mBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader.fragmentLifecycleObserver
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader.onDestroyView(this)
    }
}