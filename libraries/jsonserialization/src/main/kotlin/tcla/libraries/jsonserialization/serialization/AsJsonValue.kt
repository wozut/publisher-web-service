package tcla.libraries.jsonserialization.serialization

fun <T : Any> asJsonValue(value: T?): String =
    when (value) {
        null -> "null"
        else -> when (value) {
            is String -> "\"$value\""
            is Char -> asJsonValue(value.toString())
            is Number -> "$value"
            is Boolean -> "$value"
            else -> throw IllegalArgumentException()
        }
    }
