package tcla.contexts.tcla.core.domain.tcldriverscore.model

import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentMother
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverIdMother
import kotlin.random.Random

object TclDriverScoreMother {
    fun default(
        id: TclDriverScoreId = TclDriverScoreIdMother.default(),
        tclDriverId: TclDriverId = TclDriverIdMother.default(),
        assessmentId: AssessmentId = AssessmentMother.id(),
        value: TclDriverScoreValue = TclDriverScoreValueMother.default()
    ): TclDriverScore = TclDriverScore(
        id = id,
        tclDriverId = tclDriverId,
        assessmentId = assessmentId,
        value = value
    )

    fun value(
        value: Double = Random.nextDouble(1.00, 5.00)
    ): TclDriverScoreValue = TclDriverScoreValueMother.default(value)
}
