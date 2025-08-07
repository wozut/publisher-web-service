package tcla.contexts.tcla.core.domain.tcldriverscore.model

import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId

data class TclDriverScore(
    val id: TclDriverScoreId,
    val tclDriverId: TclDriverId,
    val assessmentId: AssessmentId,
    val value: TclDriverScoreValue
) : Comparable<TclDriverScore> {
    override fun compareTo(other: TclDriverScore): Int = value.compareTo(other.value)
}
