package tcla.contexts.tcla.webapi.springweb.report.jsonapi

import tcla.contexts.tcla.core.domain.report.model.Dictionary

fun Dictionary.toResource(): List<EntryResource> =
    entries.map { EntryResource(id = it.id, it.term, it.definition) }
