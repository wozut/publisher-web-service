package tcla.contexts.tcla.core.application.timezone.search

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.timezone.getTimeZones

@Named
class SearchTimeZonesQueryHandler {
    fun execute(query: SearchTimeZonesQuery): SearchTimeZonesSuccess =
        SearchTimeZonesSuccess(getTimeZones(sorted = true))
}
