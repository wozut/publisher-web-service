package tcla.contexts.tcla.webapi.springweb.tcldriverscore.search

import arrow.core.right
import com.google.gson.JsonParser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tcla.contexts.tcla.core.application.tcldriverscore.search.SearchTclDriverScoresQueryHandler
import tcla.contexts.tcla.core.application.tcldriverscore.search.SearchTclDriverScoresSuccess
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScoreMother
import java.util.UUID


@WebMvcTest(controllers = [SearchTclDriverScoresController::class])
@ContextConfiguration(classes = [SearchTclDriverScoresController::class])
class SearchTclDriverScoresControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var searchTclDriverScoresQueryHandler: SearchTclDriverScoresQueryHandler

    @Test
    fun `search by assessment`() {
        val assessmentUuid = UUID.randomUUID()
        val tclDriverScore1 = TclDriverScoreMother.default()
        val tclDriverScore2 = TclDriverScoreMother.default()
        val tclDriverScores = listOf(tclDriverScore1, tclDriverScore2)

        every { searchTclDriverScoresQueryHandler.execute(any()) } returns SearchTclDriverScoresSuccess(tclDriverScores = tclDriverScores).right()

        val result = mockMvc.get("/tcl-driver-scores") {
            param("filter[assessment]", assessmentUuid.toString())
        }.andExpect {
            status { isOk() }
            content {
                contentType("application/vnd.api+json")
            }
        }.andReturn()

        val expectedJsonAsString = """
                    {
                        "data": [
                            {
                                "id": "${tclDriverScore1.id.value}",
                                "type": "tcl-driver-score",
                                "attributes": {
                                    "value": ${tclDriverScore1.value.value},
                                    "tclDriverId": "${tclDriverScore1.tclDriverId.value}",
                                    "assessmentId": "${tclDriverScore1.assessmentId.uuid}"
                                }
                            },
                            {
                                "id": "${tclDriverScore2.id.value}",
                                "type": "tcl-driver-score",
                                "attributes": {
                                    "value": ${tclDriverScore2.value.value},
                                    "tclDriverId": "${tclDriverScore2.tclDriverId.value}",
                                    "assessmentId": "${tclDriverScore2.assessmentId.uuid}"
                                }
                            }
                        ]
                    }
                """.trimIndent()

        val actualBody = JsonParser.parseString(result.response.contentAsString)
        val expectedBody = JsonParser.parseString(expectedJsonAsString)
        assertThat(actualBody).isEqualTo(expectedBody)
    }
}
