package no.kristiania.android.reverseimagesearchapp.presentation.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.android.reverseimagesearchapp.presentation.OnClickListener

class GenericRecyclerViewAdapter<T: Any>(
    private val dataSet: List<T>,
    @LayoutRes val layoutID: Int,
//    private val clickListener: (Int, View) -> Unit,
//    private val onLongClickListener: (Int, View) -> Unit,
    private val onClickListener: OnClickListener,
    private val bindingInterface: GenericRecyclerBindingInterface<T>
) :
    androidx.recyclerview.widget.ListAdapter<T, GenericRecyclerViewAdapter.GenericViewHolder>(GenericDiffUtil()){
    class GenericViewHolder(val view: View, private val onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener{
        fun<T: Any> bind(
            item: T,
            bindingInterface: GenericRecyclerBindingInterface<T>
        ) = bindingInterface.bindData(item, view)
        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }
        override fun onClick(view: View) {
            onClickListener.onClick(layoutPosition, view)
        }

        override fun onLongClick(view: View?): Boolean {
            onClickListener.onLongClick(layoutPosition)
            return true
        }
    }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(list)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): GenericViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(layoutID, viewGroup, false)

        return GenericViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        val objectInstance = dataSet[position]
        holder.bind(objectInstance, bindingInterface)
    }

    override fun getItemCount(): Int = dataSet.size
}
