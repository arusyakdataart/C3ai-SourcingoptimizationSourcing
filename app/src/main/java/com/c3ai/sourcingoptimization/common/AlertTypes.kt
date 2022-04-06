package com.c3ai.sourcingoptimization.common

enum class AlertTypes(var categoryName: String) {

    NEW_LOWEST_PRICE("New Lowest Price"),
    UNEXPECTED_PRICE_INCREASE("Unexpected Price Increase"),
    REQUESTED_DELIVERY_DATE_CHANGE("Requested Delivery Date Change"),
    SHORT_CYCLED_PURCHASE_ORDER("Short-cycled Purchase Order"),
    INDEX_PRICE_CHANGE("Index Price Change"),
    CORRELATED_INDEX_PRICING_ANOMALY("Correlated Index Pricing Anomaly"),
    D_U_N_S_RISK("D-U-N-S Risk"),
    RAPID_RATINGS_RISK("Rapid Ratings Risk");

    init {
        this.categoryName = categoryName
    }
}