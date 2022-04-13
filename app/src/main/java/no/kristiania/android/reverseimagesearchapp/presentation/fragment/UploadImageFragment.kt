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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.observer.UploadImageObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image){
    private lateinit var observer: UploadImageObserver
    private lateinit var chooseImageBtn: Button
    private var bitmap: Bitmap? = null
    private var selectedImage: UploadedImage? = null
    private lateinit var captureImageBtn: Button
    private lateinit var photoView: ImageView
    private lateinit var cropFragmentBtn : Button

    //ViewModels need to be instantiated after onAttach()
    //So we do not inject them in the constructor, but place them as a property.
    //ViewModels persist across configuration changes (such as rotation)
    //They are cleared when the activity/fragment is destroyed,
    //Until then, this property will remain the same instance
    private val viewModel by viewModels<UploadImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = UploadImageObserver(requireActivity().activityResultRegistry, requireContext())
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
        photoView = view.findViewById(R.id.image_view)
        //cropFragmentBtn = view.findViewById(R.id.crop_image_button)

        //Make coroutines do this - > captureImageBtn.isEnabled = wasInit { selectedImage }
        captureImageBtn.apply {
            setOnClickListener {
                if ( selectedImage == null ) {
                    Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "Wait for it...")
                    val file = File(requireActivity().cacheDir, selectedImage!!.photoFileName)
                    viewModel.onUpload(selectedImage!!, file)
                }
            }
        }

        chooseImageBtn.apply {
            setOnClickListener {
                observer.selectImage()
            }
        }
        /*
        cropFragmentBtn.setOnClickListener{
            val cropFragment = CropFragment()
            val transaction: FragmentTransaction = FragmentManager.beginTransaction() ?:
            transaction.replace(R.id.fragment_container, cropFragment)
        }*/

        return view
    }

    private fun initSelectedPhoto(bitmap: Bitmap) {
        Log.i(TAG, "WHY IS THIS TAKING SO LONG")

        val image = UploadedImage(
            "first_one",
            bitmap
        )

        val file = File(requireActivity().cacheDir, image.photoFileName)
        createFileFromBitmap(image.bitmap, file)

        photoView.setImageBitmap(image.bitmap)
        selectedImage = image
    }

    //We used lifecycleobserver to choose image
    //When onStarted() is initialized, we will observe the value of
    //Bitmap property, if it is not null, we will update the UI
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

    companion object {
        fun newInstance() = UploadImageFragment()
    }

    /*
    fun switchCropFragment() {
        //fragmentManager1 = supportFragmentManager
        fragmentManager1.beginTransaction()
            .replace(R.id.fragment_container,CropFragment(),"CropFragment").commit()
    }
*/


}
