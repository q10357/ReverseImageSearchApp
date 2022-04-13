package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.edmodo.cropper.CropImageView
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.observer.RegisterActivityResultsObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image){
    private lateinit var observer: RegisterActivityResultsObserver
    private lateinit var chooseImageBtn: Button
    private lateinit var selectedImage: UploadedImage
    private lateinit var captureImageBtn: Button
    private lateinit var cropImageView: CropImageView



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

        chooseImageBtn = view.findViewById(R.id.choose_image_btn)
        captureImageBtn = view.findViewById(R.id.capture_image_btn)
        cropImageView = view.findViewById(R.id.image_view)


        //This btn is used for instantiating upload to server
        captureImageBtn.apply {
            setOnClickListener {
                if ( !wasInit { selectedImage } ) {
                    Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
                } else {

                    //setting bitmap for selected image to the cropped uri
                     cropImage(selectedImage)
                    Log.i(TAG, "Wait for it...")
                    val file = File(requireActivity().cacheDir, selectedImage.photoFileName)
                    viewModel.onUpload(selectedImage, file)
                }
            }
        }
        cropImageView.setOnClickListener {

                val intent = Intent()
                //chose to set intent to only png
                intent.type = "image/png"
                intent.action = Intent.ACTION_GET_CONTENT

                startForResult.launch(intent)

        }

        chooseImageBtn.apply {
            setOnClickListener {
                observer.selectImage()
            }
        }

        return view
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
                }
            }
        )
        Log.i(TAG, "Now in start")
    }

    private var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->


        if(result.resultCode == Activity.RESULT_OK) {
            val resultString = result.data?.data.toString()

            Log.i(TAG,resultString)
            val image: Bitmap =
                getBitmap(requireContext(), null, resultString, ::uriToBitmap)
            //cropped image under results of crop
            initSelectedPhoto(image)
            cropImageView.setImageBitmap(image)

        }
    }

    private fun cropImage(selectedImage: UploadedImage) {
        val croppedImage = cropImageView.croppedImage
        selectedImage.bitmap = croppedImage

    }

    companion object {
        fun newInstance() = UploadImageFragment()
    }




}
