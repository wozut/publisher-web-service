package tcla.contexts.tcla.webapi.springweb.organization.create

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
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationCommand
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationCommandHandler
import tcla.contexts.tcla.core.application.organization.create.CreateOrganizationSuccess
import tcla.contexts.tcla.core.domain.organization.OrganizationMother
import tcla.contexts.tcla.core.domain.organization.model.OrganizationId

@WebMvcTest(controllers = [CreateOrganizationControllerTest::class])
@ContextConfiguration(classes = [CreateOrganizationController::class])
class CreateOrganizationControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var createOrganizationCommandHandler: CreateOrganizationCommandHandler

    @Test
    fun `create an organization with nullable name, industry and size`() {

        val createOrganizationSuccess = CreateOrganizationSuccess(organizationId).right()
        every {
            val createOrganizationCommand = any<CreateOrganizationCommand>()
            createOrganizationCommandHandler.execute(createOrganizationCommand)
        } returns createOrganizationSuccess

        mockMvc.post("/organizations") {
            contentType = mediaType
            content = createOrganizationRequestBody
        }.andExpect {
            status { isCreated() }
            header { string("Location", "/$organizationId") }
            content {
                contentType(mediaType)
            }
        }

    }

    companion object {
        private val organizationId: OrganizationId = OrganizationMother.id()
        val mediaType = MediaType("application", "vnd.api+json")
        val createOrganizationRequestBody = """
            {
                "data": {
                    "type": "organization",
                    "attributes": {
                        "name": "ACME",
                        "industry": "Software Engineering",
                        "size": "1-10 employees"
                    }
                }
            }
        """.trimIndent()
    }
}
