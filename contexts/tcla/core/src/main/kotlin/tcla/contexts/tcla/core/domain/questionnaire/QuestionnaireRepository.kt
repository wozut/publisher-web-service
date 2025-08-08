package tcla.contexts.tcla.core.domain.questionnaire

import arrow.core.Either
import arrow.core.NonEmptyList
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.assessment.model.Step
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.team.model.TeamId
import tcla.libraries.search.Filter
import java.time.Instant

interface QuestionnaireRepository {
    fun exists(id: QuestionnaireId): Either<Failure, Boolean>
    fun find(id: QuestionnaireId): Either<NonEmptyList<Failure>, Questionnaire>
    fun save(questionnaire: Questionnaire): Either<NonEmptyList<Failure>, Questionnaire>
    fun search(filter: Filter<QuestionnaireFilterKey>? = null): Either<NonEmptyList<Failure>, List<Questionnaire>>
    fun searchByExternalQuestionnaireIdIsNull(): Either<NonEmptyList<Failure>, List<Questionnaire>>

    fun searchByExternalQuestionnaireIdIsNotNullAndStartDateBeforeAndExternalQuestionnaireIsPublic(
        instant: Instant,
        externalQuestionnaireIsPublic: Boolean
    ): Either<NonEmptyList<Failure>, List<Questionnaire>>

    fun searchByAssessment_Team_IdAndAssessment_CurrentStepIsIn(teamId: TeamId, steps: Set<Step>): Either<NonEmptyList<Failure>, List<Questionnaire>>

    fun searchFirstByAssessment_Team_IdAndAssessment_CurrentStepIsNotOrderByEndDateDesc(teamId: TeamId, step: Step): Either<NonEmptyList<Failure>, List<Questionnaire>>
}
