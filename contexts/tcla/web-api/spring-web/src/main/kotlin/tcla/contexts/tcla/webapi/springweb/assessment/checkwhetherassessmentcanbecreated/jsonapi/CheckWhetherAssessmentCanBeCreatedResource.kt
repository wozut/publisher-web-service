package tcla.contexts.tcla.webapi.springweb.assessment.checkwhetherassessmentcanbecreated.jsonapi

import tcla.contexts.tcla.core.application.assessment.checkwhetherassessmentcanbecreated.CheckWhetherAssessmentCanBeCreatedSuccess
import java.util.UUID

data class CheckWhetherAssessmentCanBeCreatedResource(
    val id: String,
    val attributes: CheckWhetherAssessmentCanBeCreatedResourceAttributes,
) {
    val type: String = CHECK_WHETHER_ASSESSMENT_CAN_BE_CREATED_JSON_API_TYPE
}

fun CheckWhetherAssessmentCanBeCreatedSuccess.toResource(): CheckWhetherAssessmentCanBeCreatedResource =
    CheckWhetherAssessmentCanBeCreatedResource(
        id = UUID.randomUUID().toString(),
        attributes = CheckWhetherAssessmentCanBeCreatedResourceAttributes(
            result = this.result,
            earliestAvailableDateAfterLatestAssessment = this.earliestAvailableDateAfterLatestAssessment.toString()
        )
    )
