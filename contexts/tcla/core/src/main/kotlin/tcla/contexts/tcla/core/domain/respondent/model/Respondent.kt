package tcla.contexts.tcla.core.domain.respondent.model

import tcla.contexts.tcla.core.domain.Email
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId

data class Respondent(
    val id: RespondentId,
    val name: String,
    val email: Email,
    val assessmentId: AssessmentId
)
