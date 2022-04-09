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
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import okhttp3.MultipartBody
import java.io.File

private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class UploadImageFragment : Fragment(), ProgressRequestBody.UploadCallback{
    private var i = 0
    private lateinit var selectedImage: UploadedImage
    private lateinit var chooseImageBtn: Button
    private lateinit var captureImageBtn: Button
    private lateinit var registry: ActivityResultRegistry
    private lateinit var photoView: ImageView
    private lateinit var photoUri: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var body: MultipartBody.Part

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

        val view = inflater.inflate(R.layout.activity_photo, container, false)

        chooseImageBtn = view.findViewById(R.id.choose_image_btn)
        captureImageBtn = view.findViewById(R.id.capture_image_btn)
        photoView = view.findViewById(R.id.image_view)

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
                    photoUri.toString()
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

}
