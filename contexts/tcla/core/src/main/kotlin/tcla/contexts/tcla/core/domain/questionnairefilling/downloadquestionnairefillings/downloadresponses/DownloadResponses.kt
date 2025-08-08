package tcla.contexts.tcla.core.domain.questionnairefilling.downloadquestionnairefillings.downloadresponses

import arrow.core.Either
import tcla.contexts.tcla.core.application.failures.DownloadResponsesFailure
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnairefilling.model.QuestionnaireFilling


interface DownloadResponses {
    fun execute(externalQuestionnaireId: ExternalQuestionnaireId?, questions: Set<Question>): Either<DownloadResponsesFailure, Set<QuestionnaireFilling>>
}
