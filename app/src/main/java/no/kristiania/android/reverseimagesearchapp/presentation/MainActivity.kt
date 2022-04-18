package no.kristiania.android.reverseimagesearchapp.presentation

import android.os.Bundle
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
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment
import no.kristiania.android.reverseimagesearchapp.presentation.viewmodel.SharedViewModel

private const val TAG = "MainActivityTAG"
private const val ARG_PARENT_IMAGE_URL = "parent_url"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UploadImageFragment.Callbacks {
    private var displayResultFragment = DisplayResultFragment.newInstance(null)
    private var uploadImageFragment = UploadImageFragment.newInstance(null)
    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel by viewModels<SharedViewModel>()

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
        //Blocking main thread so no onclick can happen
        lifecycleScope.launch{
            val job = async {
                viewModel.fetchImageData(url)
            }
        }
        //We change the property to now be
        displayResultFragment = DisplayResultFragment.newInstance(image)
    }

//    private fun startService(){
//        val serviceIntent = Intent(this, ResultImageService::class.java)
//        startService(serviceIntent)
//        bindService()
//
//    }
//
//    private fun bindService(){
//        val serviceIntent = Intent(this, ResultImageService::class.java)
//        bindService(serviceIntent, viewModel.getConnection(), Context.BIND_AUTO_CREATE)
//    }
}