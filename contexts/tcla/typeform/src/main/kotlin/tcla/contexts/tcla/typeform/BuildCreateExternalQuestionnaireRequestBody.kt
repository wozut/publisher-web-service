package tcla.contexts.tcla.typeform

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.questionnaire.model.Questionnaire

@Named
class BuildCreateExternalQuestionnaireRequestBody {

    fun build(workspace: String, publish: Boolean, survey: Questionnaire, showTypeformBranding: Boolean): String = """
        {
            "type": "quiz",
            "title": "Survey id: ${survey.id.uuid}",
            "workspace": {
                "href": "https://api.typeform.com/workspaces/${workspace}"
            },
            "hidden": ["respondent"],
            "theme": {
                "href": "https://api.typeform.com/themes/dV4jhPMk"
            },
            "settings": {
                "language": "en",
                "progress_bar": "proportion",
                "meta": {
                    "allow_indexing": false
                },
                "hide_navigation": false,
                "is_public": ${publish},
                "is_trial": false,
                "show_progress_bar": false,
                "show_typeform_branding": ${showTypeformBranding},
                "are_uploads_public": false,
                "show_time_to_complete": true,
                "show_number_of_submissions": false,
                "show_cookie_consent": false,
                "show_question_number": false,
                "show_key_hint_on_choices": true,
                "autosave_progress": false,
                "free_form_navigation": false,
                "use_lead_qualification": false,
                "pro_subdomain_enabled": false,
                "capabilities": {
                    "e2e_encryption": {
                        "enabled": false,
                        "modifiable": false
                    }
                }
            },
            "thankyou_screens": [
                {
                    "ref": "58922574-874d-4e40-8ff6-b60c4ab99d4e",
                    "title": "",
                    "type": "thankyou_screen",
                    "properties": {
                        "show_button": false,
                        "share_icons": false,
                        "button_mode": "default_redirect",
                        "button_text": "again"
                    }
                },
                {
                    "ref": "default_tys",
                    "title": "All done! Thanks for your time.",
                    "type": "thankyou_screen",
                    "properties": {
                        "show_button": false,
                        "share_icons": false
                    }
                }
            ],
            "fields": [
                ${buildQuestions(survey)}
            ]
        }
    """.trimIndent()

    private fun buildQuestions(questionnaire: Questionnaire): String {
        return """
            ${buildTclQuestions(questionnaire)}
        """.trimIndent()
    }

    private fun buildTclQuestions(questionnaire: Questionnaire): String =
        questionnaire.questions
            .toList()
            .sortedBy { question -> question.order }
            .joinToString(",") { question -> question.toExternalQuestion() }
}
