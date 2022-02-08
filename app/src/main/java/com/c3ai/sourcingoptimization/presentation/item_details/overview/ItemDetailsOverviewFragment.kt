package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsOverviewBinding
import com.c3ai.sourcingoptimization.presentation.item_details.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * The fragment representing overview page in ItemDetailsViewPagerFragment
 * @see ItemDetailsViewPagerFragment
 * */
@AndroidEntryPoint
class ItemDetailsOverviewFragment : BaseFragment<FragmentItemDetailsOverviewBinding>(
    FragmentItemDetailsOverviewBinding::inflate
) {

    @Inject
    lateinit var assistedFactory: ItemDetailsViewModelAssistedFactory

    private val viewModel: ItemDetailsViewModel by viewModels {
        ItemDetailsViewModel.Factory(assistedFactory, "item0",
            listOf("OpenPOLineQuantity", "ClosedPOLineQuantity"),
            "2021-01-01T00:00:00",
            "2021-01-05T00:00:00",
            "YEAR")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OverviewItemsAdapter()
        binding.overviewItemsList.adapter = adapter
        setupScrollListener()

        viewModel.uiState.asLiveData().observe(viewLifecycleOwner, { result ->
            if (result is ItemDetailsUiState.HasItems) {
                adapter.submitList(result.items)

            } else {
                    // TODO!!! Handle error and loading states
                }
        })
    }

    private fun setupScrollListener() {

        val layoutManager = binding.overviewItemsList.layoutManager as LinearLayoutManager
        binding.overviewItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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