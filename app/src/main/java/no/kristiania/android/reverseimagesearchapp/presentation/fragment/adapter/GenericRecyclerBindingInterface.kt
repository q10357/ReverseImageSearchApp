package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

interface GenericRecyclerBindingInterface<T : Any>{
    fun bindData(instance: T, view: View)
}