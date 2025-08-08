package tcla.contexts.tcla.webapi.springweb.timezones.search


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.timezone.search.SearchTimeZonesQuery
import tcla.contexts.tcla.core.application.timezone.search.SearchTimeZonesQueryHandler
import tcla.contexts.tcla.core.application.timezone.search.SearchTimeZonesSuccess
import tcla.contexts.tcla.webapi.springweb.timezones.search.jsonapi.TimesZonesDocument


@RestController
class SearchTimeZonesController(
    private val searchTimeZonesQueryHandler: SearchTimeZonesQueryHandler
) {

    @GetMapping("/time-zones", produces = ["application/json"])
    fun execute(): ResponseEntity<Any> =
        searchTimeZonesQueryHandler.execute(SearchTimeZonesQuery).toSuccessResponse()

    private fun SearchTimeZonesSuccess.toSuccessResponse(): ResponseEntity<Any> =
        TimesZonesDocument(timeZones = timeZones).let { ResponseEntity.ok(it) }
}
