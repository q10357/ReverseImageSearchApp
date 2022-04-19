package no.kristiania.android.reverseimagesearchapp.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService

private const val TAG = "MainActivityTAG"
private const val ARG_PARENT_IMAGE_URL = "parent_url"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UploadImageFragment.Callbacks {
    private var displayResultFragment = DisplayResultFragment.newInstance(null)
    private var uploadImageFragment = UploadImageFragment.newInstance(null)
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var mService: ResultImageService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ResultImageService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //installing the splashscreen and letting a coroutine splashscreen gets screen time
        installSplashScreen()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch { delay(10000) }

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        //We initialize with the uploadFragment
        setFragment(uploadImageFragment, R.id.upload)
        var selectedItem = bottomNavigationView.menu.findItem(R.id.upload)


        bottomNavigationView.setOnItemSelectedListener { m ->
            when (m.itemId) {
                R.id.upload -> setFragment(uploadImageFragment, m.itemId)
                R.id.display_result -> setFragment(displayResultFragment, m.itemId)
                R.id.display_collection -> Log.i(TAG, "Not Implemented")
            }
            selectedItem.apply { isEnabled = true }
            selectedItem = m
            selectedItem.isEnabled = false
            true
        }
    }

    private fun setFragment(currentFragment: Fragment, pos: Int) {
        if(checkIfFragmentVisible(pos)) return
        //If we are already on the selected fragment, we will return
        //We check this by adding a tag, related to the id of it's placement on the navbar,
        //If the if check is true, it means that fragment is already in layout
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, currentFragment, "$pos")
            .commit()
    }

    private fun checkIfFragmentVisible(i: Int): Boolean {
        //If the supportFragmentManager can't find the tag, (isNull)
        //The fragment is not instantiated
        supportFragmentManager.findFragmentByTag("$i") ?: return false
        //In addition we check if the non-null view is visible, if not,
        //We may proceed. If visible, ignore fragment change
        return supportFragmentManager.findFragmentByTag("$i")!!.isInLayout
    }

    override fun onImageSelected(image: UploadedImage) {
        val url = image.urlOnServer ?: return

        lifecycleScope.launch{
            mService.fetchImageData(url)
        }

        displayResultFragment = DisplayResultFragment.newInstance(image)

    }

    override fun onStart() {
        super.onStart()
        startService()
    }

    private fun startService(){
        val serviceIntent = Intent(this, ResultImageService::class.java)
        startService(serviceIntent)
        bindService()
    }

    private fun bindService(){
        val serviceIntent = Intent(this, ResultImageService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    fun getService(): ResultImageService {
        return mService
    }

    fun getConnection(): ServiceConnection {
        return connection
    }
}