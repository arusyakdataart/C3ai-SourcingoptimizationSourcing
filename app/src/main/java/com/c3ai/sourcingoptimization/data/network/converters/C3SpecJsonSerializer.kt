package com.c3ai.sourcingoptimization.data.network.converters

import com.c3ai.sourcingoptimization.data.network.requests.C3Spec
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Gson serializer[JsonSerializer] for request's parameters transforms a data when send requests
 * to the server
 * @see C3ApiService
 * */
class C3SpecJsonSerializer : JsonSerializer<C3Spec> {

    override fun serialize(
        src: C3Spec,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonObject().apply {
            addProperty("include", src.include?.joinToString(","))
            addProperty("filter", src.filter)
            addProperty("limit", src.limit)
            addProperty("offset", src.offset)
            addProperty("order", src.order)
        }
    }
}