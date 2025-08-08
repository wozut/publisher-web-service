package tcla.contexts.tcla.core.domain.timezone

fun isValidTimeZone(timeZone: String): Boolean = getTimeZones().contains(element = timeZone)
