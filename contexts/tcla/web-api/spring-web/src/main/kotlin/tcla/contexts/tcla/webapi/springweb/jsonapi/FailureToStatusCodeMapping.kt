package tcla.contexts.tcla.webapi.springweb.jsonapi

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import tcla.contexts.tcla.core.application.failures.ActionFailure
import tcla.contexts.tcla.core.application.failures.AnalyseTclFailure
import tcla.contexts.tcla.core.application.failures.CancelAssessmentFailure
import tcla.contexts.tcla.core.application.failures.CreateAssessmentFailure
import tcla.contexts.tcla.core.application.failures.CreateExternalQuestionnaireFailure
import tcla.contexts.tcla.core.application.failures.CreateOrganizationFailure
import tcla.contexts.tcla.core.application.failures.CreateTeamFailure
import tcla.contexts.tcla.core.application.failures.DecideWhetherDataIsAnalysableFailure
import tcla.contexts.tcla.core.application.failures.DeleteTeamFailure
import tcla.contexts.tcla.core.application.failures.DownloadQuestionnaireFillingsFailure
import tcla.contexts.tcla.core.application.failures.DownloadResponsesFailure
import tcla.contexts.tcla.core.application.failures.EnqueueAccountMessageSendingIfNeededFailure
import tcla.contexts.tcla.core.application.failures.EnqueueDecisionWhetherDataIsAnalysableFailure
import tcla.contexts.tcla.core.application.failures.EnqueueQuestionnaireFillingsDownloadIfNeededFailure
import tcla.contexts.tcla.core.application.failures.EnqueueTclAnalysisIfNeededFailure
import tcla.contexts.tcla.core.application.failures.EnqueueTeamMemberMessageSendingIfNeededFailure
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.Failure.EntityAlreadyExists
import tcla.contexts.tcla.core.application.failures.InstantToTimestampFailure
import tcla.contexts.tcla.core.application.failures.InvalidEmail
import tcla.contexts.tcla.core.application.failures.NotOverlappingQuestionnairesByTeamRuleFailure
import tcla.contexts.tcla.core.application.failures.ReanalyseTclFailure
import tcla.contexts.tcla.core.application.failures.ResponseAcceptanceIntervalFailure
import tcla.contexts.tcla.core.application.failures.SearchTeamOwnersFailure
import tcla.contexts.tcla.core.application.failures.SendMessageToAccountFailure
import tcla.contexts.tcla.core.application.failures.SendMessageToTeamMemberFailure
import tcla.contexts.tcla.core.application.failures.StartDataCollectionAtStartDateFailure
import tcla.contexts.tcla.core.application.failures.StartDataCollectionFailure
import tcla.contexts.tcla.core.application.failures.StepFailure
import tcla.contexts.tcla.core.application.failures.StopDataCollectionAtEndDateFailure
import tcla.contexts.tcla.core.application.failures.TeamFailure
import tcla.contexts.tcla.core.application.failures.TimeZoneToDomainFailure

