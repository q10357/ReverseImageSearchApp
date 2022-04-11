package no.kristiania.android.reverseimagesearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import no.kristiania.android.reverseimagesearchapp.databinding.FragmentDisplayResultsBinding

@AndroidEntryPoint
class DisplayResultFragment: Fragment() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var binding: FragmentDisplayResultsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDisplayResultsBinding.bind(view)


        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = layoutManager
        Log.i("Hey girl", "Hey girl")
    }

    companion object {
        fun newInstance() = DisplayResultFragment()
    }
}