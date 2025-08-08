package tcla.libraries.jsonapi

import tcla.libraries.search.ManyValuesFilter

fun buildFilters(keyValuePairs: List<Pair<String, String?>>): List<ManyValuesFilter<String, String>> =
    keyValuePairs.mapNotNull { keyValuePair -> buildFilter(keyValuePair.first, keyValuePair.second) }
