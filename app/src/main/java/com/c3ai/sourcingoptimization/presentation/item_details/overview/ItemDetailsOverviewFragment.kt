package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.formatDate
import com.c3ai.sourcingoptimization.common.getCurrentDate
import com.c3ai.sourcingoptimization.common.getMonthBackDate
import com.c3ai.sourcingoptimization.common.getYearBackDate
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsOverviewBinding
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.OpenClosedPOLineQtyItem
import com.c3ai.sourcingoptimization.domain.model.SavingsOpportunityItem
import com.c3ai.sourcingoptimization.presentation.item_details.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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
            po_expressions = listOf("OpenPOLineQuantity", "ClosedPOLineQuantity"),
            po_startDate = formatDate(date = getYearBackDate(1)),
            po_endDate = formatDate(date = getCurrentDate()),
            po_interval = "YEAR",
            so_expressions = listOf("SavingsOpportunityCompound"),
            so_startDate = formatDate(date = getMonthBackDate(1)),
            so_endDate = formatDate(date = getCurrentDate()),
            so_interval = "DAY",
        )
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adapter = OverviewItemsAdapter()
//        binding.overviewItemsList.adapter = adapter
//        setupScrollListener()

//        var items: List<C3Item>? = null
//        var poLines: OpenClosedPOLineQtyItem? = null
//        var savingOpportunities: SavingOpportunityItem? = null

        setSpinnerView()

        viewModel.uiState.asLiveData().observe(viewLifecycleOwner, { result ->

            when (result) {
                is ItemDetailsUiState.HasItems -> {
                    bindC3Item(result.items[0])
                }

                is ItemDetailsUiState.HasPOLinesQtyMetrics -> {
                    bindOpenClosedPOLineQty(result.poLineQty)
                }

                is ItemDetailsUiState.HasSavingsOpportunity -> {
                    bindSavingsOpportunity(result.savingsOpportunity)
                }
                else -> {
                    // TODO!!! Handle error and loading states
                }
            }
//            if (result is ItemDetailsUiState.HasItems) {
//                adapter.submitList(result.items)
//
//            } else {
//                    // TODO!!! Handle error and loading states
//                }
        })
    }

    private fun setSpinnerView() {
        val spinnerValues = listOf("Total Spent ($)", "Share (%)")
        binding.totalSharespinner.adapter = SpinnerArrayAdapter(requireContext(), spinnerValues)
        binding.totalSharespinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.totalSharespinner.findViewById<TextView>(R.id.input).text = spinnerValues[position]
                (binding.totalSharespinner.adapter as SpinnerArrayAdapter).selectedPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun bindC3Item(item: C3Item) {
        binding.description.text = item.description
        if (item.hasActiveAlerts == true) {
            binding.alertsCount.visibility = View.VISIBLE
            binding.alertsCount.text = item.numberOfActiveAlerts?.toString()
            binding.suppliers.text = item.numberOfVendors?.toString()
        }
        binding.inventory.text = String.format("%s%s", item.currentInventory?.value, " Cases")
        binding.lastPrice.text = String.format("%s%s", "$", String.format("%.2f", item.lastUnitPricePaid?.value))
        binding.avgPrice.text = String.format("%s%s", "$", String.format("%.2f", item.averageUnitPricePaid?.value))
        binding.lowestPrice.text = String.format("%s%s", "$", String.format("%.2f", item.minimumUnitPricePaid?.value))
    }

    private fun bindOpenClosedPOLineQty(data: OpenClosedPOLineQtyItem) {
        binding.openValue.text = String.format("%s%s", "$",
            data.result?.item0?.OpenPOLineQuantity?.data?.get(0)?.toString())
        binding.closedValue.text = String.format("%s%s", "$",
            data.result?.item0?.ClosedPOLineQuantity?.data?.get(0)?.toString())
    }

    private fun bindSavingsOpportunity(data: SavingsOpportunityItem) {
        val aaGradientChartModel = configureGradientColorAreaChart(data)
        val gradientChartChart = binding.gradientChart
        gradientChartChart.aa_drawChartWithChartModel(aaGradientChartModel)

        val notMissingData = data.result?.item0?.SavingsOpportunityCompound?.missing?.filter { it < 100 }
        val savingOpp = if (notMissingData.isNullOrEmpty())0 else notMissingData?.sum()?.div(notMissingData.size)

        binding.savingPrice.text = String.format("%s%s", "$", savingOpp)
    }

    private fun configureGradientColorAreaChart(data: SavingsOpportunityItem): AAChartModel {
        val stopsArr: Array<Any> = arrayOf(
            arrayOf(0, "rgba(86,179,95,1)"),
            arrayOf(1, "rgba(86,179,95,0.5)")
        )

        val linearGradientColor = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToBottom,
            stopsArr
        )

        return AAChartModel()
            .chartType(AAChartType.Areaspline)
            .title("")
            .subtitle("")
            .backgroundColor("rgba(0,0,0,0)")
            .yAxisTitle("")
            .markerRadius(8f)
            .markerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .markerSymbol(AAChartSymbolType.Circle)
            .yAxisLineWidth(0f)
            .yAxisGridLineWidth(0f)
            .legendEnabled(false)
            .xAxisVisible(false)
            .xAxisLabelsEnabled(false)
            .yAxisLineWidth(0f)
            .yAxisTitle("")
            .yAxisVisible(false)
            .markerRadius(0f)
            .tooltipEnabled(false)
            .dataLabelsEnabled(false)
            .touchEventEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Saving Opportunity")
                        .lineWidth(3.0f)
                        .color("rgba(86,179,95, 1)")
                        .fillColor(linearGradientColor)
                        .data(data.result?.item0?.SavingsOpportunityCompound?.data?.toTypedArray() ?: arrayOf())
                )
            )
    }

//    private fun setupScrollListener() {
//
//        val layoutManager = binding.overviewItemsList.layoutManager as LinearLayoutManager
//        binding.overviewItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val totalItemCount = layoutManager.itemCount
//                val visibleItemCount = layoutManager.childCount
//                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                viewModel.accept(
//                    UiAction.Scroll(
//                        visibleItemCount = visibleItemCount,
//                        lastVisibleItemPosition = lastVisibleItem,
//                        totalItemCount = totalItemCount
//                    )
//                )
//            }
//        })
//    }
}