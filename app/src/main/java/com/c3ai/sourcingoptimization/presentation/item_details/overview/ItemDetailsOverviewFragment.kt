package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.databinding.FragmentItemDetailsOverviewBinding
import com.c3ai.sourcingoptimization.presentation.item_details.BaseFragment
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.*

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

    fun configureStepLineChart(): AAChartModel {
        return AAChartModel()
            .chartType(AAChartType.Line)
            .animationType(AAChartAnimationType.Bounce)
            .backgroundColor("rgba(0,0,0,0)")
            .legendEnabled(false)
            .tooltipEnabled(false)
            .dataLabelsEnabled(false)
            .markerSymbolStyle(AAChartSymbolStyleType.BorderBlank)
            .markerRadius(7f)
            .series(arrayOf(
                AASeriesElement()
                    .name("Berlin")
                    .data(arrayOf(450, 432, 401, 454, 590, 530, 510))
                    .step("right")
                , AASeriesElement()
                    .name("New York")
                    .data(arrayOf(220, 282, 201, 234, 290, 430, 410))
                    .step("center")
                , AASeriesElement()
                    .name("Tokyo")
                    .data(arrayOf(120, 132, 101, 134, 90, 230, 210))
                    .step("left")
            ))
    }

    private fun configureLineChartAndSplineChartStyle(): AAChartModel {
        val aaChartModel = AAChartModel.Builder(requireContext())
            .setChartType(AAChartType.Spline)
            .setBackgroundColor("rgba(0,0,0,0)")
            .setDataLabelsEnabled(false)
            .setXAxisGridLineWidth(0f)
            .setYAxisGridLineWidth(0f)
            .setLegendEnabled(false)
            .setTooltipEnabled(false)
            .setDataLabelsEnabled(false)
            .setTouchEventEnabled(true)
            .setSeries(
                AASeriesElement()
                    .name("Tokyo")
                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                AASeriesElement()
                    .name("NewYork")
                    .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
                AASeriesElement()
                    .name("London")
                    .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
                AASeriesElement()
                    .name("Berlin")
                    .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
            )
        .build()

        aaChartModel
            .markerSymbolStyle(AAChartSymbolStyleType.BorderBlank)
            .markerSymbol(AAChartSymbolType.Circle)
            .markerRadius(6f)

        val element1 = AASeriesElement()
            .color("#82B0FF")
            .name("Tokyo")
            .lineWidth(2f)
            .data(arrayOf(50, 320, 230, 370, 230, 400))

        val element2 = AASeriesElement()
            .color("#C799FF")
            .name("Berlin")
            .lineWidth(1f)
            .data(arrayOf(80, 390, 210, 340, 240, 350))

        val element3 = AASeriesElement()
            .color("#F2950A")
            .name("New York")
            .lineWidth(2f)
            .data(arrayOf(100, 370, 180, 280, 260, 300))

        val element4 = AASeriesElement()
            .color("#49BFA9")
            .name("London")
            .lineWidth(2f)
            .data(arrayOf(130, 350, 160, 310, 250, 268))


        val element5 = AASeriesElement()
            .color("#A7ADC4")
            .name("Yerevan")
            .lineWidth(2f)
            .data(arrayOf(130, 350, 160, 310, 250, 268))


            aaChartModel
                .animationType(AAChartAnimationType.Linear)
                .series(arrayOf(element1, element2, element3, element4, element5))
        return aaChartModel
    }
}