package tcla.contexts.tcla.core.domain.checkwhetherorganizationcanhaveonemoreteam

import arrow.core.right
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.RequestIsAuthenticatedRule
import tcla.contexts.tcla.core.domain.organization.OrganizationMother
import tcla.contexts.tcla.core.domain.organization.OrganizationRepository
import tcla.contexts.tcla.core.domain.organization.model.MaximumAmountOfTeams
import tcla.contexts.tcla.core.domain.organization.rule.RequesterOwnsOrganizationRule
import tcla.contexts.tcla.core.domain.team.TeamFilterKey
import tcla.contexts.tcla.core.domain.team.TeamRepository
import tcla.contexts.tcla.core.domain.team.model.Team
import tcla.contexts.tcla.core.domain.team.model.TeamMother
import tcla.libraries.search.OneValueFilter
import tcla.libraries.transactional.FakeTransactionExecutor


class CheckWhetherOrganizationCanHaveOneMoreTeamTest {
    private val teamRepository: TeamRepository = mockk<TeamRepository>()
    private val organizationRepository: OrganizationRepository = mockk<OrganizationRepository>()
    private val requestIsAuthenticatedRule: RequestIsAuthenticatedRule = mockk<RequestIsAuthenticatedRule>()
    private val requesterOwnsOrganizationRule: RequesterOwnsOrganizationRule = mockk<RequesterOwnsOrganizationRule>()

    private val checkWhetherOrganizationCanHaveOneMoreTeam: CheckWhetherOrganizationCanHaveOneMoreTeam = CheckWhetherOrganizationCanHaveOneMoreTeam(
        FakeTransactionExecutor(),
        organizationRepository,
        teamRepository
    )
    private val requesterId = "1234"

    @Test
    fun `A team can be created if the organization has not reached the maximum amount of teams`() {
        val organization = OrganizationMother.default(maximumAmountOfTeams = MaximumAmountOfTeams(1))

        every { requestIsAuthenticatedRule.ensure() }.returns(requesterId.right())
        every { requesterOwnsOrganizationRule.ensure(organization.id) }.returns(organization.id.right())
        every { teamRepository.search(OneValueFilter(TeamFilterKey.ORGANIZATION, organization.id)) }
            .returns(emptyList<Team>().right())
        every { organizationRepository.find(organization.id) }.returns(organization.right())

        checkWhetherOrganizationCanHaveOneMoreTeam.execute(organization.id)
            .fold(
                { Assertions.fail("It must be right") },
                { teamCanBeCreated -> assertThat(teamCanBeCreated).isTrue() }
            )
    }

    @Test
    fun `A team cannot be created if the organization has reached the maximum amount of teams`() {
        val organization = OrganizationMother.default(maximumAmountOfTeams = MaximumAmountOfTeams(1))

        every { requestIsAuthenticatedRule.ensure() }.returns(requesterId.right())
        every { requesterOwnsOrganizationRule.ensure(organization.id) }.returns(organization.id.right())
        every { teamRepository.search(OneValueFilter(TeamFilterKey.ORGANIZATION, organization.id)) }
            .returns(listOf(TeamMother.default()).right())
        every { organizationRepository.find(organization.id) }.returns(organization.right())

        checkWhetherOrganizationCanHaveOneMoreTeam.execute(organization.id)
            .fold(
                { Assertions.fail("It must be right") },
                { teamCanBeCreated -> assertThat(teamCanBeCreated).isFalse() }
            )
    }
}