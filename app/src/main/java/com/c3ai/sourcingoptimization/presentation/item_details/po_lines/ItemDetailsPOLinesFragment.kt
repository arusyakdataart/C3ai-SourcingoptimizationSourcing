package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsPoLinesBinding
import com.c3ai.sourcingoptimization.presentation.item_details.BaseFragment
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsViewPagerFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The fragment representing PO Lines page in ItemDetailsViewPagerFragment
 * @see ItemDetailsViewPagerFragment
 * */

@AndroidEntryPoint
class ItemDetailsPOLinesFragment : BaseFragment<FragmentItemDetailsPoLinesBinding>(
    FragmentItemDetailsPoLinesBinding::inflate
) {

    @Inject
    lateinit var assistedFactory: ItemPOLinesViewModelAssistedFactory

    private val viewModel: ItemPOLinesViewModel by viewModels {
        ItemPOLinesViewModel.Factory(assistedFactory, "item0")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemPOLinesAdapter()
        binding.poLinesList.adapter = adapter
        setupScrollListener()

        viewModel.uiState.asLiveData().observe(viewLifecycleOwner, { result ->

            when (result) {
                is ItemPOLinesUiState.HasItems -> {
                    adapter.submitList(result.items)
                }
                else -> {
                    // TODO!!! Handle error and loading states
                }
            }
        })
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        expandCollapseView(binding.poLine1.expendCollapse, binding.poLine1.expandableContent)
//        expandCollapseView(binding.poLine2.expendCollapse, binding.poLine2.expandableContent)
//    }

    private fun expandCollapseView(text: TextView, view: View) {
        text.setOnClickListener {
            if (view.visibility == View.VISIBLE) {
                view.visibility = View.GONE
                text.text = "Expand "
                text.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.arrow_down,
                    0
                )
            } else {
                view.visibility = View.VISIBLE
                text.text = "Collapse "
                text.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.arrow_up,
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

    private fun setupScrollListener() {

        val layoutManager = binding.poLinesList.layoutManager as LinearLayoutManager
        binding.poLinesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                viewModel.accept(
                    UiAction.Scroll(
                        visibleItemCount = visibleItemCount,
                        lastVisibleItemPosition = lastVisibleItem,
                        totalItemCount = totalItemCount
                    )
                )
            }
        })
    }
}