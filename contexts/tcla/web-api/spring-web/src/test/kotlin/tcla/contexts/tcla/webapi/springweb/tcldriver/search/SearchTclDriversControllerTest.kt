package tcla.contexts.tcla.webapi.springweb.tcldriver.search

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
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversQuery
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversQueryHandler
import tcla.contexts.tcla.core.application.tcldriver.search.SearchTclDriversSuccess
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverMother


@WebMvcTest(controllers = [SearchTclDriversController::class])
@ContextConfiguration(classes = [SearchTclDriversController::class])
class SearchTclDriversControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var searchTclDriversQueryHandler: SearchTclDriversQueryHandler

    @Test
    fun `search all`() {
        val tclDriver1 = TclDriverMother.default()
        val tclDriver2 = TclDriverMother.default(parent = null)
        val tclDrivers = listOf(tclDriver1, tclDriver2)

        every { searchTclDriversQueryHandler.execute(query = SearchTclDriversQuery()) } returns SearchTclDriversSuccess(tclDrivers).right()

        val result = mockMvc
            .get("/tcl-drivers")
            .andExpect {
                status { isOk() }
                content {
                    contentType("application/vnd.api+json")
                }
            }.andReturn()

        val expectedJsonAsString = """
                    {
                        "data": [
                            {
                                "id": "${tclDriver1.id.value}",
                                "type": "tcl-driver",
                                "attributes": {
                                    "name": "${tclDriver1.name.value}",
                                    "children": [${tclDriver1.children.joinToString(separator = ",") { tclDriverId -> "${tclDriverId.value}" }}],
                                    "parent": "${tclDriver1.parent!!.value}"
                                }
                            },
                            {
                                "id": "${tclDriver2.id.value}",
                                "type": "tcl-driver",
                                "attributes": {
                                    "name": "${tclDriver2.name.value}",
                                    "children": [${tclDriver2.children.joinToString(separator = ",") { tclDriverId -> "${tclDriverId.value}" }}],
                                    "parent": null
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
