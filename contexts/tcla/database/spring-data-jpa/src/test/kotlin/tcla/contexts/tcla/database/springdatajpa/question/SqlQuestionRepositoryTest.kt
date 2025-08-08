package tcla.contexts.tcla.database.springdatajpa.question

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import tcla.contexts.tcla.core.domain.answeroption.AnswerOptionMother
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentMother
import tcla.contexts.tcla.core.domain.organization.OrganizationMother
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestion
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestionMother
import tcla.contexts.tcla.core.domain.questionnaire.model.SurveyMother
import tcla.contexts.tcla.core.domain.team.model.TeamMother
import tcla.contexts.tcla.database.springdatajpa.answeroption.JpaAnswerOptionRepository
import tcla.contexts.tcla.database.springdatajpa.answeroption.toJpa
import tcla.contexts.tcla.database.springdatajpa.assessment.JpaAssessmentRepository
import tcla.contexts.tcla.database.springdatajpa.assessment.toJpa
import tcla.contexts.tcla.database.springdatajpa.organization.JpaOrganizationRepository
import tcla.contexts.tcla.database.springdatajpa.organization.toJpa
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaireRepository
import tcla.contexts.tcla.database.springdatajpa.questionnaire.toJpa
import tcla.contexts.tcla.database.springdatajpa.team.JpaTeamRepository
import tcla.contexts.tcla.database.springdatajpa.team.toJpa

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SqlQuestionRepositoryTest(
    @Autowired private val jpaTeamRepository: JpaTeamRepository,
    @Autowired private val jpaQuestionRepository: JpaQuestionRepository,
    @Autowired private val jpaQuestionnaireRepository: JpaQuestionnaireRepository,
    @Autowired private val jpaAssessmentRepository: JpaAssessmentRepository,
    @Autowired private val jpaAnswerOptionRepository: JpaAnswerOptionRepository,
    @Autowired private val jpaOrganizationRepository: JpaOrganizationRepository
) {
    private val questionRepository: SqlQuestionRepository = SqlQuestionRepository(jpaQuestionRepository)

    @Test
    @Disabled
    fun `it finds a question in survey`() {
        val organization = OrganizationMother.default()
        val team = TeamMother.default()
        val assessment = AssessmentMother.default(teamId = team.id)
        val survey = SurveyMother.default(assessmentId = assessment.id)

        val answerOption = AnswerOptionMother.default()
        val question: MultipleChoiceQuestion = MultipleChoiceQuestionMother.default(
            answerOptions = setOf(answerOption)
        )

        var jpaOrganization = organization.toJpa()
        jpaOrganization = jpaOrganizationRepository.save(jpaOrganization)

        var jpaTeam = team.toJpa(jpaOrganization)
        jpaTeam = jpaTeamRepository.save(jpaTeam)

        var jpaAssessment = assessment.toJpa(null, jpaTeam = jpaTeam)
        jpaAssessment = jpaAssessmentRepository.save(jpaAssessment)
        var jpaSurvey = survey.toJpa(jpaAssessment)
        jpaSurvey = jpaQuestionnaireRepository.save(jpaSurvey)

        var jpaQuestion = question.toJpa(jpaSurvey)
        jpaQuestion = jpaQuestionRepository.save(jpaQuestion)

        var jpaAnswerOption = answerOption.toJpa(jpaQuestion)
        jpaAnswerOption = jpaAnswerOptionRepository.save(jpaAnswerOption)

        jpaQuestion.answerOptions.add(jpaAnswerOption)
        jpaQuestionRepository.save(jpaQuestion)

        questionRepository.find(question.id).fold(
            { Assertions.fail("It must be right") },
            { questionInSurveyFound ->
                assertThat(questionInSurveyFound).usingRecursiveComparison().isEqualTo(question)
            }
        )
    }
}