fun Failure.toStatusCode(): HttpStatus = when (this) {
    is Failure.DataWasExpectedToExist -> when (this) {
        Failure.DataWasExpectedToExist.Survey -> INTERNAL_SERVER_ERROR
        Failure.DataWasExpectedToExist.AnswerOption -> INTERNAL_SERVER_ERROR
        Failure.DataWasExpectedToExist.Assessment -> INTERNAL_SERVER_ERROR
        Failure.DataWasExpectedToExist.TclDriver -> INTERNAL_SERVER_ERROR
        Failure.DataWasExpectedToExist.QuestionToThinkAbout -> INTERNAL_SERVER_ERROR
        Failure.DataWasExpectedToExist.Organization -> INTERNAL_SERVER_ERROR
    }

    is Failure.DatabaseException -> BAD_GATEWAY
    is Failure.EntityNotFound -> NOT_FOUND
    Failure.RequestNotAuthenticated -> UNAUTHORIZED
    is Failure.RequesterDoesNotOwnResource -> when (this) {
        Failure.RequesterDoesNotOwnResource.Assessment -> TODO()
        Failure.RequesterDoesNotOwnResource.Team -> TODO()
        Failure.RequesterDoesNotOwnResource.TeamOwner -> TODO()
        Failure.RequesterDoesNotOwnResource.Action -> TODO()
        Failure.RequesterDoesNotOwnResource.Organization -> TODO()
        Failure.RequesterDoesNotOwnResource.TeamMember -> TODO()
    }

    is Failure.UnableToTransformIntoDomainData -> when (this) {
        Failure.UnableToTransformIntoDomainData.AccountMessage -> INTERNAL_SERVER_ERROR
        Failure.UnableToTransformIntoDomainData.TeamMember -> INTERNAL_SERVER_ERROR
        Failure.UnableToTransformIntoDomainData.TeamMemberMessage -> INTERNAL_SERVER_ERROR
        Failure.UnableToTransformIntoDomainData.QuestionLabel -> INTERNAL_SERVER_ERROR
        Failure.UnableToTransformIntoDomainData.Action -> INTERNAL_SERVER_ERROR
        Failure.UnableToTransformIntoDomainData.Respondent -> INTERNAL_SERVER_ERROR
    }

    is Failure.UnableToTransformIntoPersistenceData -> when (this) {
        Failure.UnableToTransformIntoPersistenceData.AccountMessage -> TODO()
        Failure.UnableToTransformIntoPersistenceData.TeamMemberMessage -> TODO()
    }

    is Failure.UnsupportedDatabaseFilter -> INTERNAL_SERVER_ERROR

    is DeleteTeamFailure -> when (this) {
        is DeleteTeamFailure.DatabaseException -> BAD_GATEWAY
        DeleteTeamFailure.IdNotConformsUuid -> BAD_REQUEST
        DeleteTeamFailure.RequesterNotOwnsTeam -> FORBIDDEN
        DeleteTeamFailure.TeamNotFound -> NOT_FOUND
    }

    is EntityAlreadyExists -> when (this) {
        EntityAlreadyExists.Assessment -> INTERNAL_SERVER_ERROR
        EntityAlreadyExists.Survey -> INTERNAL_SERVER_ERROR
        EntityAlreadyExists.Question -> INTERNAL_SERVER_ERROR
        EntityAlreadyExists.Action -> INTERNAL_SERVER_ERROR
        EntityAlreadyExists.Organization -> INTERNAL_SERVER_ERROR
        EntityAlreadyExists.Respondent -> INTERNAL_SERVER_ERROR
    }

    is StartDataCollectionFailure -> when (this) {
        StartDataCollectionFailure.AssessmentIsNotInAppropriateStep -> TODO()
        StartDataCollectionFailure.ExternalQuestionnaireAlreadyExists -> TODO()
        StartDataCollectionFailure.QuestionnaireHasNotStartedYet -> TODO()
    }

    is Failure.StringIsNotUuid -> when (this) {
        Failure.StringIsNotUuid.AssessmentId -> BAD_REQUEST
        Failure.StringIsNotUuid.TeamId -> BAD_REQUEST
        Failure.StringIsNotUuid.SurveyId -> TODO()
        Failure.StringIsNotUuid.QuestionToThinkAboutId -> TODO()
        Failure.StringIsNotUuid.TclDriverId -> TODO()
        Failure.StringIsNotUuid.ActionId -> TODO()
        Failure.StringIsNotUuid.TeamMemberId -> TODO()
        Failure.StringIsNotUuid.OrganizationId -> BAD_REQUEST
    }

    is CreateTeamFailure -> when (this) {
        CreateTeamFailure.MaximumAmountOfTeamsPerOrganizationReached -> FORBIDDEN
    }

    is TeamFailure -> when (this) {
        TeamFailure.InvalidName -> BAD_REQUEST
        TeamFailure.InvalidTimeZone -> BAD_REQUEST
        TeamFailure.InvalidIsArchived -> BAD_REQUEST
    }

    is AnalyseTclFailure -> when (this) {
        AnalyseTclFailure.AssessmentIsNotInAppropriateStep -> TODO()
        AnalyseTclFailure.AssessmentNotFound -> TODO()
        is AnalyseTclFailure.DatabaseException -> TODO()
        AnalyseTclFailure.FilterNotSupported -> TODO()
        AnalyseTclFailure.InsufficientCells -> TODO()
        AnalyseTclFailure.InsufficientRows -> TODO()
        AnalyseTclFailure.InvalidURL -> TODO()
        AnalyseTclFailure.MissingQuestionnaire -> TODO()
        AnalyseTclFailure.QuestionnaireNotFound -> TODO()
        AnalyseTclFailure.TclDriverNotFound -> TODO()
        AnalyseTclFailure.TclDriverScoreAlreadyExist -> TODO()
        AnalyseTclFailure.TclModelCallException -> TODO()
        AnalyseTclFailure.TclModelRun -> TODO()
        AnalyseTclFailure.ThereAreNoResponses -> TODO()
        AnalyseTclFailure.UnableToCreateWorkbook -> TODO()
        AnalyseTclFailure.UnableToGetSheet -> TODO()
        AnalyseTclFailure.UnsupportedData -> TODO()
    }

    DecideWhetherDataIsAnalysableFailure.AssessmentIsNotInAppropriateStep -> TODO()
    is DownloadQuestionnaireFillingsFailure -> when (this) {
        DownloadQuestionnaireFillingsFailure.AssessmentIsNotInAppropriateStep -> TODO()
        DownloadQuestionnaireFillingsFailure.ExternalQuestionnaireIdIsNull -> TODO()
        DownloadQuestionnaireFillingsFailure.QuestionnaireNotFound -> TODO()
    }

    is SearchTeamOwnersFailure -> when (this) {
        SearchTeamOwnersFailure.AssessmentIdNotConformsUUID -> TODO()
        SearchTeamOwnersFailure.AssessmentNotFound -> TODO()
        SearchTeamOwnersFailure.AtLeastOneFilterRequired -> TODO()
        is SearchTeamOwnersFailure.DatabaseException -> TODO()
        SearchTeamOwnersFailure.NoValuesPresentInFilter -> TODO()
        is SearchTeamOwnersFailure.NotAllowedFilterKey -> TODO()
        is SearchTeamOwnersFailure.NotAllowedFilterValues -> TODO()
        SearchTeamOwnersFailure.TooManyFilters -> TODO()
        SearchTeamOwnersFailure.UnsupportedDatabaseFilter -> TODO()
    }

    is Failure.TechnicalException -> TODO()
    is ResponseAcceptanceIntervalFailure -> when (this) {
        ResponseAcceptanceIntervalFailure.InvalidDuration -> TODO()
        ResponseAcceptanceIntervalFailure.UnableModifyStartDateWhenItHasAlreadyStarted -> TODO()
        ResponseAcceptanceIntervalFailure.EndDateMustBeEqualToOrAfterNow -> TODO()
        ResponseAcceptanceIntervalFailure.StartDateMustBeEqualToOrAfterNow -> TODO()
        ResponseAcceptanceIntervalFailure.UnableModifyEndDateWhenItHasAlreadyFinished -> TODO()
    }

    is CancelAssessmentFailure -> when (this) {
        CancelAssessmentFailure.AssessmentNotCancelable -> TODO()
    }

    is CreateAssessmentFailure -> when (this) {
        CreateAssessmentFailure.InvalidEndDateFormat -> TODO()
        CreateAssessmentFailure.InvalidStartDateFormat -> TODO()
        CreateAssessmentFailure.StartDateMustBeNowOrAfter -> TODO()
        CreateAssessmentFailure.ResultsShareableTokenAlreadyExists -> TODO()
        CreateAssessmentFailure.UnableToCreate -> FORBIDDEN
    }

    is NotOverlappingQuestionnairesByTeamRuleFailure -> when (this) {
        NotOverlappingQuestionnairesByTeamRuleFailure.OverlapsWithAnotherAssessment -> FORBIDDEN
    }

    is InstantToTimestampFailure -> when (this) {
        InstantToTimestampFailure.InstantIsTooLargeToRepresentAsTimestamp -> TODO()
    }

    Failure.GeneratedIdsAreNotDistinct.Question -> TODO()
    is CreateExternalQuestionnaireFailure -> when(this) {
        is CreateExternalQuestionnaireFailure.ErrorResponse -> TODO()
        CreateExternalQuestionnaireFailure.InvalidUrl -> TODO()
        CreateExternalQuestionnaireFailure.LocationHeaderNotPresent -> TODO()
        is CreateExternalQuestionnaireFailure.RequestNotExecuted -> TODO()
    }

    is DownloadResponsesFailure -> when(this) {
        DownloadResponsesFailure.InvalidUrl -> TODO()
        is DownloadResponsesFailure.RequestNotExecuted -> TODO()
        is DownloadResponsesFailure.ResponseError -> TODO()
        DownloadResponsesFailure.UnexpectedResponse -> TODO()
    }

    StepFailure.InvalidStep -> TODO()
    is EnqueueQuestionnaireFillingsDownloadIfNeededFailure.BackgroundException -> TODO()
    is StartDataCollectionAtStartDateFailure -> when(this) {
        is StartDataCollectionAtStartDateFailure.BackgroundException -> TODO()
    }
    is StopDataCollectionAtEndDateFailure -> when(this) {
        is StopDataCollectionAtEndDateFailure.BackgroundException -> TODO()
    }


    Failure.FilterIsNeeded -> BAD_REQUEST
    Failure.FilterKeyNotAllowed -> BAD_REQUEST
    Failure.FilterValuesNotAllowed -> BAD_REQUEST
    Failure.NoValuesPresentInFilter -> BAD_REQUEST
    Failure.InvalidDocument -> BAD_REQUEST
    Failure.JsonApiTypeNotAllowed -> BAD_REQUEST
    is ActionFailure -> when(this) {
        ActionFailure.InvalidChallenges -> TODO()
        ActionFailure.InvalidContext -> TODO()
        ActionFailure.InvalidDescription -> TODO()
        ActionFailure.InvalidGoals -> TODO()
        ActionFailure.InvalidTargetTclDrivers -> TODO()
        ActionFailure.InvalidTitle -> TODO()
        ActionFailure.InvalidTargetQuestionsToThinkAbout -> TODO()
        ActionFailure.InvalidIsArchived -> TODO()
    }
    is EnqueueAccountMessageSendingIfNeededFailure -> when(this) {
        is EnqueueAccountMessageSendingIfNeededFailure.BackgroundException -> TODO()
    }
    is EnqueueTeamMemberMessageSendingIfNeededFailure -> when(this) {
        is EnqueueTeamMemberMessageSendingIfNeededFailure.BackgroundException -> TODO()
    }

    is SendMessageToAccountFailure -> when(this) {
        SendMessageToAccountFailure.AlreadySent -> TODO()
        SendMessageToAccountFailure.FindTeamOwner -> TODO()
        SendMessageToAccountFailure.SendPredefinedEmail -> TODO()
        SendMessageToAccountFailure.SendingTooEarly -> TODO()
    }

    is SendMessageToTeamMemberFailure -> when(this) {
        SendMessageToTeamMemberFailure.AlreadySent -> TODO()
        SendMessageToTeamMemberFailure.FindTeamOwner -> TODO()
        SendMessageToTeamMemberFailure.SendPredefinedEmail -> TODO()
        SendMessageToTeamMemberFailure.SendingTooEarly -> TODO()
    }

    is CreateOrganizationFailure.CurrentPlanNotAllows -> TODO()
    is EnqueueDecisionWhetherDataIsAnalysableFailure -> when(this) {
        is EnqueueDecisionWhetherDataIsAnalysableFailure.BackgroundException -> TODO()
    }
    is EnqueueTclAnalysisIfNeededFailure -> when(this) {
        is EnqueueTclAnalysisIfNeededFailure.BackgroundException -> TODO()
    }
    Failure.TooManyFilters -> TODO()
    TimeZoneToDomainFailure.UnknownTimeZone -> INTERNAL_SERVER_ERROR
    InvalidEmail -> TODO()
    Failure.InsufficientPermissions -> FORBIDDEN
    is ReanalyseTclFailure -> when(this) {
        ReanalyseTclFailure.AssessmentHasNotAnAppropriateStatus -> BAD_REQUEST
        ReanalyseTclFailure.DuplicatedAssessmentIds -> BAD_REQUEST
    }
}
