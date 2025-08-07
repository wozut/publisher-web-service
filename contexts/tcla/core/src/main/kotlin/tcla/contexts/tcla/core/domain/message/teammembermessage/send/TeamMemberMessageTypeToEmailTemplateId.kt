package tcla.contexts.tcla.core.domain.message.teammembermessage.send

import tcla.contexts.tcla.core.domain.message.teammembermessage.model.TeamMemberMessage

fun TeamMemberMessage.Type.toEmailTemplateId(): String =
    when (this) {
        TeamMemberMessage.Type.SURVEY_INVITATION -> 5431255
        TeamMemberMessage.Type._48_HOURS_BEFORE_SURVEY_END -> 5431196
        TeamMemberMessage.Type.SURVEY_DURATION_EXTENDED -> 5431371
    }.toString()
