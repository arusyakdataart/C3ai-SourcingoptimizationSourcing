package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.model.AlertFeedback
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.AlertsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.UiAlert
import com.c3ai.sourcingoptimization.presentation.views.convert
import com.c3ai.sourcingoptimization.presentation.views.filterByCategory
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Alerts route.
 *
 * This is derived from [AlertsUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface AlertsUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There is no data to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : AlertsUiState

    /**
     * There is data to render, as contained in model[alerts].
     *
     */
    data class HasData(
        val alerts: List<UiAlert>,
        val filteredAlerts: List<UiAlert>,
        val collapsedListItemIds: Set<String> = emptySet(),
        val selectedCategoriesList: Set<String> = emptySet(),
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : AlertsUiState
}

/**
 * An internal representation of the Alerts route state, in a raw form
 */
private data class AlertsViewModelState(
    override val settings: C3AppSettingsProvider,
    val alerts: List<Alert>? = null,
    val alertsFeedBacks: List<AlertFeedback>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val collapsedListItemIds: Set<String> = emptySet(),
    val selectedCategoriesList: Set<String> = emptySet()
) : ViewModelState() {

    /**
     * Converts this [AlertsViewModelState] into
     * a more strongly typed [AlertsUiState] for driving the ui.
     */
    fun toUiState(): AlertsUiState =
        if (alerts != null && alertsFeedBacks != null) {
            val uiAlert = convert(alerts, alertsFeedBacks)
            AlertsUiState.HasData(
                alerts = uiAlert,
                collapsedListItemIds = collapsedListItemIds,
                selectedCategoriesList = selectedCategoriesList,
                filteredAlerts =filterByCategory(uiAlert, selectedCategoriesList),
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            AlertsUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class AlertsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val useCases: AlertsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        AlertsViewModelState(
            settings = settings,
            isLoading = true
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshDetails()
    }

    /**
     * Refresh alerts data and update the UI state accordingly
     */
    fun refreshDetails(sortOrder: String = "") {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = useCases.getAlerts(sortOrder)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> {
                        getFeedbacks(result.data.map { it.id })
                        it.copy(alerts = result.data)
                    }
                    is C3Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    private fun getFeedbacks(alertIds: List<String>) {
        viewModelScope.launch {
            val result = useCases.getAlertsFeedbacks(alertIds)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(alertsFeedBacks = result.data, isLoading = false)
                    is C3Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    fun updateAlerts(alertIds: List<String>, statusType: String, statusValue: Boolean) {
        viewModelScope.launch {
            val result = useCases.updateAlerts(alertIds, statusType, statusValue)
//            viewModelState.update {
//                when (result) {
//                    is C3Result.Success -> it.copy(alertsFeedBacks = result.data, isLoading = false)
//                    is C3Result.Error -> {
//                        val errorMessages = it.errorMessages + ErrorMessage(
//                            id = UUID.randomUUID().mostSignificantBits,
//                            messageId = R.string.load_error
//                        )
//                        it.copy(errorMessages = errorMessages, isLoading = false)
//                    }
//                }
//            }
        }
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: AlertsEvent) {
        viewModelState.update { state ->
            when (event) {
                is AlertsEvent.OnSearchInputChanged -> {
                    state.copy(searchInput = event.searchInput)
                }
                is AlertsEvent.OnCollapsableItemClick -> {
                    state.copy(
                        collapsedListItemIds = state.collapsedListItemIds.toMutableSet().apply {
                            val isRemoved = remove(event.id)
                            isRemoved || add((event.id))
                        })
                }
                is AlertsEvent.OnFilterChanged -> {
                    state.copy(selectedCategoriesList = event.categories.toMutableSet())
                }
                else -> {
                    state.copy()
                }
            }
        }

        when (event) {
            is AlertsEvent.OnSortChanged -> {
                refreshDetails(event.sortOption)
            }
        }
    }
}