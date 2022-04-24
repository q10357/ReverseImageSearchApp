package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.presentation.fragment.onclicklistener.OnClickPhotoListener

class GenericPhotoAdapter<T: Any>(
    private val dataSet: List<T>,
    @LayoutRes val layoutID: Int,
    private val onClickPhotoListener: OnClickPhotoListener,
    private val bindingInterface: GenericRecyclerBindingInterface<T>
) :
    androidx.recyclerview.widget.ListAdapter<T, GenericPhotoAdapter.GenericViewHolder>(GenericDiffUtil()){
    class GenericViewHolder(val view: View, private val onClickPhotoListener: OnClickPhotoListener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        fun<T: Any> bind(
            item: T,
            bindingInterface: GenericRecyclerBindingInterface<T>
        ) = bindingInterface.bindData(item, view)
        init {
            view.setOnClickListener(this)
        }
        override fun onClick(view: View) {
            onClickPhotoListener.onClick(layoutPosition, view)
        }
    }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): GenericViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(layoutID, viewGroup, false)

        return GenericViewHolder(view, onClickPhotoListener)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        val objectInstance = dataSet[position]
        holder.bind(objectInstance, bindingInterface)
    }

    override fun getItemCount(): Int = dataSet.size
}
