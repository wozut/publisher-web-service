package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore

@Named
class ClusterSectionBuilder {

    fun buildIntroductionTitle(): String = """
        #### Key factors influencing Team Cognitive Load in this cluster
    """.trimIndent()

    fun buildIntroductionBody(cluster: Cluster): String =
        buildString {
            appendLine(
                when (cluster) {
                    Cluster.TeamCharacteristics -> TEAM_CHARACTERISTICS_INTRODUCTION_BODY
                    Cluster.TaskCharacteristics -> TASK_CHARACTERISTICS_INTRODUCTION_BODY
                    Cluster.WorkPracticesAndProcesses -> WORK_PRACTICES_PROCESSES_INTRODUCTION_BODY
                    Cluster.WorkEnvironmentAndTools -> WORK_ENVIRONMENT_TOOLS_INTRODUCTION_BODY
                }
            )
        }

    fun buildQuestionsAndRecommendedReadingsBody(highestDriverScorePairs: List<TclDriverAndScore>): String? {
        val questionsPart: String? = buildQuestionsPart(highestDriverScorePairs)
        val recommendedReadingsPart: String? = buildRecommendedReadingsPart(highestDriverScorePairs)

        if (questionsPart == null && recommendedReadingsPart == null) return null

        return buildString {
            appendLine("<question-box>")
            appendLine() //this empty line is needed for correct rendering
            if (questionsPart != null) appendLine(questionsPart)
            if (recommendedReadingsPart != null) appendLine(recommendedReadingsPart)
            appendLine("</question-box>")
        }
    }

    private fun buildRecommendedReadingsPart(tclDriverAndScoreList: List<TclDriverAndScore>): String? {
        val recommendedReadings: List<String> = tclDriverAndScoreList
            .map { it.name.value }
            .mapNotNull { it.toRelevantDriver() }
            .map { tclDriverAndRecommendedReadings.getRecommendedReadingsFor(it) }
            .flatten()

        return when (recommendedReadings.isEmpty()) {
            true -> null
            false -> buildString {
                appendLine("#### Recommended reading")
                val recommendedReadingsFormatted = recommendedReadings.joinToString("\n") { it }
                appendLine(recommendedReadingsFormatted)
            }
        }
    }

    private fun buildQuestionsPart(highestDriverScorePairs: List<TclDriverAndScore>): String? =
        when (val questionList: String? = buildQuestionList(highestDriverScorePairs)) {
            null -> null
            else -> buildString {
                appendLine(QuestionsSubsectionText.TITLE)
                appendLine()
                appendLine(questionList)
            }
        }

    private fun buildQuestionList(highestDriverScorePairs: List<TclDriverAndScore>): String? {
        val drivers: List<Driver> = highestDriverScorePairs
            .map { it.name.value }
            .mapNotNull(String::toRelevantDriver)

        val questions: List<String> = tclDriverAndQuestionsPairs.chooseQuestionsFor(drivers)

        return when (questions.isEmpty()) {
            true -> null
            false -> questionListToMarkdown(questions)
        }
    }

    private fun questionListToMarkdown(questions: List<String>): String =
        questions.joinToString(separator = "\n") { "* $it" }

    companion object {
        private val TEAM_CHARACTERISTICS_INTRODUCTION_BODY: String = """
            The characteristics of a team, including team member roles and team culture, directly affect Team Cognitive Load. Ambiguity in roles, ineffective communication, and a lack of trust can cause team members to feel overwhelmed and frustrated, increasing cognitive load.
        """.trimIndent()

        private val TASK_CHARACTERISTICS_INTRODUCTION_BODY: String = """
            Team Cognitive Load is affected by the nature and complexity of tasks. Ambiguous problems and challenges to be solved, and lack of clear success indicators can lead to ineffective collaboration and heightened frustration, contributing to increased cognitive load levels in the team.
        """.trimIndent()
        private val WORK_PRACTICES_PROCESSES_INTRODUCTION_BODY: String = """
            Ineffective and/or inconsistent work practices contribute to an increase in Team Cognitive Load. Delays and unreliability in processes and information lead to frustration, uncertainty, and poor outcomes, increasing cognitive load.
        """.trimIndent()
        private val WORK_ENVIRONMENT_TOOLS_INTRODUCTION_BODY: String = """
            Working conditions and effectiveness of tooling play a key role in Team Cognitive Load. Bad lighting, lack of suitable working spaces (physical and digital), and inefficient tools can lead to high levels of distraction and anxiety, and increases the likelihood of suboptimal work outputs.
        """.trimIndent()

    }
}

object QuestionsSubsectionText {
    const val TITLE = "#### Questions to think about"
}

