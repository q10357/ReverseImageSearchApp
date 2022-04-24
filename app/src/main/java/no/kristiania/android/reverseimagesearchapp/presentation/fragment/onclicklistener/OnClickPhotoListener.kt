package no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener

import android.view.View

interface OnClickPhotoListener {
    fun onClick(position: Int, view: View)

    fun onLongClick(position: Int)
}