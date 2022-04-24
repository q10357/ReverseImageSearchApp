package no.kristiania.android.reverseimagesearchapp.presentation

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService

private const val ARG_UPLOADED_IMAGE = "uploaded_image"
private const val TAG = "ActivityResultTAG"

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {
    private var mBinder: ResultImageService.LocalBinder? = null
    private var mService: ResultImageService? = null
    private var mBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(TAG, "Connected to the service")
            val binder = service as ResultImageService.LocalBinder
            mBound = true
            mService = binder.getService()
            mBinder = binder
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.i(TAG, "Disconnected from service")
            mService = null
            mBound = false
            mBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        Log.i(TAG, "Starting Activity")
        val image = intent.getParcelableExtra<UploadedImage>(ARG_UPLOADED_IMAGE)

        startService(Intent(this, ResultImageService::class.java)
            .putExtra("image_url", image?.urlOnServer))

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = DisplayResultFragment.newInstance(image)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Intent(this, ResultImageService::class.java).also {
            stopService(it)
        }
    }
}