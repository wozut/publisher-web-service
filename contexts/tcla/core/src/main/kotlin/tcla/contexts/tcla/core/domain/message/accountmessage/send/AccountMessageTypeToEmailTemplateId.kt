package tcla.contexts.tcla.core.domain.message.accountmessage.send

import tcla.contexts.tcla.core.domain.message.accountmessage.model.AccountMessage

fun AccountMessage.Type.toEmailTemplateId(): String =
    when (this) {
        AccountMessage.Type._48_HOURS_BEFORE_SURVEY_END -> 5431313
        AccountMessage.Type.RESULTS_AVAILABLE -> 5431289
        AccountMessage.Type.RESULTS_NOT_AVAILABLE -> 5431142
        AccountMessage.Type.CANCELED_SURVEY -> 5431053
    }.toString()
