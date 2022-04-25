package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentUploadImageBinding
import no.kristiania.android.reverseimagesearchapp.presentation.DialogType
import no.kristiania.android.reverseimagesearchapp.presentation.PopupDialog
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.observer.RegisterActivityResultsObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image) {
    private lateinit var observer: RegisterActivityResultsObserver
    private lateinit var selectedImage: UploadedImage

    //UI components
    private lateinit var binding: FragmentUploadImageBinding
    private lateinit var imageView: CropImageView
    private lateinit var bitmap: Bitmap
    private var callbacks: Callbacks? = null
    private var bitmapScaling = 2
    private var scaleFactor = 1

    interface Callbacks {
        fun onImageSelected(image: UploadedImage)
        fun onUploadError(data: Resource<String>)
    }

    private val viewModel by viewModels<UploadImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = RegisterActivityResultsObserver(
            requireActivity().activityResultRegistry,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadImageBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(observer)
        Log.i(TAG, "We are in view created")
        //To give a cleaner look
        imageView = binding.cropImageView

        viewModel.mProgress.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    binding.progressBar.progress = it
                }
            }
        )

        viewModel.mResult.observe(
            this,
            {
                when(it.status){
                    Status.SUCCESS -> {
                        selectedImage.urlOnServer = it.data
                        callbacks?.onImageSelected(selectedImage)
                    }
                    Status.ERROR -> {
                        //If the code is 413, we know the image is too large,
                        //If this is the case, we will scale the bitmap, and increase the scalingFactor,
                        //If the image still is too large, it will be scaled down until "infinity"
                        if(isCode13(it.data)){
                            bitmap = getScaledBitmap(bitmap, bitmapScaling * scaleFactor)
                            scaleFactor++
                            writeToFile()
                        }
                        callbacks?.onUploadError(it)
                    }
                    Status.LOADING -> Log.i(TAG, "Loading...")
                }
            }
        )

        if (isInit { bitmap }) {
            imageView.setImageBitmap(bitmap)
            updateButtonFunctionality(true)
        } else {
            //to avoid NullPointerExceptions
            updateButtonFunctionality(false)
        }

        //This btn is used for instantiating upload to server
        binding.uploadImageBtn.setOnClickListener{
            if (!isInit { selectedImage }) {
                Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
            } else {
                //setting bitmap for selected image to the cropped uri
                cropImage()
                Log.i(TAG, "Wait for it...")
                upload()
            }
        }

        binding.cropImageView.setOnClickListener {
            observer.selectImage()
        }

        binding.selectImageBtn.setOnClickListener {
            observer.selectImage()

        }

        //simple button to rotate the cropView left
        binding.rotateLeftBtn.setOnClickListener {
            binding.cropImageView.rotateImage(270)
        }

        //simple button to rotate cropView to the right
        binding.rotateRightBtn.setOnClickListener {
            imageView.rotateImage(90)
        }

    }

    fun upload() {
        Log.i(TAG, "We are here...")
        binding.uploadImageBtn.isEnabled = false
        val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
        viewModel.onUpload(selectedImage, file)
    }

    //function to change the bitmap variable in the Uploaded Image Object
    //to the bitmap of the cropped imageview
    private fun cropImage() {
        val croppedImage = imageView.croppedImage
        bitmap = croppedImage
        imageView.setImageBitmap(croppedImage)

        writeToFile()
    }

    private fun writeToFile() {
        val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
        createFileFromBitmap(bitmap, file)
    }

    private fun initSelectedPhoto(uri: Uri) {
        bitmap = uriToBitmap(requireContext(), uri)

        selectedImage = UploadedImage(
            title = "default"
        )
        writeToFile()

        imageView.setImageBitmap(bitmap)
    }

    private fun updateButtonFunctionality(isEnabled: Boolean) {
        binding.rotateLeftBtn.isEnabled = isEnabled
        binding.rotateRightBtn.isEnabled = isEnabled
        binding.uploadImageBtn.isEnabled = isEnabled
    }

    override fun onStart() {
        super.onStart()
        //We used an observer to choose image from gallery
        //When onStarted() is called, we will observe the value of the observer's
        //Uri property, if it is not null, the user has chosen an image, and we will update the UI
        observer.uri.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    Log.i(TAG, "WE ARE OBSERVING URI, IT IS NOT NULL")
                    initSelectedPhoto(it)
                    updateButtonFunctionality(true)
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
        viewLifecycleOwner.lifecycle.removeObserver(observer)
    }

    companion object {
        fun newInstance() = UploadImageFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(isInit { selectedImage }){
            outState.putParcelable("selected_image_uri", observer.uri.value)
        }
    }

    private fun isCode13(data: String?): Boolean {
        data ?: return false
        val code: Int
        try {
            code = data.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        if (code == 413) {
            Log.i(TAG, "Photo to big, resize instantiated")
            return true
        }
        return false
    }
}