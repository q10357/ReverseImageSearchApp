package no.kristiania.android.reverseimagesearchapp.presentation

import android.view.View

interface OnClickListener {
    fun onClick(position: Int, view: View)

    fun onLongClick(position: Int)
}