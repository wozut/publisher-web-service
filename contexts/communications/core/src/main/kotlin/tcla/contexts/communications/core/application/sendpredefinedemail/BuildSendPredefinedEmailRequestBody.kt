package tcla.contexts.communications.core.application.sendpredefinedemail

import jakarta.inject.Named
import tcla.libraries.jsonserialization.escaped
import java.util.Hashtable

@Named
class BuildSendPredefinedEmailRequestBody {

    fun build(
        fromName: String,
        fromEmail: String,
        templateId: String,
        recipients: List<Recipient>,
        variables: Hashtable<String, String>,
        sandboxMode: Boolean
    ): String = """
    {
        "SandboxMode": ${sandboxMode},
        "Messages":[
            {
                "From": {
                    "Email": "${fromEmail.escaped()}",
                    "Name": "${fromName.escaped()}"
                },
                "To": [
                    ${buildRecipients(recipients)}
                ],
                "TemplateID": ${templateId.toInt()},
                "TemplateLanguage": true,
                "Variables": {
                    ${buildVariables(variables)}
                }
            }
        ]
    }
    """.trimIndent()

    private fun buildRecipients(recipients: List<Recipient>): String =
        recipients.map { recipient ->
            """
            {
                "Email": "${recipient.email.escaped()}",
                "Name": "${recipient.name.escaped()}"
            }
        """.trimIndent()
        }.joinToString(separator = ",") { it }

    private fun buildVariables(variables: Hashtable<String, String>): String =
        variables.map { entry -> "\"${entry.key.escaped()}\": \"${entry.value.escaped()}\"" }
            .joinToString(separator = ",") { it }
}
