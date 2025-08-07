package tcla.contexts.tcla.typeform

import arrow.core.flatMap
import arrow.core.right
import com.google.gson.JsonParser
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnaire.create.buildTclQuestionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import java.time.Duration
import java.time.Instant
import java.util.UUID

class BuildCreateExternalQuestionnaireRequestBodyTest {
    @Test
    fun `json is valid`() {
        ResponseAcceptanceInterval(Instant.now().plus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(7)))
            .flatMap { interval ->
                val actualJson = BuildCreateExternalQuestionnaireRequestBody()
                    .build(
                        workspace = "workspace",
                        publish = false,
                        showTypeformBranding = false,
                        survey = buildTclQuestionnaire(
                            questionnaireId = QuestionnaireId(UUID.randomUUID()),
                            externalQuestionnaireId = null,
                            externalQuestionnaireIsPublic = false,
                            assessmentId = AssessmentId(UUID.randomUUID()),
                            interval = interval,
                            maximumAmountToBeCollected = 5,
                            includeGenderQuestion = true,
                            includeWorkFamiliarityQuestion = true,
                            includeTeamFamiliarityQuestion = true,
                            includeModeOfWorkingQuestion = true
                        )
                    )

                val throwable = Assertions.catchThrowable { JsonParser.parseString(actualJson) }
                throwable.right()
            }.fold(
                { Assertions.fail(it.toString()) },
                { throwable -> assertThat(throwable).isNull() }
            )
    }
}
