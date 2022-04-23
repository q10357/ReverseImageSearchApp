package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.R
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayCollectiomItemBinding

private const val ARG_PARENT_ID = "parent_id"

@AndroidEntryPoint
class DisplayCollectionItemFragment: Fragment(R.layout.fragment_display_collectiom_item) {

    private lateinit var binding: FragmentDisplayCollectiomItemBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayCollectiomItemBinding.bind(view)

    }

    companion object{
        fun newInstance(parentImageId: Long): DisplayCollectionItemFragment {
            val args = Bundle().apply {
                putLong(ARG_PARENT_ID, parentImageId)
            }
            return DisplayCollectionItemFragment().apply {
                arguments = args
            }
        }
    }
}