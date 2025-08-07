package tcla.libraries.uuidvalidation

import java.util.UUID

fun String.isUuid(): Boolean =
    try {
        UUID.fromString(this)
        true
    } catch (exception: IllegalArgumentException) {
        false
    }
