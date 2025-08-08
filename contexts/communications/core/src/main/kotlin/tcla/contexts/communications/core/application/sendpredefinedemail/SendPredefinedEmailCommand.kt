package tcla.contexts.communications.core.application.sendpredefinedemail

import java.util.Hashtable

data class SendPredefinedEmailCommand(
    val fromEmail: String,
    val fromName: String,
    val recipients: List<Recipient>,
    val templateId: String,
    val variables: Hashtable<String, String>
)

data class Recipient(val name: String, val email: String)
