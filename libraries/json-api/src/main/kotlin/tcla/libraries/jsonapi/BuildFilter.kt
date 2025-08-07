package tcla.libraries.jsonapi

import tcla.libraries.search.ManyValuesFilter

fun buildFilter(key: String, value: String?): ManyValuesFilter<String, String>? =
    when (value) {
        null -> null
        else -> buildNonNullFilter(key, value)
    }
