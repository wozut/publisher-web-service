package tcla.contexts.tcla.webapi.springweb.team.delete

import arrow.core.right
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.ResponseEntity.noContent
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import tcla.contexts.tcla.core.application.team.delete.DeleteTeamCommand
import tcla.contexts.tcla.core.application.team.delete.DeleteTeamCommandHandler
import tcla.contexts.tcla.core.application.team.delete.DeleteTeamSuccess
import java.util.*


@WebMvcTest(controllers = [DeleteTeamController::class])
@ContextConfiguration(classes = [DeleteTeamController::class])
class DeleteTeamControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var deleteTeamCommandHandler: DeleteTeamCommandHandler

    @Test
    fun `delete a team`() {
        val teamId = UUID.randomUUID().toString()
        val deleteTeamSuccess = DeleteTeamSuccess.right()
        every {
            val deleteTeamCommand = any<DeleteTeamCommand>()
            deleteTeamCommandHandler.execute(deleteTeamCommand)
        } returns deleteTeamSuccess

        mockMvc.delete("/teams/{id}", teamId)
            .andExpect {
                status { noContent() }
            }
    }
}