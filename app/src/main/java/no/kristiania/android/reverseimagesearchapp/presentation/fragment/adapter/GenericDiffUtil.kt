package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class GenericDiffUtil<T: Any> : DiffUtil.ItemCallback<T>(){
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

}