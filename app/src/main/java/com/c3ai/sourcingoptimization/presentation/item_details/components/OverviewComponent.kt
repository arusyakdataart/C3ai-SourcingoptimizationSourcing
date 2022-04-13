package com.c3ai.sourcingoptimization.presentation.item_details.components

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsUiState
import com.c3ai.sourcingoptimization.presentation.views.UiSavingsOpportunityItem
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.IndexPriceCharts
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.SuppliersCharts
import com.c3ai.sourcingoptimization.ui.theme.CardBackgroundSecondary
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AACrosshair
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import com.google.gson.Gson

private val chartColors: Array<Any> = arrayOf("#82B0FF", "#C799FF", "#F2950A", "#49BFA9", "#A7ADC4")

/**
 * Decomposition of item details[ItemDetailsDataScreen] with separate component for overview tab
 * to make a code supporting easier[OverviewComponent].
 * */
@Composable
fun OverviewComponent(
    uiState: ItemDetailsUiState.HasItem,
    onAlertsClick: (String) -> Unit,
    onDateRangeSelected: (Int) -> Unit,
    onStatsTypeSelected: (Int) -> Unit,
    onSupplierClick: (String) -> Unit,
    onIndexClick: (String) -> Unit,
    onChartViewMoveOver: (Int) -> Unit,
) {

    val chartsSynchronizer: ChartsSynchronizer by remember {
        mutableStateOf(ChartsSynchronizer(onChartViewMoveOver))
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ItemDetailsInfo(uiState, onAlertsClick)
        Box(modifier = Modifier.height(16.dp))
        SourcingAnalysis(
            uiState,
            onDateRangeSelected,
            onStatsTypeSelected,
            onSupplierClick,
            onIndexClick,
            chartsSynchronizer,
        )
    }
}

