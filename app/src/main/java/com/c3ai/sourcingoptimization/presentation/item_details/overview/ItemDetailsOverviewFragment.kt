package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.*
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsOverviewBinding
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.presentation.item_details.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AACrosshair
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject
import androidx.appcompat.widget.LinearLayoutCompat


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

    private var itemId = "item0"
    private var selectedSpinnerPosition = 0
    private lateinit var suppliers: List<C3Vendor>
    private val suppliersChartData = mutableMapOf<String, List<Double>>()
    private var marketPriceIndexRelationMetric: ItemMarketPriceIndexRelationMetric? = null
    var selectedCrosshairIndex = -1
    var indexId = ""
    private val chartColors: Array<Any> =
        arrayOf("#82B0FF", "#C799FF", "#F2950A", "#49BFA9", "#A7ADC4")


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemId = activity?.intent?.getStringExtra("id") ?: "item0"
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinnerView()

        var relations = listOf<ItemRelation>()

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
                    relations = result.relations
                    viewModel.getItemVendorRelationMetrics(
                        ids = relations.map { it.id },
                        expressions = listOf("OrderLineValue"),
                        startDate = formatDate(date = getYearBackDate(1)),
                        endDate = formatDate(date = getCurrentDate()),
                        interval = "MONTH"
                    )
                }
                is ItemDetailsUiState.HasItemVendorRelationMetrics -> {
                    val metrics = result.relationMetrics
                    relations.forEach {
                        suppliersChartData.put(
                            it.to.id,
                            metrics.result.get(it.id)?.OrderLineValue?.data ?: listOf()
                        )
                    }
                    bindMultiLineChart()
                }

                is ItemDetailsUiState.HasMarketPriceIndex -> {
                    if (result.indexes.isNotEmpty()) {
                        indexId = result.indexes[0].id
                        viewModel.getItemMarketPriceIndexRelation(itemId, indexId)
                    }
                }

                is ItemDetailsUiState.HasItemMarketPriceIndexRelation -> {
                    viewModel.getItemMarketPriceIndexRelationMetrics(
                        ids = listOf(indexId),
                        expressions = listOf("IndexPrice"),
                        startDate = formatDate(date = getYearBackDate(1)),
                        endDate = formatDate(date = getCurrentDate()),
                        interval = "MONTH"
                    )
                }

                is ItemDetailsUiState.HasItemMarketPriceIndexRelationMetrics -> {
                    marketPriceIndexRelationMetric = result.relationMetrics.result[indexId]
                    setGraphYear()
                    val indexPrice = marketPriceIndexRelationMetric?.IndexPrice
                    if (indexPrice != null) {
                        bindDashedLineChart(indexPrice)
                    }
                }
                else -> {
                    // TODO!!! Handle error and loading states
                }
            }
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
        binding.name.text = item.name
        if (item.hasActiveAlerts == true) {
            binding.alertsCount.visibility = View.VISIBLE
            binding.alertsCount.text = item.numberOfActiveAlerts.toString()
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
            data.result[itemId]?.OpenPOLineQuantity?.data?.get(0)?.toString()
        )
        binding.closedValue.text = String.format(
            "%s%s", "$",
            data.result[itemId]?.ClosedPOLineQuantity?.data?.get(0)?.toString()
        )
    }

    private fun bindSavingsOpportunity(data: SavingsOpportunityItem) {
        val aaGradientChartModel = configureGradientColorAreaChart(data)
        val gradientChartChart = binding.gradientChart
        gradientChartChart.aa_drawChartWithChartModel(aaGradientChartModel)

        val notMissingData =
            data.result[itemId]?.SavingsOpportunityCompound?.missing?.filter { it < 100 }
        val savingOpp = if (notMissingData.isNullOrEmpty()) 0 else notMissingData.sum()
            .div(notMissingData.size)

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
                            data.result[itemId]?.SavingsOpportunityCompound?.data?.toTypedArray()
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
            if (selectedSpinnerPosition == TOTAL_SPENT) "{point.y:,.2f}M" else "{point.y:,.0f} %"
        )
        return aaOptions
    }

    private fun configureColorfulColumnChart(): AAChartModel {
        val values = getBarChartData(suppliers)

        return AAChartModel()
            .chartType(AAChartType.Column)
            .colorsTheme(chartColors)
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
            .categories(suppliers.map { it.name }.toTypedArray())
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

    private fun bindMultiLineChart() {
        val lineChart = binding.lineChartView
        lineChart.isClearBackgroundColor = true
        lineChart.callBack = ChartCallback()
        lineChart.aa_drawChartWithChartOptions(multiLineChartOptions())
    }

    private fun configureMultiLineChart(): AAChartModel {

        val chartData = mutableListOf<AASeriesElement>()
        suppliersChartData.values.forEachIndexed { index, d ->
            val element = AASeriesElement()
                .color(chartColors[index])
                .lineWidth(2f)
                .data(d.toTypedArray())
            chartData.add(element)
        }

        val aaChartModel = AAChartModel.Builder(requireContext())
            .setChartType(AAChartType.Line)
            .setXAxisVisible(false)
            .setDataLabelsEnabled(false)
            .setAnimationType(AAChartAnimationType.Bounce)
            .setTouchEventEnabled(true)
            .setLegendEnabled(false)
            .setMarkerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .setMarkerRadius(0f)
            .setMarkerSymbol(AAChartSymbolType.Circle)
            .setTooltipEnabled(false)
            .setCategories(*arrayOf())
            .setYAxisTitle("")
            .setAxesTextColor("#AAAEB5")
            .build()

        aaChartModel
            .series(chartData.toTypedArray())
        return aaChartModel
    }

    private fun multiLineChartOptions(): AAOptions {
        val model = configureMultiLineChart()
        val aaOptions = model.aa_toAAOptions()
        aaOptions.xAxis?.crosshair(AACrosshair().width(1f))
        aaOptions.yAxis?.lineWidth(0f)?.gridLineColor(AAColor.Clear)?.lineColor(AAColor.Clear)

        //val states = AAStates().inactive(AAInactive().enabled(false))
        //aaOptions.plotOptions?.series?.states(states)
        return aaOptions
    }

    private fun bindDashedLineChart(data: IndexPrice) {
        val dashedLineChart = binding.dashedLineChartView
        dashedLineChart.isClearBackgroundColor = true
        dashedLineChart.callBack = ChartCallback()
        dashedLineChart.aa_drawChartWithChartOptions(dashedLineChartOptions(data))
    }

    private fun setGraphYear() {
        val firstYear = getYear(marketPriceIndexRelationMetric?.IndexPrice?.dates?.get(0) ?: "")
        val lastYear = getYear(
            marketPriceIndexRelationMetric?.IndexPrice?.dates?.get(
                marketPriceIndexRelationMetric?.IndexPrice?.dates?.size?.minus(1) ?: 0
            ) ?: ""
        )
        binding.year.text = if (firstYear == lastYear) firstYear.toString()
        else String.format("%s%s%s", firstYear.toString(), " - ", lastYear.toString())
    }

    private fun configureDashedLineChart(data: IndexPrice): AAChartModel {
        val max = data.data.toTypedArray().maxOrNull()
        val aaChartModel = AAChartModel.Builder(requireContext())
            .setChartType(AAChartType.Line)
            .setXAxisVisible(false)
            .setDataLabelsEnabled(false)
            .setAnimationType(AAChartAnimationType.Bounce)
            .setTouchEventEnabled(true)
            .setLegendEnabled(false)
            .setMarkerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .setMarkerRadius(3f)
            .setMarkerSymbol(AAChartSymbolType.Circle)
            .setTooltipEnabled(false)
            .setYAxisMax(if (max == null || max == 0.0) 100f else max.toFloat())
            .setCategories(*data.dates.map { getMonth(it) }.toTypedArray())
            .setCategories(*arrayOf())
            .setYAxisTitle("")
            .setAxesTextColor("#AAAEB5")
            .build()

        val element = AASeriesElement()
            .color("#008066")
            .name("")
            .lineWidth(2f)
            .data(data.data.toTypedArray())
            .dashStyle(AAChartLineDashStyleType.ShortDash)

        aaChartModel
            .series(arrayOf(element))
        return aaChartModel
    }

    private fun dashedLineChartOptions(data: IndexPrice): AAOptions {
        val model = configureDashedLineChart(data)
        val aaOptions = model.aa_toAAOptions()
        aaOptions.xAxis?.crosshair(AACrosshair().width(1f))
        aaOptions.yAxis?.gridLineColor(AAColor.Clear)?.lineColor(AAColor.Clear)
        aaOptions.xAxis?.gridLineColor(AAColor.Clear)
            ?.lineColor(AAColor.Clear)?.labels?.autoRotationLimit(0f)?.step(3)
        return aaOptions
    }

    inner class ChartCallback() : AAChartView.AAChartViewCallBack {
        override fun chartViewDidFinishLoad(aaChartView: AAChartView) {

        }

        override fun chartViewMoveOverEventMessage(
            aaChartView: AAChartView,
            messageModel: AAMoveOverEventMessageModel
        ) {
            updateCustomCrosshair(messageModel.index ?: -1, aaChartView)
        }

        private fun updateCustomCrosshair(index: Int, aaChartView: AAChartView) {
            if (selectedCrosshairIndex == index) {
                return
            }

            selectedCrosshairIndex = index

            val addPlotLine1 = "aaGlobalChart.series[0].points[$index].onMouseOver()"
            binding.lineChartView.post {
                if (aaChartView == binding.lineChartView) {
                    binding.dashedLineChartView.aa_evaluateTheJavaScriptStringFunction(addPlotLine1)
                } else {
                    binding.lineChartView.aa_evaluateTheJavaScriptStringFunction(addPlotLine1)
                }
            }
            updateData(index)
        }
    }

    private fun updateData(index: Int) {
        activity?.runOnUiThread {

            val date = marketPriceIndexRelationMetric?.IndexPrice?.dates?.get(index) ?: ""
            val dateString = getMonth(date) + " " + getYear(date)
            binding.dateSpp.text = dateString
            binding.dateIndex.text = dateString

            binding.price.text = String.format(
                "%s%s", "$", String.format(
                    "%.2f",
                    marketPriceIndexRelationMetric?.IndexPrice?.data?.get(index)
                )
            )

            binding.suppliersContainer.removeAllViews()
            suppliers.forEach {
                val supplierView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.supplier_item_view, binding.suppliersContainer, false)

                val param = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
                param.weight = 1f
                supplierView.layoutParams = param
                binding.suppliersContainer.addView(supplierView)
                supplierView.findViewById<TextView>(R.id.supplier).text = it.name
                supplierView.findViewById<TextView>(R.id.price).text =
                    String.format(
                        "%s%s", "$", String.format(
                            "%.2f",
                            suppliersChartData[it.id]?.get(index)
                        )
                    )
            }
        }
    }
}