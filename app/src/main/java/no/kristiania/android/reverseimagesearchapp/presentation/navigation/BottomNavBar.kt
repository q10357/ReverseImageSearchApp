package no.kristiania.android.reverseimagesearchapp.presentation.navigation

import android.content.Context
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.qualifiers.ApplicationContext
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.DisplayResultFragment
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.UploadImageFragment
import javax.inject.Inject

class BottomNavBar (context: Context): BottomNavigationView(
    context, null, R.style.Theme_AppCompat_DayNight
) {
    private lateinit var selectedItem: MenuItem
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var uploadImageFragment: UploadImageFragment
    private lateinit var displayResultFragment: DisplayResultFragment
}