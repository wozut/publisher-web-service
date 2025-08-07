package tcla.contexts.tcla.core.application.failures

import kotlin.reflect.KClass


object FailureClassAndCodeMapping {
    private const val CONTEXT_CODE_PREFIX = "1"

    val associations: List<FailureClassAndCodeAssociation> = listOf(
        FailureClassAndCodeAssociation(Failure.InvalidDocument::class, 1),
        FailureClassAndCodeAssociation(Failure.JsonApiTypeNotAllowed::class, 2),
        // 3
        FailureClassAndCodeAssociation(TimeZoneToDomainFailure.UnknownTimeZone::class, 4),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Action::class, 5),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Survey::class, 6),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Assessment::class, 7),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Question::class, 8),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Respondent::class, 9),
        FailureClassAndCodeAssociation(Failure.EntityAlreadyExists.Organization::class, 10),
        // 11
        FailureClassAndCodeAssociation(NotOverlappingQuestionnairesByTeamRuleFailure.OverlapsWithAnotherAssessment::class, 12),
        // 13
        // 14
        // 15
        // 16
        // 17
        // 18
        FailureClassAndCodeAssociation(ReanalyseTclFailure.DuplicatedAssessmentIds::class, 19),
        FailureClassAndCodeAssociation(ReanalyseTclFailure.AssessmentHasNotAnAppropriateStatus::class, 20),
        FailureClassAndCodeAssociation(Failure.StringIsNotUuid.TeamId::class, 21),
        FailureClassAndCodeAssociation(Failure.StringIsNotUuid.OrganizationId::class, 22),
        FailureClassAndCodeAssociation(Failure.RequestNotAuthenticated::class, 23),
        FailureClassAndCodeAssociation(Failure.InsufficientPermissions::class, 24),
        FailureClassAndCodeAssociation(Failure.DatabaseException::class, 25),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.TeamMember::class, 26),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.Respondent::class, 27),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.Action::class, 28),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.QuestionLabel::class, 29),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.AccountMessage::class, 30),
        FailureClassAndCodeAssociation(Failure.UnableToTransformIntoDomainData.TeamMemberMessage::class, 31),
        FailureClassAndCodeAssociation(DeleteTeamFailure.RequesterNotOwnsTeam::class, 32),
        FailureClassAndCodeAssociation(DeleteTeamFailure.DatabaseException::class, 33),
        FailureClassAndCodeAssociation(DeleteTeamFailure.IdNotConformsUuid::class, 34),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Assessment::class, 35),
        FailureClassAndCodeAssociation(DeleteTeamFailure.TeamNotFound::class, 36),
        FailureClassAndCodeAssociation(CreateAssessmentFailure.UnableToCreate::class, 37),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Team::class, 38),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.TeamMember::class, 39),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Account::class, 40),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Survey::class, 41),
        FailureClassAndCodeAssociation(Failure.UnsupportedDatabaseFilter::class, 42),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Organization::class, 43),
        FailureClassAndCodeAssociation(CreateTeamFailure.MaximumAmountOfTeamsPerOrganizationReached::class, 44),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.Organization::class, 45),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.Survey::class, 46),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.Assessment::class, 47),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.TclDriver::class, 48),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.AnswerOption::class, 49),
        FailureClassAndCodeAssociation(Failure.DataWasExpectedToExist.QuestionToThinkAbout::class, 50),
        FailureClassAndCodeAssociation(TeamFailure.InvalidName::class, 51),
        FailureClassAndCodeAssociation(TeamFailure.InvalidTimeZone::class, 52),
        FailureClassAndCodeAssociation(TeamFailure.InvalidIsArchived::class, 53)
    )

    fun getCodeFor(clazz: KClass<*>): String? = associations
        .firstOrNull { association -> association.clazz == clazz }
        ?.code
        ?.let { code: Int -> CONTEXT_CODE_PREFIX.plus(code.toString()) }

}

data class FailureClassAndCodeAssociation(val clazz: KClass<out Failure>, val code: Int)


