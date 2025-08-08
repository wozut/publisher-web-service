package tcla.contexts.tcla.core.domain.questionnaire.publishexternalquestionnaire

import arrow.core.Either
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId

interface PublishExternalQuestionnaire {
    fun execute(externalQuestionnaireId: ExternalQuestionnaireId): Either<PublishExternalQuestionnaireFailure, Unit>
}
