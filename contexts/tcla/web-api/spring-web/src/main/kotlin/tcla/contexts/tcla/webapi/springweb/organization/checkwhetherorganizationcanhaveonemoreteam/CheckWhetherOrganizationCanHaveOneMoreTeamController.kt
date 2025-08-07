package tcla.contexts.tcla.webapi.springweb.organization.checkwhetherorganizationcanhaveonemoreteam

import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.tcla.core.application.organization.checkwhetherorganizationcanhaveonemoreteam.CheckWhetherOrganizationCanHaveOneMoreTeamQuery
import tcla.contexts.tcla.core.application.organization.checkwhetherorganizationcanhaveonemoreteam.CheckWhetherOrganizationCanHaveOneMoreTeamQueryHandler
import tcla.contexts.tcla.webapi.springweb.jsonapi.toFailureResponse
import tcla.contexts.tcla.webapi.springweb.organization.checkwhetherorganizationcanhaveonemoreteam.jsonapi.CheckWhetherOrganizationCanHaveOneMoreTeamDocument
import tcla.contexts.tcla.webapi.springweb.organization.checkwhetherorganizationcanhaveonemoreteam.jsonapi.toResource

@RestController
class CheckWhetherOrganizationCanHaveOneMoreTeamController(
    private val checkWhetherOrganizationCanHaveOneMoreTeamQueryHandler: CheckWhetherOrganizationCanHaveOneMoreTeamQueryHandler
) {
    @GetMapping("/check-whether-organization-can-have-one-more-team/{organizationId}", produces = ["application/vnd.api+json"])
    fun execute(
        @PathVariable("organizationId") organizationId: String
    ): ResponseEntity<Any> =
        checkWhetherOrganizationCanHaveOneMoreTeamQueryHandler.execute(
            CheckWhetherOrganizationCanHaveOneMoreTeamQuery(organizationId)
        )
            .flatMap { querySuccess -> querySuccess.result.right() }
            .fold({ it.toFailureResponse() }, { it.toSuccessResponse() })

    private fun Boolean.toSuccessResponse(): ResponseEntity<Any> =
        CheckWhetherOrganizationCanHaveOneMoreTeamDocument(data = this.toResource()).let { ResponseEntity.ok(it) }
}
