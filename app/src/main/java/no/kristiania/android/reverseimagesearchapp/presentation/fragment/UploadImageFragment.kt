package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import okhttp3.MultipartBody
import java.io.File
import androidx.fragment.app.FragmentManager as FragmentManager1

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(R.layout.fragment_upload_image), ProgressRequestBody.UploadCallback{
    private lateinit var selectedImage: UploadedImage
    private lateinit var chooseImageBtn: Button
    private lateinit var captureImageBtn: Button
    private lateinit var photoView: ImageView
    private lateinit var photoUri: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var body: MultipartBody.Part
    private lateinit var cropFragmentBtn : Button



    //ViewModels need to be instantiated after onAttach()
    //So we do not inject them in the constructor, but place them as a property.
    //ViewModels persist across configuration changes (such as rotation)
    //They are cleared when the activity/fragment is destroyed,
    //Until then, this property will remain the same instance
    private val viewModel by viewModels<UploadImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        registry = requireActivity().activityResultRegistry

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
                if (!wasInit { body }) {
                    Toast.makeText(this.context, "Select Image First", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "Wait for it...")
                    viewModel.onUpload(body)
                }
            }
        }

        chooseImageBtn.apply {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            setOnClickListener {
                startForResult.launch(i)

                //val path = getRealPathFromString(internalImageUri)
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

    private var startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                photoUri = result.data?.data!!
                bitmap = uriToBitmap(requireContext(), photoUri)
                photoView.setImageBitmap(bitmap)

                selectedImage = UploadedImage(
                    "first_one",
                    photoUri.toString(),
                    bitmap
                )

                uploadImage()
            }
        }

    private fun uploadImage() {
        //Creating new file to write the compressed bitmap, png formatted file
        val file = File(requireActivity().filesDir, selectedImage.photoFileName)
        val scaledBitmap = getScaledBitmap(bitmap)
        createFileFromBitmap(scaledBitmap, file)
        photoView.setImageBitmap(scaledBitmap)
        Log.i(TAG, "This is size of newfile: ${file.length()}")

        body = getMultiPartBody(file, this)

    }

    companion object {
        fun newInstance() = UploadImageFragment()
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.i(TAG, "This is percentage $percentage")
    }

    override fun onError() {
        Log.e(TAG, "Error in upload")
    }

    override fun onFinish() {
        Log.i(TAG, "Upload finish")
    }


    /*
    fun switchCropFragment() {
        //fragmentManager1 = supportFragmentManager
        fragmentManager1.beginTransaction()
            .replace(R.id.fragment_container,CropFragment(),"CropFragment").commit()
    }
*/


}
