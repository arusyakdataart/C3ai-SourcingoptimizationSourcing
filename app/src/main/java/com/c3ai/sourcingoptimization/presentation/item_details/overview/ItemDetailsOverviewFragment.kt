package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsOverviewBinding
import com.c3ai.sourcingoptimization.presentation.item_details.BaseFragment
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection

class ItemDetailsOverviewFragment : BaseFragment<FragmentItemDetailsOverviewBinding>(
    FragmentItemDetailsOverviewBinding::inflate)  {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSwitchToButtons()

        val aaBarChartModel = configureColorfulColumnChart()
        val barChart = binding.barChartView
        barChart.aa_drawChartWithChartModel(aaBarChartModel)

        val aaLineChartModel = configureLineChartAndSplineChartStyle()
        val lineChart = binding.lineChartView
        lineChart.aa_drawChartWithChartModel(aaLineChartModel)

        val aaDashedLineChartModel = configureDahsedLineChartAndSplineChartStyle()
        val dashedLineChart = binding.dashedLineChartView
        dashedLineChart.aa_drawChartWithChartModel(aaDashedLineChartModel)

        val aaGradientChartModel = configureGradientColorAreasplineChart()
        val gradientChartChart = binding.gradientChart
        gradientChartChart.aa_drawChartWithChartModel(aaGradientChartModel)
    }

    private fun setSwitchToButtons() {
        val totalSpentButton = binding.totalSpent
        val shareButton = binding.share
        totalSpentButton.isActivated = true
        totalSpentButton.setOnClickListener {
            if (!it.isActivated) {
                totalSpentButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_grey_view_20)
                totalSpentButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
                totalSpentButton.isActivated = true

                shareButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.outlined_view_20)
                shareButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_3))
                shareButton.isActivated = false

            }
        }
        shareButton.setOnClickListener {
            if (!it.isActivated) {
                shareButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_grey_view_20)
                shareButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_text))
                shareButton.isActivated = true

                totalSpentButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.outlined_view_20)
                totalSpentButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_3))
                totalSpentButton.isActivated = false
            }
        }
    }

    fun configureColorfulColumnChart(): AAChartModel {
        return AAChartModel()
            .chartType(AAChartType.Column)
            .colorsTheme(arrayOf("#82B0FF", "#C799FF", "#F2950A", "#49BFA9", "#A7ADC4"))
            .series(arrayOf(
                AASeriesElement()
                    .data(arrayOf(149.9, 171.5, 106.4, 129.2, 144.0))
                    .colorByPoint(true)))
            .dataLabelsEnabled(true)
            .legendEnabled(false)
            .tooltipEnabled(false)
            .xAxisVisible(true)
            .axesTextColor("#FFFFFF")
            .yAxisVisible(false)
            .yAxisLabelsEnabled(false)
            .xAxisLabelsEnabled(true)
            .yAxisMin(50f)
            .yAxisMax(175f)
            .stacking(AAChartStackingType.Normal)
            .backgroundColor("rgba(0,0,0,0)")
            .categories(arrayOf("Jan", "Feb", "March", "Apr", "May"))
            .dataLabelsStyle(
                AAStyle()
                .color("#1f1b1b")//Title font color
                .fontSize(11f)//Title font size
                .fontWeight(AAChartFontWeightType.Bold)//Title font weight
                .textOutline("0px 0px contrast"))
    }

    private fun configureLineChartAndSplineChartStyle(): AAChartModel {
        val aaChartModel = AAChartModel.Builder(requireContext())
            .setChartType(AAChartType.Line)
            .setBackgroundColor("rgba(0,0,0,0)")
            .setDataLabelsEnabled(false)
            .setXAxisGridLineWidth(0f)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setXAxisVisible(false)
            .setXAxisLabelsEnabled(false)
            .setYAxisLineWidth(0f)
            .setYAxisTitle("")
//            .setYAxisLabelsEnabled(false)
            //.setTooltipEnabled(false)
            //.setTooltipValueSuffix("")
            .setDataLabelsEnabled(false)
            .setTouchEventEnabled(true)
        .build()

        aaChartModel
            .markerSymbolStyle(AAChartSymbolStyleType.BorderBlank)
            .markerSymbol(AAChartSymbolType.Circle)
            .markerRadius(1f)

        val element1 = AASeriesElement()
            .color("#82B0FF")
            .name("IJK")
            .lineWidth(2f)
            .data(arrayOf(50, 320, 230, 370, 230, 400))

        val element2 = AASeriesElement()
            .color("#C799FF")
            .name("ETW")
            .lineWidth(1f)
            .data(arrayOf(20, 290, 310, 140, 240, 350))

        val element3 = AASeriesElement()
            .color("#F2950A")
            .name("ABC")
            .lineWidth(2f)
            .data(arrayOf(100, 370, 180, 280, 260, 300))

        val element4 = AASeriesElement()
            .color("#49BFA9")
            .name("FGH")
            .lineWidth(2f)
            .data(arrayOf(130, 350, 160, 310, 250, 268))


        val element5 = AASeriesElement()
            .color("#A7ADC4")
            .name("HAE")
            .lineWidth(2f)
            .data(arrayOf(150, 260, 100, 350, 150, 168))


            aaChartModel
                .animationType(AAChartAnimationType.SwingFromTo)
                .series(arrayOf(element1, element2, element3, element4, element5))
        return aaChartModel
    }

    private fun configureDahsedLineChartAndSplineChartStyle(): AAChartModel {
        val aaChartModel = AAChartModel.Builder(requireContext())
            .setChartType(AAChartType.Line)
            .setBackgroundColor("rgba(0,0,0,0)")
            .setDataLabelsEnabled(false)
            .setXAxisGridLineWidth(0f)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setXAxisVisible(false)
            .setXAxisLabelsEnabled(false)
            .setYAxisLineWidth(0f)
            .setYAxisTitle("")
//            .setYAxisLabelsEnabled(false)
            //.setTooltipEnabled(false)
            //.setTooltipValueSuffix("")
            .setDataLabelsEnabled(false)
            .setTouchEventEnabled(true)
            .build()

        aaChartModel
            .markerSymbolStyle(AAChartSymbolStyleType.InnerBlank)
            .markerSymbol(AAChartSymbolType.Circle)
            .markerRadius(3f)

        val element = AASeriesElement()
            .color("#008066")
            .name("")
            .lineWidth(2f)
            .data(arrayOf(50, 320, 230, 370, 230, 400))

        aaChartModel
            .animationType(AAChartAnimationType.SwingFromTo)
            .series(arrayOf(element))
        return aaChartModel
    }

    fun configureGradientColorAreasplineChart(): AAChartModel {
        val stopsArr:Array<Any> = arrayOf(
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
            .categories(arrayOf("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"))
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
//            .setYAxisLabelsEnabled(false)
            //.setTooltipEnabled(false)
            //.setTooltipValueSuffix("")
            .dataLabelsEnabled(false)
            .touchEventEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Tokyo Hot")
                    .lineWidth(5.0f)
                    .color("rgba(86,179,95, 1)")
                    .fillColor(linearGradientColor)
                    .data(arrayOf(7.0, 6.9, 2.5, 14.5, 18.2, 21.5, 5.2, 26.5, 23.3, 45.3, 13.9, 9.6))))
    }
}