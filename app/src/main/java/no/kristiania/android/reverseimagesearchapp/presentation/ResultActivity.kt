package no.kristiania.android.reverseimagesearchapp.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.core.util.Status
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService

private const val ARG_UPLOADED_IMAGE = "uploaded_image"
private const val TAG = "ActivityResultTAG"

@AndroidEntryPoint
class ResultActivity : AppCompatActivity(),
PopupDialog.DialogListener
{
    private var mBinder: ResultImageService.LocalBinder? = null
    private var mService: ResultImageService? = null
    private var mBound = false
    private lateinit var image: UploadedImage

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(TAG, "Connected to the service")
            val binder = service as ResultImageService.LocalBinder
            mBound = true
            mService = binder.getService()
            mBinder = binder
            observeServiceResponse()

        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.i(TAG, "Disconnected from service")
            mService = null
            mBound = false
            mBinder = null
        }
    }

    private fun observeServiceResponse() {
        if(!mService!!.resultItems.value.isNullOrEmpty()) return
        lifecycleScope.launch(Dispatchers.Main) {
            mService!!.apply{
                Log.i(TAG, "ResultItems is null or empty")
                onStart(image.urlOnServer)
                mResult.observe(
                    this@ResultActivity
                ){
                    when(it.status){
                        Status.ERROR -> onError()
                        Status.SUCCESS -> initResultFragment(image)
                    }
                }
            }
        }
    }

    private fun onError() {
        val errorPopup = PopupDialog(DialogType.ERROR)
        errorPopup.show(supportFragmentManager, "dialog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        Log.i(TAG, "Starting Activity")
        image = intent.getParcelableExtra<UploadedImage>(ARG_UPLOADED_IMAGE) ?: return

        bindService()
        startService(Intent(this, ResultImageService::class.java)
            .putExtra("image_url", image?.urlOnServer))
    }

    private fun initResultFragment(image: UploadedImage){
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

    private fun bindService(){
        bindService(Intent(
            this, ResultImageService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onPause() {
        super.onPause()
        Intent(this, ResultImageService::class.java).also {
            stopService(it)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.i(TAG, "User pressed try again")
        observeServiceResponse()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Log.i(TAG, "User cancelled, returning to main")
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}