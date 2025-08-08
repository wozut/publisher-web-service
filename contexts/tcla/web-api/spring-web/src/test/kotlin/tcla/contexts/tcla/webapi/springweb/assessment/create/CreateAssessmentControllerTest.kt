package tcla.contexts.tcla.webapi.springweb.assessment.create

import arrow.core.right
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentCommand
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentCommandHandler
import tcla.contexts.tcla.core.application.assessment.create.CreateAssessmentSuccess
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentMother
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.contexts.tcla.core.domain.team.model.TeamMother
import tcla.libraries.time.InstantMother

@WebMvcTest(controllers = [CreateAssessmentController::class])
@ContextConfiguration(classes = [CreateAssessmentController::class])
class CreateAssessmentControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var createAssessmentCommandHandler: CreateAssessmentCommandHandler

    @Test
    fun `it creates an assessment`() {

        val createAssessmentSuccess = CreateAssessmentSuccess(assessmentId).right()
        every {
            val createAssessmentCommand = any<CreateAssessmentCommand>()
            createAssessmentCommandHandler.execute(createAssessmentCommand)
        } returns createAssessmentSuccess

        mockMvc.post("/assessments") {
            contentType = mediaType
            content = createAssessmentRequestBody
        }.andExpect {
            status { isCreated() }
            header { string("Location", "/${assessmentId.uuid}") }
            content {
                contentType(mediaType)
            }
        }

    }

    companion object {
        private val assessmentId: AssessmentId = AssessmentMother.id()
        private val teamId: TeamId = TeamMother.id()
        private val openingDate: String = InstantMother.nowMinusOneDayAsIso()
        private val closingDate: String = InstantMother.nowPlusOneDayAsIso()
        private const val TITLE: String = "My Assessment"
        val mediaType = MediaType("application", "vnd.api+json")
        val createAssessmentRequestBody = """
            {
                "data": {
                    "type": "assessment",
                    "attributes": {
                        "responseAcceptanceIntervalStartDate": "$openingDate",
                        "responseAcceptanceIntervalEndDate": "$closingDate",
                        "title": "$TITLE",
                        "teamId": "$teamId",
                        "includeQuestionsOfInterest": {
                            "gender": true,
                            "workFamiliarity": true,
                            "teamFamiliarity": true,
                            "modeOfWorking": true
                        }
                    }
                }
            }
        """.trimIndent()
    }
}