@Composable
private fun ItemDetailsInfo(
    uiState: ItemDetailsUiState.HasItem,
    onAlertsClick: (String) -> Unit,
) {
    val item = uiState.item
    val savingsOpportunity = uiState.savingsOpportunity
    val ocPOLineQty = uiState.ocPOLineQty
    C3SimpleCard {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Create references for the composables to constrain
            val (
                alerts,
                desc,
                savingOpportunity,
                suppliersWithContract,
                soChartView,
                last30days,
                divider,
                closedPOLValue,
                openPOLValue,
                inventory,
                lastPrice,
                avgPrice,
                lowestPrice,
            ) = createRefs()
            Text(
                (item.name ?: ", ") + (item.description ?: ""),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(end = 40.dp)
                    .constrainAs(desc) {
                        top.linkTo(parent.top)
                    }
            )
            C3IconButton(
                onClick = { onAlertsClick(item.id) },
                badgeText = item.numberOfActiveAlerts,
                modifier = Modifier
                    .constrainAs(alerts) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = stringResource(R.string.cd_read_more),
                    tint = MaterialTheme.colors.primary
                )
            }
            LabeledValue(
                label = stringResource(R.string.saving_opportunity),
                value = savingsOpportunity?.savingOppText ?: "",
                modifier = Modifier
                    .constrainAs(savingOpportunity) {
                        top.linkTo(desc.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        width = Dimension.fillToConstraints
                    },
            )
            LabeledValue(
                label = stringResource(R.string.suppliers_with_contract),
                value = item.numberOfVendors.toString(),
                modifier = Modifier
                    .width(80.dp)
                    .constrainAs(suppliersWithContract) {
                        top.linkTo(desc.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    },
            )
            AndroidView(
                factory = { ctx ->
                    AAChartView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                },
                update = { view ->
                    if (view.tag != uiState.chartsHashCode) {
                        view.tag = uiState.chartsHashCode
                        savingsOpportunity?.let {
                            view.aa_drawChartWithChartModel(
                                configureGradientColorAreaChart(it)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .height(38.dp)
                    .constrainAs(soChartView) {
                        top.linkTo(savingOpportunity.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(suppliersWithContract.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            Text(
                stringResource(R.string.last_30_days),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(last30days) {
                        top.linkTo(soChartView.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                    },
            )
            ListDivider(Modifier.constrainAs(divider) { top.linkTo(last30days.bottom) })
            LabeledValue(
                label = stringResource(R.string.closed_pol_value),
                value = ocPOLineQty.closedValueText,
                labelModifier = Modifier.height(32.dp),
                modifier = Modifier
                    .constrainAs(closedPOLValue) {
                        top.linkTo(divider.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(openPOLValue.start)
                        width = Dimension.fillToConstraints
                    },
            )
            LabeledValue(
                label = stringResource(R.string.open_pol_value),
                value = ocPOLineQty.openValueText,
                labelModifier = Modifier.height(32.dp),
                modifier = Modifier
                    .constrainAs(openPOLValue) {
                        top.linkTo(divider.bottom)
                        start.linkTo(closedPOLValue.end, margin = 12.dp)
                        end.linkTo(inventory.start)
                        width = Dimension.fillToConstraints
                    },
            )
            LabeledValue(
                label = stringResource(R.string.inventory),
                value = String.format(
                    "%s %s", item.currentInventory?.value, stringResource(R.string.cases)
                ),
                labelModifier = Modifier.height(32.dp),
                modifier = Modifier
                    .constrainAs(inventory) {
                        top.linkTo(divider.bottom)
                        start.linkTo(openPOLValue.end, margin = 12.dp)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
            )
            LabeledValue(
                label = stringResource(R.string.last_price),
                value = "",
                modifier = Modifier
                    .constrainAs(lastPrice) {
                        top.linkTo(closedPOLValue.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(avgPrice.start)
                        width = Dimension.fillToConstraints
                    },
            )
            Column(
                modifier = Modifier
                    .constrainAs(avgPrice) {
                        top.linkTo(closedPOLValue.bottom, margin = 16.dp)
                        start.linkTo(lastPrice.end, margin = 12.dp)
                        end.linkTo(lowestPrice.start)
                        width = Dimension.fillToConstraints
                    },
            ) {
                LabeledValue(
                    label = stringResource(R.string.avg_price),
                    value = item.averageUnitPricePaid,
                )
                Text(
                    stringResource(R.string.last_12_mo),
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.secondary
                )
            }
            Column(
                modifier = Modifier
                    .constrainAs(lowestPrice) {
                        top.linkTo(closedPOLValue.bottom, margin = 16.dp)
                        start.linkTo(avgPrice.end, margin = 12.dp)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
            ) {
                LabeledValue(
                    label = stringResource(R.string.lowest_price),
                    value = item.averageUnitPricePaid,
                )
                Text(
                    stringResource(R.string.last_12_mo),
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
private fun SourcingAnalysis(
    uiState: ItemDetailsUiState.HasItem,
    onDateRangeSelected: (Int) -> Unit,
    onStatsTypeSelected: (Int) -> Unit,
    onSupplierClick: (String) -> Unit,
    onIndexClick: (String) -> Unit,
    chartsSynchronizer: ChartsSynchronizer,
) {
    val context = LocalContext.current
    val item = uiState.item
    C3SimpleCard {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (
                title,
                dateRange,
                statsMenu,
                suppliersChartView,
                divider,
                supplierTitle,
                supplierRoute,
                supplierDate,
                supplierList,
                divider2,
                indexTitle,
                indexRoute,
                indexDate,
                indexData,
                divider3,
                bottomGraphCard,
            ) = createRefs()
            Text(
                stringResource(R.string.sourcing_analysis),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(dateRange.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            Spinner(
                items = stringArrayResource(R.array.dateRange).asList(),
                onItemSelectedListener = { position, _ -> onDateRangeSelected(position) },
                selectedPosition = uiState.dateRangeSelected,
                modifier = Modifier
                    .constrainAs(dateRange) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            )
            CardMenu(
                items = stringArrayResource(R.array.sourcingAnalysisStatsType).asList(),
                onItemSelectedListener = { position, _ -> onStatsTypeSelected(position) },
                modifier = Modifier
                    .constrainAs(statsMenu) {
                        top.linkTo(dateRange.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    }
            )
            AndroidView(
                factory = { ctx ->
                    AAChartView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                },
                update = { view ->
                    Log.e("chartsHashCode", uiState.chartsHashCode.toString())
                    Log.e("view.tag", view.tag?.toString() ?: "")
                    if (view.tag != uiState.chartsHashCode) {
                        view.tag = uiState.chartsHashCode
                        uiState.suppliersChart?.let {
                            val model = configureColumnChart(it)
                            val aaColumn = AAColumn().groupPadding(0.01f).borderWidth(0f)

                            val aaOptions = model.aa_toAAOptions()
                            aaOptions.xAxis?.lineColor = AAColor.Clear
                            aaOptions.plotOptions?.column = aaColumn
                            model.aa_toAAOptions()
                            aaOptions.plotOptions?.series?.dataLabels?.format(it.dataLabelsFormat)
                            view.aa_drawChartWithChartOptions(aaOptions)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .constrainAs(suppliersChartView) {
                        top.linkTo(title.bottom, margin = 40.dp)
                        start.linkTo(parent.start)
                    }
            )
            ListDivider(Modifier.constrainAs(divider) { top.linkTo(suppliersChartView.bottom) })
            Text(
                stringResource(R.string.supplier),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(supplierTitle) {
                        top.linkTo(divider.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(supplierRoute.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            C3IconButton(
                onClick = {
                    onSupplierClick(Gson().toJson(uiState.suppliersChart?.suppliers?.ids))
                },
                modifier = Modifier
                    .constrainAs(supplierRoute) {
                        top.linkTo(divider.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.cd_read_more),
                    tint = MaterialTheme.colors.primary
                )
            }
            Text(
                uiState.indexPriceChart?.dateText ?: "",
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(supplierDate) {
                        top.linkTo(supplierTitle.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                    }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(supplierList) {
                        top.linkTo(supplierDate.bottom, margin = 8.dp)
                    }
                    .horizontalScroll(rememberScrollState())
            ) {
                uiState.suppliersChart?.suppliers?.chartData?.forEach { (key, value) ->
                    NameValue(
                        name = key,
                        value = value,
                        modifier = Modifier.padding(end = 30.dp)
                    )
                }
            }
            ListDivider(Modifier.constrainAs(divider2) { top.linkTo(supplierList.bottom) })
            Text(
                stringResource(R.string.index),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(indexTitle) {
                        top.linkTo(divider2.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(indexRoute.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            C3IconButton(
                onClick = { onIndexClick(item.id) },
                modifier = Modifier
                    .constrainAs(indexRoute) {
                        top.linkTo(divider2.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.cd_read_more),
                    tint = MaterialTheme.colors.primary
                )
            }
            Text(
                uiState.indexPriceChart?.dateText ?: "",
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(indexDate) {
                        top.linkTo(indexTitle.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                    }
            )
            NameValue(
                name = uiState.indexPriceChart?.nameText ?: "",
                value = uiState.indexPriceChart?.priceText ?: "",
                modifier = Modifier
                    .constrainAs(indexData) {
                        top.linkTo(indexDate.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    }
            )
            ListDivider(Modifier.constrainAs(divider3) { top.linkTo(indexData.bottom) })
            C3SimpleCard(
                backgroundColor = CardBackgroundSecondary,
                border = BorderStroke(1.dp, CardBackgroundSecondary),
                modifier = Modifier
                    .constrainAs(bottomGraphCard) { top.linkTo(divider3.bottom) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(R.string.supplier_avg_price_symbol, "$"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)
                    )
                    AndroidView(
                        factory = { ctx ->
                            AAChartView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            }
                        },
                        update = { view ->
                            if (view.tag != uiState.chartsHashCode) {
                                view.tag = uiState.chartsHashCode
                                uiState.vendorRelationMetrics?.let {
                                    val model = configureMultiLineChart(context, it)
                                    val aaOptions = model.aa_toAAOptions().apply {
                                        xAxis?.crosshair(AACrosshair().width(1f))
                                        yAxis?.apply {
                                            lineWidth(0f)
                                            gridLineColor(AAColor.Clear)
                                            lineColor(AAColor.Clear)
                                        }
                                    }
                                    view.isClearBackgroundColor = true
                                    chartsSynchronizer.views.add(view)
                                    view.callBack = chartsSynchronizer
                                    view.aa_drawChartWithChartOptions(aaOptions)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                    )
                    Text(
                        stringResource(R.string.index_symbol, "$"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)
                    )
                    AndroidView(
                        factory = { ctx ->
                            AAChartView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            }
                        },
                        update = { view ->
                            if (view.tag != uiState.chartsHashCode) {
                                view.tag = uiState.chartsHashCode
                                uiState.indexPriceChart?.let {
                                    val model = configureDashedLineChart(context, it)
                                    val aaOptions = model.aa_toAAOptions().apply {
                                        xAxis?.apply {
                                            crosshair(AACrosshair().width(1f))
                                            gridLineColor(AAColor.Clear)
                                                .lineColor(AAColor.Clear)
                                                .labels?.autoRotationLimit(0f)?.step(3)
                                        }
                                        yAxis?.gridLineColor(AAColor.Clear)
                                            ?.lineColor(AAColor.Clear)
                                    }
                                    view.isClearBackgroundColor = true
                                    chartsSynchronizer.views.add(view)
                                    view.callBack = chartsSynchronizer
                                    view.aa_drawChartWithChartOptions(aaOptions)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Text(
                        uiState.indexPriceChart?.graphYearFormat ?: "",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(all = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NameValue(
    name: String,
    value: String,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            name,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondary,
        )
        Text(
            value,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun configureGradientColorAreaChart(
    savingsOpportunity: UiSavingsOpportunityItem
): AAChartModel {
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
                    .data(savingsOpportunity.data.toTypedArray())
            )
        )
}

private fun configureColumnChart(suppliersChart: SuppliersCharts): AAChartModel {

    return AAChartModel()
        .chartType(AAChartType.Column)
        .colorsTheme(chartColors)
        .series(
            arrayOf(
                AASeriesElement()
                    .data(suppliersChart.data.toTypedArray())
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
        .yAxisMax(suppliersChart.maxValue?.toFloat())
        .stacking(AAChartStackingType.Normal)
        .backgroundColor("rgba(0,0,0,0)")
        .categories(suppliersChart.categories.toTypedArray())
        .dataLabelsStyle(
            AAStyle()
                .color("#1f1b1b")//Title font color
                .lineWidth(0f)
                .fontSize(11f)//Title font size
                .fontWeight(AAChartFontWeightType.Bold)//Title font weight
                .textOutline("0px 0px contrast")
        )
}

private fun configureMultiLineChart(
    context: Context,
    vendorRelationMetrics: Map<String, List<Double>>
): AAChartModel {

    val chartData = mutableListOf<AASeriesElement>()
    vendorRelationMetrics.values.forEachIndexed { index, d ->
        val element = AASeriesElement()
            .color(chartColors[index])
            .lineWidth(2f)
            .data(d.toTypedArray())
        chartData.add(element)
    }

    val aaChartModel = AAChartModel.Builder(context)
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
    aaChartModel.series(chartData.toTypedArray())
    return aaChartModel
}

private fun configureDashedLineChart(
    context: Context,
    indexPriceChart: IndexPriceCharts
): AAChartModel {
    val aaChartModel = AAChartModel.Builder(context)
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
        .setYAxisMax(indexPriceChart.maxValue.toFloat())
        .setCategories(*indexPriceChart.categories.toTypedArray())
        .setCategories(*arrayOf())
        .setYAxisTitle("")
        .setAxesTextColor("#AAAEB5")
        .build()

    val element = AASeriesElement()
        .color("#008066")
        .name("")
        .lineWidth(2f)
        .data(indexPriceChart.data.toTypedArray())
        .dashStyle(AAChartLineDashStyleType.ShortDash)

    aaChartModel
        .series(arrayOf(element))
    return aaChartModel
}

private class ChartsSynchronizer(
    val update: (Int) -> Unit
) : AAChartView.AAChartViewCallBack {

    val views: MutableSet<AAChartView> = mutableSetOf()
    var index = -1

    override fun chartViewDidFinishLoad(aaChartView: AAChartView) {}

    override fun chartViewMoveOverEventMessage(
        aaChartView: AAChartView,
        messageModel: AAMoveOverEventMessageModel
    ) {
        messageModel.index?.let {
            if (index != it) {
                index = it
                update(index)
                val addPlotLine1 = "aaGlobalChart.series[0].points[$index].onMouseOver()"
                aaChartView.post {
                    views.forEach { view ->
                        if (aaChartView != view) {
                            view.aa_evaluateTheJavaScriptStringFunction(addPlotLine1)
                        }
                    }
                }
            }
        }
    }
}
