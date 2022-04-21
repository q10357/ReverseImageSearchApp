package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.Status
import no.kristiania.android.reverseimagesearchapp.core.util.createFileFromBitmap
import no.kristiania.android.reverseimagesearchapp.core.util.uriToBitmap
import no.kristiania.android.reverseimagesearchapp.core.util.wasInit
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.observer.RegisterActivityResultsObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image) {
    private lateinit var observer: RegisterActivityResultsObserver
    private lateinit var selectedImage: UploadedImage
    private lateinit var mProgressBar: ProgressBar

    //UI components
    private lateinit var selectImageBtn: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var cropImageView: CropImageView
    private lateinit var rotateRightBtn: Button
    private lateinit var rotateLeftBtn: Button
    private lateinit var bitmap: Bitmap

    private var callbacks: Callbacks? = null


    interface Callbacks {
        fun onImageSelected(image: UploadedImage)
    }

    //ViewModels need to be instantiated after onAttach()
    //So we do not inject them in the constructor, but place them as a property.
    //ViewModels persist across configuration changes (such as rotation)
    //They are cleared when the activity/fragment is destroyed,
    //Until then, this property will remain the same instance
    private val viewModel by viewModels<UploadImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = RegisterActivityResultsObserver(
            requireActivity().activityResultRegistry,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(observer)

        //We used an observer to choose image from gallery
        //When onStarted() is called, we will observe the value of the observer's
        //Uri property, if it is not null, the user has chosen an image, and we will update the UI
        observer.uri.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    initSelectedPhoto(it)
                    updateButtonFunctionality(true)
                }
            }
        )

        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)

        selectImageBtn = view.findViewById(R.id.select_image_btn)
        uploadImageBtn = view.findViewById(R.id.upload_image_btn)
        rotateLeftBtn = view.findViewById(R.id.rotate_left_button)
        rotateRightBtn = view.findViewById(R.id.rotate_right_button)
        cropImageView = view.findViewById(R.id.image_view)
        mProgressBar = view.findViewById(R.id.progress_bar_circular)

        if (wasInit { bitmap }) {
            cropImageView.setImageBitmap(bitmap)
            updateButtonFunctionality(true)
        } else {
            //to avoid NullPointerExceptions
            updateButtonFunctionality(false)
        }

        //This btn is used for instantiating upload to server
        uploadImageBtn.apply {
            setOnClickListener {
                if (!wasInit { selectedImage }) {
                    Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
                } else {
                    //setting bitmap for selected image to the cropped uri
                    cropImage()
                    Log.i(TAG, "Wait for it...")
                    val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
                    viewModel.onUpload(selectedImage, file)
                    observeUpload()
                    observeResponse()
                }
            }
        }

        cropImageView.apply {
            setOnClickListener {
                observer.selectImage()
            }
        }

        selectImageBtn.apply {
            setOnClickListener {
                observer.selectImage()
            }
        }

        //simple button to rotate the cropview left
        rotateLeftBtn.setOnClickListener {
            cropImageView.rotateImage(270)
        }

        //simple button to rotate cropview to the right
        rotateRightBtn.setOnClickListener {
            cropImageView.rotateImage(90)
        }

        return view
    }

    //function to change the bitmap variable in the Uploaded Image Object
    //to the bitmap of the cropped imageview
    private fun cropImage() {
        val croppedImage = cropImageView.croppedImage
        bitmap = croppedImage
        cropImageView.setImageBitmap(croppedImage)

        writeToFile()
    }

    private fun writeToFile() {
        val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
        //We get the rightly scaled bitmap here
        createFileFromBitmap(bitmap, file)
    }

    private fun initSelectedPhoto(uri: Uri) {
        bitmap = uriToBitmap(requireContext(), uri)

        selectedImage = UploadedImage(
            title = "default"
        )
        writeToFile()

        cropImageView.setImageBitmap(bitmap)
    }

    private fun updateButtonFunctionality(isEnabled: Boolean) {
        rotateLeftBtn.isEnabled = isEnabled
        rotateRightBtn.isEnabled = isEnabled
        uploadImageBtn.isEnabled = isEnabled
    }

    private fun observeUpload() {
        viewModel.mProgress.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    mProgressBar.progress = it
                }
            }
        )
    }

    private fun observeResponse() {
        viewModel.mResult.observe(
            viewLifecycleOwner,
            {
                when(it.status){
                    Status.SUCCESS -> {
                        selectedImage.urlOnServer = it.data
                        callbacks?.onImageSelected(selectedImage)
                    }
                    Status.ERROR -> {
                        val message = it.message

                    }
                }
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "WE ARE DESTROYING IT")
        viewLifecycleOwner.lifecycle.removeObserver(observer)
    }

    companion object {
        fun newInstance() = UploadImageFragment()
    }
}
