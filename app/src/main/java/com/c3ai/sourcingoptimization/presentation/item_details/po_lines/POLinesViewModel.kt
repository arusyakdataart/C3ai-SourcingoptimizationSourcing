package com.c3ai.sourcingoptimization.presentation.item_details.po_lines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c3ai.sourcingoptimization.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class POLinesViewModel @Inject constructor() : ViewModel() {

    // TODO!!! this is a hardcoded data.
    val poLines = listOf(
        POLine(
            id = "453425",
            totalCost = 8000.0,
            numberOfActiveAlerts = 0,
            fulfilledStr = "Open",
            unitPrice = 33.5,
            totalQuantity = 238,
            orderCreationDate = "01/04/2021",
            openedDate = "-",
            closedDate = "-",
            plannedLeadTime = "21 days (Plan)",
            requestedDeliveryDate = "05/1/2021",
            promisedDeliveryDate = "12/20/2021",
            to = To(
                id = "1",
                name = "Facility Name",
                city = "San Francisco",
                geometry = "US",
                address = "123 Main St",
                address1 = "",
                postal_code = "",
                state = ""
            ),
            buyer = Buyer(
                id = "1",
                name = "Wilson Donin",
                facilityName = "Facility Name"
            ),
            vendor = Vendor(
                id = "1",
                name = "ABC Inc.",
                numberOfActiveAlerts = 0,
                location = Location(
                    address = "123 Main St.",
                    region = "",
                    city = "San Francisco",
                    state = "US"
                )
            )
        ),
        POLine(
            id = "453426",
            totalCost = 8000.0,
            numberOfActiveAlerts = 0,
            fulfilledStr = "Open",
            unitPrice = 33.5,
            totalQuantity = 238,
            orderCreationDate = "01/04/2021",
            openedDate = "-",
            closedDate = "-",
            plannedLeadTime = "21 days (Plan)",
            requestedDeliveryDate = "05/1/2021",
            promisedDeliveryDate = "12/20/2021",
            to = To(
                id = "1",
                name = "Facility Name",
                city = "San Francisco",
                geometry = "US",
                address = "123 Main St",
                address1 = "",
                postal_code = "",
                state = ""
            ),
            buyer = Buyer(
                id = "1",
                name = "Wilson Donin",
                facilityName = "Facility Name"
            ),
            vendor = Vendor(
                id = "1",
                name = "ABC Inc.",
                numberOfActiveAlerts = 0,
                location = Location(
                    address = "123 Main St.",
                    region = "",
                    city = "San Francisco",
                    state = "US"
                )
            )
        )
    )

    val observableData: LiveData<List<POLine>> = MutableLiveData(poLines)
}