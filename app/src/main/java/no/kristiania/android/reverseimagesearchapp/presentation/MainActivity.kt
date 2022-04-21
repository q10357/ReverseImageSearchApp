package no.kristiania.android.reverseimagesearchapp.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.*
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayCollectionFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService

private const val ARG_NAV_POSITION = "nav_position"
private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UploadImageFragment.Callbacks {
    private var displayResultFragment = DisplayResultFragment.newInstance(null)
    private var uploadImageFragment = UploadImageFragment.newInstance()
    private var displayCollectionFragment = DisplayCollectionFragment.newInstance()
    private lateinit var bottomNavigationView: BottomNavigationView
    private var navPos: Int? = null

    private lateinit var mService: ResultImageService
    private var serviceStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //installing the splashscreen and letting a coroutine splashscreen gets screen time
        installSplashScreen()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch { delay(1000) }

        setContentView(R.layout.activity_main)

        navPos = Bundle().getInt(ARG_NAV_POSITION)
        if(navPos == null){
            //If Bundle() empty
            //We initialize with the uploadFragment
            navPos = R.id.upload
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        setFragment(getCurrentFragment(navPos!!), navPos!!)
        var navMenuItem = bottomNavigationView.menu.findItem(R.id.upload)

        bottomNavigationView.setOnItemSelectedListener { m ->
            when (m.itemId) {
                R.id.upload -> setFragment(uploadImageFragment, m.itemId)
                R.id.display_result -> setFragment(displayResultFragment, m.itemId)
                R.id.display_collection -> setFragment(displayCollectionFragment,m.itemId)
            }
            navPos = m.itemId
            navMenuItem.apply { isEnabled = true }
            navMenuItem = m
            navMenuItem.isEnabled = false
            true
        }
    }

    private fun getCurrentFragment(navPos: Int): Fragment {
        return when(navPos){
            R.id.upload -> uploadImageFragment
            R.id.display_result -> displayResultFragment
            R.id.display_collection -> displayCollectionFragment
            else -> {return uploadImageFragment}
        }
    }

    private fun setFragment(currentFragment: Fragment, pos: Int) {
        if (checkIfFragmentVisible(pos)) return
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
        startService(Intent(this, ResultImageService::class.java)
            .putExtra("image_url", url))
        serviceStarted = true

        displayResultFragment = DisplayResultFragment.newInstance(image)
    }

    private fun startService(){
        val intent = Intent(this, ResultImageService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        navPos?.let { Bundle().putInt(ARG_NAV_POSITION, it) }
    }
}