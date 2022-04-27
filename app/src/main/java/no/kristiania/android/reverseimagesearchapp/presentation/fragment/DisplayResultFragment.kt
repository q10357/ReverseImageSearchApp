package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.inflatePhoto
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding
import no.kristiania.android.reverseimagesearchapp.presentation.PopupDialog
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericPhotoAdapter
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter.GenericRecyclerBindingInterface
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener.OnClickPhotoListener
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.observer.DisplayResultObserver
import no.kristiania.android.reverseimagesearchapp.presentation.service.ThumbnailDownloader
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.DisplayResultViewModel
import java.io.File


private const val PARENT_IMAGE_DATA = "parent_image_data"
private const val TAG = "DisplayResultImages"

@AndroidEntryPoint
class DisplayResultFragment : Fragment(R.layout.fragment_display_results),
    OnClickPhotoListener{

    private lateinit var binding: FragmentDisplayResultsBinding
    private lateinit var thumbnailDownloader: ThumbnailDownloader<ImageButton>
    private lateinit var observer: DisplayResultObserver<ImageButton>
    private lateinit var bitmap: Bitmap
    private var imageCount: Int = 0

    //Temporary containers before sending to db, on users request
    private var resultItems = mutableListOf<ReverseImageSearchItem>()

    private val viewModel by viewModels<DisplayResultViewModel>()
    private lateinit var parentImage: UploadedImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentImage = arguments?.getParcelable(PARENT_IMAGE_DATA) ?: return
        val file = File(requireContext().cacheDir, parentImage.photoFileName)
        Log.i(TAG, "Size of file: ${file.length()}")
        bitmap = BitmapFactory.decodeFile(file.path) ?: return

        var counter = 0
        thumbnailDownloader =
            ThumbnailDownloader(Handler(Looper.getMainLooper()), null)
            { holder, bitmap ->
                if (counter < resultItems.size) {
                    resultItems[counter].bitmap = bitmap
                    holder.setImageBitmap(bitmap)
                    counter++
                }
            }

        observer = DisplayResultObserver(
            thumbnailDownloader,
            requireActivity(),
        )

        lifecycle.addObserver(observer)

        observer._resultItems.observe(
            this
        ) {
            resultItems = it as MutableList<ReverseImageSearchItem>
            binding.rvContainer.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = GenericPhotoAdapter(
                    resultItems,
                    R.layout.list_photo_gallery,
                    this@DisplayResultFragment,
                    createBindingInterface()
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayResultsBinding.bind(view)

        val file = File(requireContext().cacheDir, parentImage.photoFileName)
        Log.i(TAG, "Size of file: ${file.length()}")
        bitmap = BitmapFactory.decodeFile(file.path)

        binding.imageView.setImageBitmap(bitmap)

        binding.buttonSave.setOnClickListener {
            if (imageCount <= 0) {
                Toast.makeText(requireContext(), "No pictures selected", Toast.LENGTH_SHORT).show()
            } else {
                //ViewModel has lazy init
                //ViewModel is an observer, and must be added in the main thread
                //So when we plan to use it in a coroutine, we have to
                //Be sure that it is initialized
                //in the main thread
                //val f: (UploadedImage) -> Unit = { i: UploadedImage -> addCollectionToDb(i)}
                viewModel
                showPopupForSaving(parentImage)
            }
        }
    }

    private fun showPopupForSaving(image: UploadedImage) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val popupLayout = inflater.inflate(R.layout.save_collection_popup, null)
        val editText = popupLayout.findViewById<EditText>(R.id.new_collection_name)

        //make a popup which the user names collection of the parent image
        with(builder) {
            setTitle("Name your collection")
            setPositiveButton("OK") { dialog, _ ->
                //list.add(editText.text.toString())
                Toast.makeText(requireContext(), editText.text.toString(), Toast.LENGTH_SHORT)
                    .show()
                //parentImage?.collectionName = editText.text.toString()
                val text = editText.text.toString().also{
                    image.title = it
                }
                addCollectionToDb()
            }
            setNegativeButton("cancel") { dialog, _ ->
                Toast.makeText(requireContext(), "Cancel the popout", Toast.LENGTH_SHORT).show()
            }
            setView(popupLayout)
            show()
        }
    }

    private fun createBindingInterface() =
        object : GenericRecyclerBindingInterface<ReverseImageSearchItem> {
            override fun bindData(
                instance: ReverseImageSearchItem,
                view: View,
            ) {
                val imageButton = view.findViewById<ImageButton>(R.id.item_recycler_view)
                thumbnailDownloader.queueThumbnail(imageButton, instance.link)
            }
        }

    private fun addCollectionToDb() {
        lifecycleScope.launch(IO) {
            val parentId = async { viewModel.saveParentImage(parentImage) }
            val chosenImages = async { resultItems.filter { it.chosenByUser } }
            chosenImages.await().forEach {
                it.parentImageId = parentId.await()
                viewModel.saveChildImage(it)
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

    private fun treatOnClick(isChosen: Boolean): Drawable? {
        return when (isChosen) {
            true -> ResourcesCompat.getDrawable(resources, R.drawable.highlight, null)
            false -> ColorDrawable(Color.TRANSPARENT)
        }
    }

    override fun onClick(position: Int, view: View) {
        Log.i(TAG, "Photo clicked, check if add or remove")
        resultItems[position].apply {
            chosenByUser = when (chosenByUser) {
                true -> false.also { imageCount-- }
                false -> true.also { imageCount++ }
            }
        }.also {
            view.background = treatOnClick(it.chosenByUser)
        }
        Log.i(TAG, "Number of images chosen: $imageCount")
    }

    override fun onLongClick(position: Int) {
        resultItems[position].bitmap?.let {
            inflatePhoto(it, requireActivity(),
                requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(observer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer.onDestroyView()
    }
}