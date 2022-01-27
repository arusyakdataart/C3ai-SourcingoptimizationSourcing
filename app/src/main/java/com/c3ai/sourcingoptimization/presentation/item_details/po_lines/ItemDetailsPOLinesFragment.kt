package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsPoLinesBinding
import com.c3ai.sourcingoptimization.presentation.item_details.BaseFragment

class ItemDetailsPOLinesFragment : BaseFragment<FragmentItemDetailsPoLinesBinding>(
    FragmentItemDetailsPoLinesBinding::inflate)  {
    // TODO!!! Uncomment when using real data
//    private val viewModel by viewModels<POLinesViewModel>()
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val adapter = POLinesAdapter()
//        binding.poLinesList.adapter = adapter
//
//        viewModel.observableData.observe(viewLifecycleOwner, { pagedList ->
//            //adapter.submitList(pagedList)
//        })
//    }
}