package tcla.libraries.jsonapi

fun splitFilterValue(values: String?): List<String> =
    when (values) {
        null -> listOf()
        else -> values.split(",")
    }
