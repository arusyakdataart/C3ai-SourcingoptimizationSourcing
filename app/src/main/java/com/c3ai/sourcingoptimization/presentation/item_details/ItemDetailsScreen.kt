package com.c3ai.sourcingoptimization.presentation.item_details

import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.presentation.item_details.overview.ItemDetailsUiState
import com.c3ai.sourcingoptimization.presentation.item_details.overview.PreviewItemDetailsUiState
import com.c3ai.sourcingoptimization.presentation.views.SuppliersChart
import com.c3ai.sourcingoptimization.presentation.views.UiSavingsOpportunityItem
import com.c3ai.sourcingoptimization.ui.theme.*
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AACrosshair
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import kotlinx.coroutines.runBlocking

private val chartColors: Array<Any> = arrayOf("#82B0FF", "#C799FF", "#F2950A", "#49BFA9", "#A7ADC4")

/**
 * A display of the item details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [ItemDetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [ItemDetailsDataScreen].
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ItemDetailsScreen(
    scaffoldState: ScaffoldState,
    itemId: String,
    uiState: ItemDetailsUiState,
    onRefreshDetails: () -> Unit,
    onTabItemClick: (Int) -> Unit,
    onBackButtonClick: () -> Unit,
    loadData: () -> Unit,
    onAlertsClick: (String) -> Unit,
    onDateRangeSelected: (Int) -> Unit,
    onStatsTypeSelected: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var phoneNumber: String by remember {
        mutableStateOf("")
    }

    var emailAddress: String by remember {
        mutableStateOf("")
    }

    LaunchedEffect(itemId) {
        loadData()
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            ContactSupplierBottomSheetContent(phoneNumber, emailAddress)
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.item_, itemId),
                    onBackButtonClick = onBackButtonClick,
                )
            },
            snackbarHost = { C3SnackbarHost(hostState = it) },
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)

            LoadingContent(
                empty = when (uiState) {
                    is ItemDetailsUiState.NoItem -> uiState.isLoading
                    else -> false
                },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefreshDetails,
                content = {
                    when (uiState) {
                        is ItemDetailsUiState.HasItem -> {
                            Column {
                                Tabs(
                                    selectedTab = uiState.tabIndex,
                                    TabItem(stringResource(R.string.overview)) {
                                        onTabItemClick(0)
                                    },
                                    TabItem(stringResource(R.string.po_lines)) {
                                        onTabItemClick(1)
                                    },
                                    TabItem(stringResource(R.string.suppliers)) {
                                        onTabItemClick(2)
                                    }
                                )
                                when (uiState.tabIndex) {
                                    0 -> {
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
                                            )
                                        }

                                    }
                                    1 -> {

                                    }
                                    2 -> {

                                    }
                                }
                            }
                        }
                        is ItemDetailsUiState.NoItem -> {
//                            if (uiState.errorMessages.isEmpty()) {
//                                // if there are no posts, and no error, let the user refresh manually
//                                PButton(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    text = stringResource(id = R.string.tap_to_load_content),
//                                    onClick = onRefreshDetails,
//                                )
//                            } else {
//                                // there's currently an error showing, don't show any content
//                                Box(contentModifier.fillMaxSize()) { /* empty screen */ }
//                            }
                        }
                    }
                }
            )
        }
    }

    // Process one error message at a time and show them as Snackbars in the UI
