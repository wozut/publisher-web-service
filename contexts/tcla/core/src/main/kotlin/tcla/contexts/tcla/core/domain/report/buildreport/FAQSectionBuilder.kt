package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named

@Named
class FAQSectionBuilder {
    fun titleFor(sectionName: FAQSections): String =
        when(sectionName) {
            FAQSections.ABOUT_TCLA -> "# What is *Team Cognitive Load* ?"
            FAQSections.TCLA_METHODOLOGY -> "# What is the methodology used in the *Team Cognitive Load Assessment* ?"
            FAQSections.TEAM_CHARACTERISTICS_ABOUT -> "# What does the *Team Characteristics* driver entail?"
            FAQSections.TASK_CHARACTERISTICS_ABOUT -> "# What does the *Task Characteristics* driver entail?"
            FAQSections.WORK_PROCESSES_PRACTICES_ABOUT -> "# What does the *Work Processes & Practices* driver entail?"
            FAQSections.WORK_ENVIRONMENT_TOOLS_ABOUT -> "# What does the *Work Environment & Tools* driver entail?"
        }

    fun faqText(sectionName: FAQSections): String =
        when(sectionName) {
            FAQSections.ABOUT_TCLA -> AboutTcla.text
            FAQSections.TCLA_METHODOLOGY -> TclaMethodologyAbout.text
            FAQSections.TEAM_CHARACTERISTICS_ABOUT -> TeamCharacteristicsAbout.text
            FAQSections.TASK_CHARACTERISTICS_ABOUT -> TaskCharacteristicsAbout.text
            FAQSections.WORK_PROCESSES_PRACTICES_ABOUT -> WorkProcessesPracticesAbout.text
            FAQSections.WORK_ENVIRONMENT_TOOLS_ABOUT -> WorkEnvironmentToolsAbout.text
        }
}
