package tcla.contexts.tcla.webapi.springweb.teammember.delete

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
import tcla.contexts.tcla.core.application.teammember.delete.DeleteTeamMemberCommand
import tcla.contexts.tcla.core.application.teammember.delete.DeleteTeamMemberCommandHandler
import tcla.contexts.tcla.core.application.teammember.delete.DeleteTeamMemberSuccess
import java.util.*


@WebMvcTest(controllers = [DeleteTeamMemberController::class])
@ContextConfiguration(classes = [DeleteTeamMemberController::class])
class DeleteTeamMemberControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var deleteTeamMemberCommandHandler: DeleteTeamMemberCommandHandler

    @Test
    fun `delete a team member`() {
        val teamMemberId = UUID.randomUUID().toString()
        val deleteTeamMemberSuccess = DeleteTeamMemberSuccess.right()
        every {
            val deleteTeamMemberCommand = any<DeleteTeamMemberCommand>()
            deleteTeamMemberCommandHandler.execute(deleteTeamMemberCommand)
        } returns deleteTeamMemberSuccess

        mockMvc.delete("/team-members/{id}", teamMemberId)
            .andExpect {
                status { noContent() }
            }
    }
}