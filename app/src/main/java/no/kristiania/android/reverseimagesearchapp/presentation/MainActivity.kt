package no.kristiania.android.reverseimagesearchapp.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayCollectionFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayCollectionItemFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import kotlin.properties.Delegates

private const val ARG_NAV_POSITION = "nav_position"
private const val TAG = "MainActivityTAG"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UploadImageFragment.Callbacks,
    DisplayCollectionFragment.Callbacks {
    private var displayResultFragment = DisplayResultFragment.newInstance(null)
    private var uploadImageFragment = UploadImageFragment.newInstance()
    private var displayCollectionFragment = DisplayCollectionFragment.newInstance()
    private lateinit var bottomNavigationView: BottomNavigationView
    private var navPos by Delegates.notNull<Int>()
    private lateinit var navMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //installing the splashscreen and letting a coroutine splashscreen gets screen time
        installSplashScreen()

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch { delay(1000) }

        setContentView(R.layout.activity_main)

        navPos = savedInstanceState?.getInt(ARG_NAV_POSITION) ?: R.id.upload
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        navMenuItem = bottomNavigationView.menu.findItem(navPos).apply {
            this.isEnabled = false
        }

        setFragment(getCurrentFragment(navPos), navPos)

        bottomNavigationView.setOnItemSelectedListener { m ->
            when (m.itemId) {
                R.id.upload -> setFragment(uploadImageFragment, m.itemId)
                R.id.display_result -> setFragment(displayResultFragment, m.itemId)
                R.id.display_collection -> setFragment(displayCollectionFragment, m.itemId)
            }
            navPos = m.itemId
            true
        }
    }

    private fun getCurrentFragment(navPos: Int): Fragment {
        return when (navPos) {
            R.id.upload -> uploadImageFragment
            R.id.display_result -> displayResultFragment
            R.id.display_collection -> displayCollectionFragment
            else -> {
                return uploadImageFragment
            }
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
        navMenuItem.apply { isEnabled = true }
        navMenuItem = bottomNavigationView.menu.findItem(pos)
        navMenuItem.isEnabled = false
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

        displayResultFragment = DisplayResultFragment.newInstance(image)
        navPos = R.id.display_result.also { bottomNavigationView.selectedItemId = it }
        setFragment(displayResultFragment, navPos)

    }

    override fun onCollectionSelected(parentId: Long) {
        //This fragment is not directly referenced in the navigation bar, as we need a concrete
        //Instance to have any use of it (as resultFragment, which we hopefully will
        //Put in a new activity and launch in a new intent later
        val fragment = DisplayCollectionItemFragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment.newInstance(parentId))
            .addToBackStack(null)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_NAV_POSITION, navPos)
    }
}