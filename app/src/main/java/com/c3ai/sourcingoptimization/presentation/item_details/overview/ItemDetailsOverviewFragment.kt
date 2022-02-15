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
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.presentation.item_details.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject


/**
 * The fragment representing overview page in ItemDetailsViewPagerFragment
 * @see ItemDetailsViewPagerFragment
 * */

const val TOTAL_SPENT = 0
const val SHARE = 1

@AndroidEntryPoint
class ItemDetailsOverviewFragment : BaseFragment<FragmentItemDetailsOverviewBinding>(
    FragmentItemDetailsOverviewBinding::inflate
) {

    @Inject
    lateinit var assistedFactory: ItemDetailsViewModelAssistedFactory

    private val itemId = "item0"
    private var selectedSpinnerPosition = 0
    lateinit var suppliers: List<C3Vendor>


    private val viewModel: ItemDetailsViewModel by viewModels {
        ItemDetailsViewModel.Factory(
            assistedFactory, itemId,
            po_expressions = listOf("OpenPOLineQuantity", "ClosedPOLineQuantity"),
            po_startDate = formatDate(date = getYearBackDate(1)),
            po_endDate = formatDate(date = getCurrentDate()),
            po_interval = "YEAR",
            so_expressions = listOf("SavingsOpportunityCompound"),
            so_startDate = formatDate(date = getMonthBackDate(1)),
            so_endDate = formatDate(date = getCurrentDate()),
            so_interval = "MONTH",
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

        viewModel.uiState.asLiveData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ItemDetailsUiState.HasItem -> {
                    bindC3Item(result.item)
                }

                is ItemDetailsUiState.HasPOLinesQtyMetrics -> {
                    bindOpenClosedPOLineQty(result.poLineQty)
                }

                is ItemDetailsUiState.HasSavingsOpportunity -> {
                    bindSavingsOpportunity(result.savingsOpportunity)
                }
                is ItemDetailsUiState.HasSuppliers -> {
                    suppliers = result.suppliers
                    bindSuppliers()
                    viewModel.getItemVendorRelation(itemId, suppliers.map { it.id })
                }
                is ItemDetailsUiState.HasItemVendorRelation -> {
                    val relations = result.relations
                    viewModel.getItemVendorRelationMetrics(
                        ids = relations.map { it.id },
                        expressions = listOf("OrderLineValue"),
                        startDate = formatDate(date = getYearBackDate(1)),
                        endDate = formatDate(date = getCurrentDate()),
                        interval = "YEAR")
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
        }
    }

    private fun setSpinnerView() {
        val spinnerValues = listOf("Total Spent ($)", "Share (%)")
        binding.totalSharespinner.adapter = SpinnerArrayAdapter(requireContext(), spinnerValues)
        binding.totalSharespinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSpinnerPosition = position
                    binding.totalSharespinner.findViewById<TextView>(R.id.input).text =
                        spinnerValues[position]
                    (binding.totalSharespinner.adapter as SpinnerArrayAdapter).selectedPosition =
                        position

                    if (::suppliers.isInitialized) {
                        bindSuppliers()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    private fun bindC3Item(item: C3Item) {
        binding.description.text = item.description
        if (item.hasActiveAlerts == true) {
            binding.alertsCount.visibility = View.VISIBLE
            binding.alertsCount.text = item.numberOfActiveAlerts?.toString()
        }
        binding.suppliers.text = item.numberOfVendors?.toString()
        binding.inventory.text = String.format("%s%s", item.currentInventory?.value, " Cases")
        binding.lastPrice.text =
            String.format("%s%s", "$", String.format("%.2f", item.lastUnitPricePaid?.value))
        binding.avgPrice.text =
            String.format("%s%s", "$", String.format("%.2f", item.averageUnitPricePaid?.value))
        binding.lowestPrice.text =
            String.format("%s%s", "$", String.format("%.2f", item.minimumUnitPricePaid?.value))
    }

    private fun bindOpenClosedPOLineQty(data: OpenClosedPOLineQtyItem) {
        binding.openValue.text = String.format(
            "%s%s", "$",
            data.result?.item0?.OpenPOLineQuantity?.data?.get(0)?.toString()
        )
        binding.closedValue.text = String.format(
            "%s%s", "$",
            data.result?.item0?.ClosedPOLineQuantity?.data?.get(0)?.toString()
        )
    }

    private fun bindSavingsOpportunity(data: SavingsOpportunityItem) {
        val aaGradientChartModel = configureGradientColorAreaChart(data)
        val gradientChartChart = binding.gradientChart
        gradientChartChart.aa_drawChartWithChartModel(aaGradientChartModel)

        val notMissingData =
            data.result?.item0?.SavingsOpportunityCompound?.missing?.filter { it < 100 }
        val savingOpp = if (notMissingData.isNullOrEmpty()) 0 else notMissingData?.sum()
            ?.div(notMissingData.size)

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
                        .data(
                            data.result?.item0?.SavingsOpportunityCompound?.data?.toTypedArray()
                                ?: arrayOf()
                        )
                )
            )
    }

    private fun bindSuppliers() {
        val barChart = binding.barChartView
        barChart.aa_drawChartWithChartOptions(columnChartOptions())
    }

    private fun getMax(): Double? {
        if (selectedSpinnerPosition == TOTAL_SPENT) {
            return suppliers.map { formatNumber(it.spend.value) }.maxOrNull()
        }
        val total = suppliers.sumOf { it.spend.value }
        if (total == 0.0) {
            return 0.0
        }
        val shares = suppliers.map { ((it.spend.value / total!!) * 100) }.toTypedArray()
        return shares.maxOrNull()
    }

    private fun columnChartOptions(): AAOptions {
        val model = configureColorfulColumnChart()
        val aaColumn = AAColumn().groupPadding(0.01f).borderWidth(0f)

        val aaOptions = model.aa_toAAOptions()
        aaOptions.xAxis?.lineColor = AAColor.Clear
        aaOptions.plotOptions?.column = aaColumn
        model.aa_toAAOptions()
        aaOptions.plotOptions?.series?.dataLabels?.format(
            if (selectedSpinnerPosition == TOTAL_SPENT ) "{point.y:,.2f}M" else "{point.y:,.0f} %"
        )
        return aaOptions
    }

    private fun configureColorfulColumnChart(): AAChartModel {
        val values = getBarChartData(suppliers)

        return AAChartModel()
            .chartType(AAChartType.Column)
            .colorsTheme(arrayOf("#82B0FF", "#C799FF", "#F2950A", "#49BFA9", "#A7ADC4"))
            .series(
                arrayOf(
                    AASeriesElement()
                        .data(values)
                        .colorByPoint(true)
                )
            )
            .dataLabelsEnabled(true)
            .legendEnabled(false)
            .tooltipEnabled(false)
            .xAxisVisible(true)
            .axesTextColor("#FFFFFF")
            .yAxisVisible(false)
            .yAxisLabelsEnabled(false)
            .xAxisLabelsEnabled(true)
            .yAxisMin(0f)
            .yAxisMax(getMax()?.toFloat())
            .stacking(AAChartStackingType.Normal)
            .backgroundColor("rgba(0,0,0,0)")
            .categories(suppliers.map { it.name }.toTypedArray() ?: arrayOf())
            .dataLabelsStyle(
                AAStyle()
                    .color("#1f1b1b")//Title font color
                    .lineWidth(0f)
                    .fontSize(11f)//Title font size
                    .fontWeight(AAChartFontWeightType.Bold)//Title font weight
                    .textOutline("0px 0px contrast")
            )
    }

    private fun getBarChartData(data: List<C3Vendor>): Array<Any> {
        if (selectedSpinnerPosition == TOTAL_SPENT) {
            return data.map { formatNumber(it.spend.value) }.toTypedArray()
        }

        val total = data.sumOf { it.spend.value }
        if (total == 0.0) {
            return arrayOf()
        }
        return data.map { ((it.spend.value / total) * 100).toInt() }.toTypedArray()
    }

    private fun formatNumber(number: Double): Double {
        val dec = DecimalFormat("#,###.##")
        val formattedNumber = dec.format(number / 1000000)
        return formattedNumber.toDouble()
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