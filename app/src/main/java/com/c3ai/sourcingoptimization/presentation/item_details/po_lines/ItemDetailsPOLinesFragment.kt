package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsPoLinesBinding
import com.c3ai.sourcingoptimization.presentation.item_details.BaseFragment
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsViewPagerFragment

/**
 * The fragment representing PO Lines page in ItemDetailsViewPagerFragment
 * @see ItemDetailsViewPagerFragment
 * */
class ItemDetailsPOLinesFragment : BaseFragment<FragmentItemDetailsPoLinesBinding>(
    FragmentItemDetailsPoLinesBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandCollapseView(binding.poLine1.expendCollapse, binding.poLine1.expandableContent)
        expandCollapseView(binding.poLine2.expendCollapse, binding.poLine2.expandableContent)
    }

    private fun expandCollapseView(text: TextView, view: View) {
        text.setOnClickListener {
            if (view.visibility == View.VISIBLE) {
                view.visibility = View.GONE
                text.text = "Expand"
                text.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_down_24,
                    0,
                    0,
                    0
                )
            } else {
                view.visibility = View.VISIBLE
                text.text = "Collapse"
                text.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_up_24,
                    0,
                    0,
                    0
                )
            }
        }
    }
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