package com.c3ai.sourcingoptimization.data.network.converters

import com.c3ai.sourcingoptimization.domain.model.*
import com.google.gson.*
import java.lang.reflect.Type

/**
 * Gson deserializer[JsonDeserializer] for parsing a data when received
 * search results from the server
 * @see C3ApiService
 * */
class C3SearchItemJsonDeserializer : JsonDeserializer<SearchItem> {

    private val gson = GsonBuilder().create()

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SearchItem {
        return when (json.asJsonObject.get("type").asString) {
            "Item" -> gson.fromJson(json, ItemSearchItem::class.java)
            "Vendor" -> gson.fromJson(json, SupplierSearchItem::class.java)
            "SoAlertHelper" -> gson.fromJson(json, AlertSearchItem::class.java)
            "PurchaseOrder" -> gson.fromJson(json, POSearchItem::class.java)
            "PurchaseOrderLine" -> gson.fromJson(json, POLSearchItem::class.java)
            else -> UnknownSearchItem()
        }
    }
}