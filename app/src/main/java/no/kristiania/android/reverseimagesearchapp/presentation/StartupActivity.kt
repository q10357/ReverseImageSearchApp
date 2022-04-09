package no.kristiania.android.reverseimagesearchapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDatabase
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment

@AndroidEntryPoint
class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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