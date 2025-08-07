package tcla.contexts.tcla.core.domain.questionnaire.create

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class BuildTclQuestionnaireTest {

    @Test
    fun `build tcl questionnaire `() {
        val teamSizes = listOf(
            Pair(3,1.0), // teamSize, expectedMinimumRateRequired
            Pair(4,1.0),
            Pair(5,1.0),
            Pair(6,0.8),
            Pair(7,0.8),
        )
        teamSizes.forEach { (teamSize, expectedMinimumRateRequired) ->
            val questionnaire = buildTclQuestionnaire(
                questionnaireId = QuestionnaireId(UUID.randomUUID()),
                externalQuestionnaireId = null,
                externalQuestionnaireIsPublic = false,
                assessmentId = AssessmentId(UUID.randomUUID()),
                interval = ResponseAcceptanceInterval(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS)).getOrNull()!!,
                maximumAmountToBeCollected = teamSize,
                includeGenderQuestion = true,
                includeWorkFamiliarityQuestion = true,
                includeTeamFamiliarityQuestion = true,
                includeModeOfWorkingQuestion = true
            )
            assertThat(questionnaire.responses.minimumRateRequired.double)
                .withFailMessage("teamSize: $teamSize, expectedMinimumRateRequired: $expectedMinimumRateRequired, actual: ${questionnaire.responses.minimumRateRequired.double}")
                .isEqualTo(expectedMinimumRateRequired)
        }
    }
}