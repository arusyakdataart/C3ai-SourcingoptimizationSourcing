package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Supplier
import com.c3ai.sourcingoptimization.domain.model.POLine
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the Home route.
 *
 * This is derived from [SupplierDetailsUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface SupplierDetailsUiState {

    val isLoading: Boolean
    val searchInput: String

    /**
     * There are no details to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoDetails(
        override val isLoading: Boolean,
        override val searchInput: String
    ) : SupplierDetailsUiState

    /**
     * There are details to render, as contained in [supplier].
     *
     */
    data class HasDetails(
        val supplier: C3Supplier,
        val poLines: List<POLine> = emptyList(),
        val items: List<C3Item> = emptyList(),
        override val isLoading: Boolean,
        override val searchInput: String
    ) : SupplierDetailsUiState
}

/**
 * An internal representation of the SupplierDetails route state, in a raw form
 */
private data class SupplierDetailsViewModelState(
    val supplier: C3Supplier? = null,
    val poLines: List<POLine> = emptyList(),
    val items: List<C3Item> = emptyList(),
    val isLoading: Boolean = false,
    val searchInput: String = "",
) {

    /**
     * Converts this [SupplierDetailsViewModelState] into
     * a more strongly typed [SupplierDetailsUiState] for driving the ui.
     */
    fun toUiState(): SupplierDetailsUiState =
        if (supplier == null) {
            SupplierDetailsUiState.NoDetails(
                isLoading = isLoading,
                searchInput = searchInput
            )
        } else {
            SupplierDetailsUiState.HasDetails(
                supplier = supplier,
                isLoading = isLoading,
                searchInput = searchInput
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@HiltViewModel
class SuppliersDetailsViewModel(
    private val useCases: SuppliersDetailsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SupplierDetailsViewModelState(isLoading = true))

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refresh()

        // Observe for favorite changes in the repo layer
//        viewModelScope.launch {
//            postsRepository.observeFavorites().collect { favorites ->
//                viewModelState.update { it.copy(favorites = favorites) }
//            }
//        }
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refresh() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = useCases.getSuppliedItems("")
//            viewModelState.update {
//                when (result) {
//                    is Result.Success -> it.copy(postsFeed = result.data, isLoading = false)
//                    is Result.Error -> {
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
}