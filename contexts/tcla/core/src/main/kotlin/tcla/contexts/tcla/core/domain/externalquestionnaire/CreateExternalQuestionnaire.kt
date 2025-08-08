package tcla.contexts.tcla.core.domain.externalquestionnaire

import arrow.core.Either
import tcla.contexts.tcla.core.application.failures.CreateExternalQuestionnaireFailure
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire

interface CreateExternalQuestionnaire {
    fun execute(questionnaire: Questionnaire, publish: Boolean = false): Either<CreateExternalQuestionnaireFailure, ExternalQuestionnaireId>
}
