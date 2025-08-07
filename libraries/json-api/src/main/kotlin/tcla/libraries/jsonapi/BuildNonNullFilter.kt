package tcla.libraries.jsonapi

import tcla.libraries.search.ManyValuesFilter

fun buildNonNullFilter(key: String, filterValue: String): ManyValuesFilter<String, String> =
    ManyValuesFilter(key = key, splitFilterValue(values = filterValue))
