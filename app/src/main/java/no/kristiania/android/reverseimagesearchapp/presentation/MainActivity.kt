package no.kristiania.android.reverseimagesearchapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.awaitAll
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment

private const val TAG = "ActivityMain"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //private val awesomeOnClickListener = bottomNavigationView.setOnItemSelectedListener { navigate() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uploadImageFragment = UploadImageFragment.newInstance()
        val displayResultFragment = DisplayResultFragment.newInstance()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
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
        Log.i("MAIN", "$pos")
        if(checkIfFragmentVisible(pos)) return
        //If we are already on the selected fragment, we will return
        //We check this by adding a tag, related to the id of it's placement on the navbar,
        //If the if check is true, it means that fragment is already in layout
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, currentFragment, "$pos")
            .commit()
    }

    private fun checkIfFragmentVisible(i: Int): Boolean {
        //If the supportFragmentManager can't find the tag, (isNull)
        //The fragment is not instantiated
        supportFragmentManager.findFragmentByTag("$i") ?: return false
        //In addition we check if the non-null view is visible, if not,
        //We may proceed. If visible, ignore fragment change
        return supportFragmentManager.findFragmentByTag("$i")!!.isResumed
    }

}