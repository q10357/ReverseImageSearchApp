package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.edmodo.cropper.CropImageView
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.getBitmap
import no.kristiania.android.reverseimagesearchapp.core.util.uriToBitmap

class CropFragment : Fragment() {

    private lateinit var cropImageView: CropImageView
    private lateinit var btnCrop: Button
    private lateinit var croppedImageResult: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_crop, container, false)




        cropImageView = view.findViewById(R.id.CropImageView)
        croppedImageResult = view.findViewById(R.id.croppedImageView)
        btnCrop = view.findViewById(R.id.btnCrop)

        //Setting the cropping view to get a image from storage
        cropImageView.setOnClickListener{
            val intent = Intent()

            //chose to set intent to only get content off image
            //todo add png
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startForResult.launch(intent)
        }

        //Setting cropping button to crop
        btnCrop.setOnClickListener{

            val croppedImage = cropImageView.croppedImage
            croppedImageResult.setImageBitmap(croppedImage)

        }

        return view
    }

    private var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->


        if(result.resultCode == Activity.RESULT_OK) {
            val resultString = result.data?.data.toString()

            Log.i("result",resultString)
            val image: Bitmap =
                getBitmap(requireContext(), null, resultString, ::uriToBitmap)
            //cropped image under
            cropImageView.setImageBitmap(image)
        }
    }

    companion object {
        fun newInstance() = CropFragment()
    }
}