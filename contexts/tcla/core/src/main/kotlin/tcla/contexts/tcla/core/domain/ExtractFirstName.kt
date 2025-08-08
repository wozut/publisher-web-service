package tcla.contexts.tcla.core.domain

fun extractFirstName(name: String): String =
    when {
        name.isEmpty() -> name
        else -> name.split(" ").first()
    }
