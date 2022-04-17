package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edmodo.cropper.CropImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.observer.RegisterActivityResultsObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File
import java.lang.NullPointerException

private const val TAG = "MainActivityTAG"
private const val ARG_CHOSEN_IMAGE = "chosen_image"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image){
    private lateinit var observer: RegisterActivityResultsObserver
    private lateinit var selectedImage: UploadedImage

    //UI components
    private lateinit var selectImageBtn: Button
    private lateinit var uploadImageBtn: Button
    private lateinit var cropImageView: CropImageView
    private lateinit var rotateRightBtn: Button
    private lateinit var rotateLeftBtn: Button

    private var callbacks: Callbacks? = null
    private lateinit var cropFragmentBtn : Button


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
        observer = RegisterActivityResultsObserver(requireActivity().activityResultRegistry, requireContext())
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_upload_image, container, false)

        selectImageBtn = view.findViewById(R.id.select_image_btn)
        uploadImageBtn = view.findViewById(R.id.upload_image_btn)
        rotateLeftBtn = view.findViewById(R.id.rotate_left_button)
        rotateRightBtn = view.findViewById(R.id.rotate_right_button)
        cropImageView = view.findViewById(R.id.image_view)

        //to avoid NullPointerExceptions
        updateButtonFunctionality(false)

        //This btn is used for instantiating upload to server
        uploadImageBtn.apply {
            setOnClickListener {
                if ( !wasInit { selectedImage } ) {
                    Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
                } else {
                    //setting bitmap for selected image to the cropped uri
                    cropImage(selectedImage)
                    Log.i(TAG, "Wait for it...")
                    val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
                    viewModel.onUpload(selectedImage, file)
                    observeImageUrl()
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
            cropImageView.rotateImage(270);
        }

        //simple button to rotate cropview to the right
        rotateRightBtn.setOnClickListener {
            cropImageView.rotateImage(90);
        }

        return view
    }

    //function to change the bitmap variable in the Uploaded Image Object
    //to the bitmap of the cropped imageview
    private fun cropImage(selectedImage: UploadedImage) {
        val croppedImage = cropImageView.croppedImage
        selectedImage.bitmap = croppedImage
        cropImageView.setImageBitmap(croppedImage)
    }

    private fun initSelectedPhoto(bitmap: Bitmap) {
        selectedImage = UploadedImage(
            "first_one",
            bitmap
        )
        val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
        createFileFromBitmap(selectedImage.bitmap, file)

        cropImageView.setImageBitmap(selectedImage.bitmap)
    }

    //We used an observer to choose image from gallery
    //When onStarted() is initialized, we will observe the value of the observer's
    //Bitmap property, if it is not null, the user has chosen an image, and we will update the UI
    override fun onStart() {
        super.onStart()
        observer.bitmap.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    initSelectedPhoto(it)
                    updateButtonFunctionality(true)
                }
            }
        )
    }

    private fun updateButtonFunctionality(isEnabled: Boolean) {
        rotateLeftBtn.isEnabled = isEnabled
        rotateRightBtn.isEnabled = isEnabled
        uploadImageBtn.isEnabled = isEnabled
    }

    private fun observeImageUrl(){
        viewModel.uploadedImage.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    callbacks?.onImageSelected(it)
                    Log.i(TAG, "This is our callback ${it}")
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

    companion object {
        fun newInstance() = UploadImageFragment()
    }
}
