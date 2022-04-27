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
import kotlinx.coroutines.Dispatchers.IO
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
    PopupDialog.DialogListener, DisplayResultFragment.Callbacks {
    private var mBinder: ResultImageService.LocalBinder? = null
    private var mService: ResultImageService? = null
    private var mBound = false
    private lateinit var image: UploadedImage
    private var counter = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(TAG, "Connected to the service")
            val binder = service as ResultImageService.LocalBinder
            mBound = true
            mService = binder.getService()
            mBinder = binder
            serviceFetchData()
            mService!!.mResult.observe(
                this@ResultActivity
            ) {
                when (it.status) {
                    Status.ERROR -> onError()
                    Status.SUCCESS -> initResultFragment(image)
                    Status.LOADING -> Log.i(TAG, "Loading...")
                }
            }
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
        image = intent.getParcelableExtra<UploadedImage>(ARG_UPLOADED_IMAGE) ?: return

        supportFragmentManager
            .beginTransaction()
            .add(PopupDialog(DialogType.ERROR), "errorDialog")

    }

    private fun initResultFragment(image: UploadedImage) {
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

    private fun serviceFetchData() {
        val service = mService ?: return
        if (!service.resultItems.value.isNullOrEmpty()) return
        service.onStart(image.urlOnServer)
    }

    private fun onError() {
        Log.i(TAG, "This is happening this many times $counter")
        counter++
        val errorPopup = PopupDialog(DialogType.ERROR)
        errorPopup.show(supportFragmentManager, "ErrorDialog")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "Binding to service...")
        bindService()
    }

    private fun bindService() {
        bindService(Intent(
            this, ResultImageService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "Unbinding from service...")
        unbindService(connection)
    }


    override fun onDialogPositiveClick(dialog: DialogFragment) {
        Log.i(TAG, "User pressed try again")
        serviceFetchData()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Log.i(TAG, "User cancelled, returning to main")
        onDestroy()
    }

    override fun onDestroy() {
        quit()
        super.onDestroy()
    }

    //When child images are saved, this function gets called
    override fun onSave() {
        quit()
    }

    //We clean up the task, and start our main activity
    private fun quit(){
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }
}