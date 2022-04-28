package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentUploadImageBinding
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.observer.RegisterActivityResultsObserver
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.UploadImageViewModel
import java.io.File


private const val TAG = "MainActivityTAG"

private const val ARG_SELECTED_IMAGE_URI = "selected_image_uri"
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

        lifecycleScope.launchWhenStarted {
            observer.uri.observe(
                viewLifecycleOwner,
                {
                    it?.let {
                        Log.i(TAG, "Well this is it not null ${it}")
                        initSelectedPhoto(it)
                        updateButtonFunctionality(true)
                    }
                }
            )
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadImageBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(observer)
        Log.i(TAG, "We are in view created")
        //To give a cleaner look
        imageView = binding.cropImageView
        val uri: Uri? = savedInstanceState?.getParcelable(ARG_SELECTED_IMAGE_URI)

        viewLifecycleOwner.lifecycleScope.launch {
            if (uri != null) {
                initSelectedPhoto(uri)
            }
        }

        //Used to display progressBar
        viewModel.mProgress.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    binding.progressBar.progress = it
                }
            }
        )


        //When we kick of the upload, we observe the response
        //We use callbacks to inform MainAcivity about what is happening
        //If the request returns successfull, we launch ResultActivity
        //(callbacks?.onImageSelected)
        //If returns error, we use callbacks.?onUploadError to launch the popupwindow
        //Asking user if it want's to try again
        viewModel.mResult.observe(
            this,
            {
                when(it.status){
                    Status.SUCCESS -> {
                        selectedImage.urlOnServer = it.data
                        callbacks?.onImageSelected(selectedImage)
                    }
                    Status.ERROR -> {
                        Log.i(TAG, "This is code ${it.data}")
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
                startAnimation()
                upload()
            }
        }

        binding.cropImageView.setOnClickListener {
            selectImage()
        }

        binding.selectImageBtn.setOnClickListener {
            selectImage()
        }

        //simple button to rotate the cropView left
        binding.rotateLeftBtn.setOnClickListener {
            imageView.rotateImage(270)
        }

        //simple button to rotate cropView to the right
        binding.rotateRightBtn.setOnClickListener {
            imageView.rotateImage(90)
        }
    }

    private fun selectImage(){
        //We don't want to open the fileSystem in main thread
        lifecycleScope.launch(Dispatchers.Default) {
            observer.selectImage()
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "We are sinking....")
        super.onDestroy()
    }

    fun upload() {
        binding.uploadImageBtn.isEnabled = false
        lifecycleScope.launch(IO){
            val file = async {writeToFile()}
            withContext(Main){
                viewModel.onUpload(file.await())
            }
        }
    }

    //function to change the bitmap variable in the Uploaded Image Object
    //to the bitmap of the cropped imageview
    private fun cropImage() {
        val croppedImage = imageView.croppedImage
        bitmap = croppedImage
        imageView.setImageBitmap(croppedImage)
    }

    private fun writeToFile(): File {
        val f = File(requireActivity().cacheDir, selectedImage.photoFileName)
        return createFileFromBitmap(bitmap, f)
    }

    //Sub Req 7, writing to file in coroutine
    //We await the result, and set the imageview to the returned bitmap
    //We do this to not overload our main thread with
    //Operations doing read/write operations
    private fun initSelectedPhoto(uri: Uri) {
       lifecycleScope.launch(IO) {
            val bmp = async{uriToBitmap(requireContext(), uri)}
            withContext(Main){
                bmp.await().also {
                    imageView.setImageBitmap(it)
                    bitmap = it
                }
            }
        }
        selectedImage = UploadedImage(
            title = "default"
        )
    }

    val isEnabled: (List<View>, Boolean) -> Unit = { v: List<View>, b: Boolean ->
        v.forEach { it.isEnabled = b }
    }

    private fun updateButtonFunctionality(isEnabled: Boolean) {
        isEnabled(listOf(
            binding.rotateLeftBtn,
            binding.rotateRightBtn,
            binding.uploadImageBtn
        ), isEnabled)
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

        observer.uri.value ?: return
        Log.i(TAG, "We are saving it... ${observer.uri.value}")
        outState.putParcelable(ARG_SELECTED_IMAGE_URI, observer.uri.value)

    }

    private fun isCode13(data: String?): Boolean {
        //This is targeting sub req 1, if data is null, then return
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

    private fun startAnimation(){
        //hides the image so that we can see the progressbar when loading
        val animation = AlphaAnimation(0.1f,1.0f)
        animation.fillAfter = false
        animation.duration = 13000
        imageView.startAnimation(animation)


    }
}