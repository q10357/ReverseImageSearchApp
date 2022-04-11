package no.kristiania.android.reverseimagesearchapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDatabase
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.CropFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment

@AndroidEntryPoint
class StartupActivity : AppCompatActivity() {


    //variable for the time splashscreen is active
    private val splashScreenTime= 420L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()


        //TODO check our dudes way on youtube for better coroputine
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch { Thread.sleep(splashScreenTime) }

        setContentView(R.layout.activity_startup)

        val isFragmentContainerEmpty = savedInstanceState == null
        if(isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, UploadImageFragment.newInstance())
                .commit()
        }
    }
}