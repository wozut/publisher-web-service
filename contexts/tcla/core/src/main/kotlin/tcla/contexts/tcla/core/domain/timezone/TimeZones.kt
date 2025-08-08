package tcla.contexts.tcla.core.domain.timezone

import java.time.ZoneId

fun getTimeZones(sorted: Boolean = false): List<String> =
    ZoneId.getAvailableZoneIds().toList()
        .let { zoneIds ->
            when (sorted) {
                true -> zoneIds.sorted()
                false -> zoneIds
            }
        }