//    if (uiState.errorMessages.isNotEmpty()) {
//        // Remember the errorMessage to display on the screen
//        val errorMessage = remember(uiState) { uiState.errorMessages[0] }
//
//        // Get the text to show on the message from resources
//        val errorMessageText: String = stringResource(errorMessage.messageId)
//        val retryMessageText = stringResource(id = R.string.retry)
//
//        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
//        // don't restart the effect and use the latest lambda values.
//        val onRefreshPostsState by rememberUpdatedState({ })
//        val onErrorDismissState by rememberUpdatedState({ })
//
//        // Effect running in a coroutine that displays the Snackbar on the screen
//        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
//        // the previous effect will be cancelled and a new one will start with the new values
//        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
//            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
//                message = errorMessageText,
//                actionLabel = retryMessageText
//            )
//            if (snackbarResult == SnackbarResult.ActionPerformed) {
//                onRefreshPostsState()
//            }
//            // Once the message is displayed and dismissed, notify the ViewModel
//            onErrorDismissState()
//        }
//    }
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
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                },
                update = { view ->
                    savingsOpportunity?.let {
                        view.aa_drawChartWithChartModel(
                            configureGradientColorAreaChart(it)
                        )
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
                supplierMenu,
                divider2,
                indexTitle,
                indexMenu,
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
                onItemSelectedListener = { position, _ -> onStatsTypeSelected(position) },
                modifier = Modifier
                    .constrainAs(dateRange) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            )
            CardMenu(
                items = stringArrayResource(R.array.sourcingAnalysisStatsType).asList(),
                onItemSelectedListener = { position, _ -> onDateRangeSelected(position) },
                modifier = Modifier
                    .constrainAs(statsMenu) {
                        top.linkTo(dateRange.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    }
            )
            AndroidView(
                factory = { ctx ->
                    AAChartView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                },
                update = { view ->
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
                        end.linkTo(supplierMenu.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            CardMenu(
                items = stringArrayResource(R.array.sourcingAnalysisStatsType).asList(),
                onItemSelectedListener = { position, _ -> onDateRangeSelected(position) },
                modifier = Modifier
                    .constrainAs(supplierMenu) {
                        top.linkTo(divider.bottom)
                        end.linkTo(parent.end)
                    }
            )
            ListDivider(Modifier.constrainAs(divider2) { top.linkTo(supplierTitle.bottom) })
            Text(
                stringResource(R.string.index),
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .constrainAs(indexTitle) {
                        top.linkTo(divider2.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(indexMenu.start, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            CardMenu(
                items = stringArrayResource(R.array.sourcingAnalysisStatsType).asList(),
                onItemSelectedListener = { position, _ -> onDateRangeSelected(position) },
                modifier = Modifier
                    .constrainAs(indexMenu) {
                        top.linkTo(divider2.bottom)
                        end.linkTo(parent.end)
                    }
            )
            ListDivider(Modifier.constrainAs(divider3) { top.linkTo(indexTitle.bottom) })
            C3SimpleCard(
                backgroundColor = CardBackgroundSecondary,
                border = BorderStroke(1.dp, CardBackgroundSecondary),
                modifier = Modifier
                    .constrainAs(bottomGraphCard) { top.linkTo(divider3.bottom) }
            ) {
                Column {
                    Text(
                        stringResource(R.string.supplier_avg_price_symbol, "$"),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)
                    )
                    AndroidView(
                        factory = { ctx ->
                            AAChartView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            }
                        },
                        update = { view ->
                            uiState.vendorRelationMetrics?.let {
                                val model = configureMultiLineChart(context, it)
                                val aaOptions = model.aa_toAAOptions()
                                aaOptions.xAxis?.crosshair(AACrosshair().width(1f))
                                aaOptions.yAxis?.apply {
                                    lineWidth(0f)
                                    gridLineColor(AAColor.Clear)
                                    lineColor(AAColor.Clear)
                                }
                                view.isClearBackgroundColor = true
//                            view.callBack = ChartCallback()
                                view.aa_drawChartWithChartOptions(aaOptions)
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
                                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            }
                        },
                        update = { view ->
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
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

private fun configureColumnChart(suppliersChart: SuppliersChart): AAChartModel {

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
        .yAxisMax(suppliersChart.suppliersChartDataMaxValue?.toFloat())
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

/**
 * TopAppBar for the suppliers details screen[ItemDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopAppBar(
    title: String,
    onBackButtonClick: () -> Unit,
) {
    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ItemDetailsPreview() {
    val item = runBlocking {
        (C3MockRepositoryImpl().getItemDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        ItemDetailsScreen(
            scaffoldState = rememberScaffoldState(),
            itemId = item.id,
            uiState = PreviewItemDetailsUiState(item),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onAlertsClick = {},
            onDateRangeSelected = {},
            onStatsTypeSelected = {},
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ItemDetailsPOLinesTabPreview() {
    val item = runBlocking {
        (C3MockRepositoryImpl().getItemDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        ItemDetailsScreen(
            scaffoldState = rememberScaffoldState(),
            itemId = item.id,
            uiState = PreviewItemDetailsUiState(item, 1),
            onRefreshDetails = {},
            onTabItemClick = {},
            onBackButtonClick = {},
            loadData = {},
            onAlertsClick = {},
            onDateRangeSelected = {},
            onStatsTypeSelected = {},
        )
    }
}