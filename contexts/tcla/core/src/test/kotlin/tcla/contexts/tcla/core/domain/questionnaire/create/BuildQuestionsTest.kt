package tcla.contexts.tcla.core.domain.questionnaire.create


import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOptionId
import tcla.contexts.tcla.core.domain.question.model.MultipleChoiceQuestionMother
import tcla.contexts.tcla.core.domain.question.model.Order
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.core.domain.question.model.Question.Label
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Gender
import tcla.contexts.tcla.core.domain.question.model.Question.Label.ModeOfWorking
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q12UseOfInformation1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q12UseOfInformation2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q12UseOfInformation3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q13EfficiencyAndEffectivenessProcess1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q13EfficiencyAndEffectivenessProcess2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q13EfficiencyAndEffectivenessProcess3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q14EfficiencyAndEffectivenessConsistency1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q14EfficiencyAndEffectivenessConsistency2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q14EfficiencyAndEffectivenessConsistency3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q15EfficiencyAndEffectivenessPace1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q15EfficiencyAndEffectivenessPace2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q15EfficiencyAndEffectivenessPace3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q16EfficiencyAndEffectivenessPerformance1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q16EfficiencyAndEffectivenessPerformance2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q16EfficiencyAndEffectivenessPerformance3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q17AdaptabilityResilience1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q17AdaptabilityResilience2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q17AdaptabilityResilience3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q18AdaptabilityIterativeWorking1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q18AdaptabilityIterativeWorking2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q18AdaptabilityIterativeWorking3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q19AdaptabilityContinuousLearning1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q19AdaptabilityContinuousLearning2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q19AdaptabilityContinuousLearning3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q1TeamCompositionComplexity1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q1TeamCompositionComplexity2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q1TeamCompositionComplexity3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q20ProblemStatementProblemDefinition1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q20ProblemStatementProblemDefinition2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q20ProblemStatementProblemDefinition3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q21ProblemStatementSolutionAlignment1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q21ProblemStatementSolutionAlignment2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q21ProblemStatementSolutionAlignment3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q22ComplexityTask1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q22ComplexityTask2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q22ComplexityTask3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q22ComplexityTask4
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q23ComplexityContextual1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q23ComplexityContextual2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q23ComplexityContextual3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q23ComplexityContextual4
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q24Metrics1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q24Metrics2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q24Metrics3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q25ToolsSuitability1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q25ToolsSuitability2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q25ToolsSuitability3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q25ToolsSuitability4
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q26ToolsPerformance1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q26ToolsPerformance2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q26ToolsPerformance3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q26ToolsPerformance4
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q27Environment1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q27Environment2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q27Environment3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q27Environment4
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q2TeamCompositionCompetence1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q2TeamCompositionCompetence2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q2TeamCompositionCompetence3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q3MemberRolesRoleClarity1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q3MemberRolesRoleClarity2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q3MemberRolesRoleClarity3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q4MemberRolesRoleFit1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q4MemberRolesRoleFit2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q4MemberRolesRoleFit3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q5MemberRolesRoleLoad1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q5MemberRolesRoleLoad2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q5MemberRolesRoleLoad3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q6CultureTeamAlignment1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q6CultureTeamAlignment2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q6CultureTeamAlignment3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q7CultureTeamInteractionCommunication1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q7CultureTeamInteractionCommunication2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q7CultureTeamInteractionCommunication3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q8CultureTeamInteractionKnowledgeExchange1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q8CultureTeamInteractionKnowledgeExchange2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q8CultureTeamInteractionKnowledgeExchange3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity1
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity2
import tcla.contexts.tcla.core.domain.question.model.Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity3
import tcla.contexts.tcla.core.domain.question.model.Question.Label.TeamFamiliarity
import tcla.contexts.tcla.core.domain.question.model.Question.Label.WorkFamiliarity
import java.util.UUID

class BuildQuestionsTest {

    @AfterEach
    fun afterEach() {
        unmockkStatic(UUID::class)
    }

    @Test
    fun `it includes all questions of interest`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = true,
            includeWorkFamiliarityQuestion = true,
            includeTeamFamiliarityQuestion = true,
            includeModeOfWorkingQuestion = true
        )

        val expectedQuestions = buildAllQuestions()
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    @Test
    fun `it does not include any question of interest`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = false,
            includeWorkFamiliarityQuestion = false,
            includeTeamFamiliarityQuestion = false,
            includeModeOfWorkingQuestion = false
        )

        val expectedQuestions = buildDriverQuestionsForTest( 1)
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    @Test
    fun `it includes work familiarity question`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = false,
            includeWorkFamiliarityQuestion = true,
            includeTeamFamiliarityQuestion = false,
            includeModeOfWorkingQuestion = false
        )

        val expectedQuestions = buildDriverQuestionsForTest(2).plus(buildWorkFamiliarityQuestion(1))
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    @Test
    fun `it includes team familiarity question`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = false,
            includeWorkFamiliarityQuestion = false,
            includeTeamFamiliarityQuestion = true,
            includeModeOfWorkingQuestion = false
        )

        val expectedQuestions = buildDriverQuestionsForTest(2).plus(buildTeamFamiliarityQuestion(1))
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    @Test
    fun `it includes mode of working question`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = false,
            includeWorkFamiliarityQuestion = false,
            includeTeamFamiliarityQuestion = false,
            includeModeOfWorkingQuestion = true
        )

        val expectedQuestions = buildDriverQuestionsForTest(2).plus(buildModeOfWorkingQuestion(1))
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    @Test
    fun `it includes two questions of interest`() {
        val uuid = UUID.randomUUID()
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns uuid

        val actualQuestions: Set<Question> = buildQuestions(
            includeGenderQuestion = false,
            includeWorkFamiliarityQuestion = true,
            includeTeamFamiliarityQuestion = false,
            includeModeOfWorkingQuestion = true
        )

        val expectedQuestions = buildDriverQuestionsForTest(3)
            .plus(buildWorkFamiliarityQuestion(1))
            .plus(buildModeOfWorkingQuestion(2))
        assertThat(actualQuestions.toList())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrderElementsOf(expectedQuestions)
    }

    private fun buildAnswerOptions(optionValues: List<String>): Set<AnswerOption> =
        optionValues.mapIndexed { index, option -> buildAnswerOption(index, option) }.toSet()

    private fun buildAnswerOption(index: Int, option: String): AnswerOption =
        AnswerOption(
            id = AnswerOptionId(UUID.randomUUID()),
            order = AnswerOption.Order(value = index + 1),
            value = AnswerOption.Value(value = option)
        )

    private fun buildQuestionsOfInterest(firstOrder: Int): Set<Question> = setOf(
        buildGenderQuestion(firstOrder),
        buildWorkFamiliarityQuestion(firstOrder + 1),
        buildTeamFamiliarityQuestion(firstOrder + 2),
        buildModeOfWorkingQuestion(firstOrder + 3)
    )

    private fun buildModeOfWorkingQuestion(order: Int) = MultipleChoiceQuestionMother.default(
        label = ModeOfWorking,
        order = Order(order),
        answerOptions = buildModeOfWorkingAnswerOptions()
    )

    private fun buildTeamFamiliarityQuestion(order: Int) = MultipleChoiceQuestionMother.default(
        label = TeamFamiliarity,
        order = Order(order),
        answerOptions = build1to5AnswerOptions()
    )

    private fun buildWorkFamiliarityQuestion(order: Int) = MultipleChoiceQuestionMother.default(
        label = WorkFamiliarity,
        order = Order(order),
        answerOptions = build1to5AnswerOptions()
    )

    private fun buildGenderQuestion(order: Int) = MultipleChoiceQuestionMother.default(
        label = Gender,
        order = Order(order),
        answerOptions = buildGenderAnswerOptions()
    )

    private fun buildAllQuestions(): Set<Question> =
        buildQuestionsOfInterest(firstOrder = 1) + buildDriverQuestionsForTest(firstOrder = 5)

    private fun buildDriverQuestionsForTest(
        firstOrder: Int
    ): Set<Question> =
        allDriverQuestionAliases()
            .mapIndexed { index, alias ->
                MultipleChoiceQuestionMother.default(
                    label = alias,
                    order = Order(index + firstOrder),
                    answerOptions = build1to5AnswerOptions()
                )
            }.toSet()

    private fun build1to5AnswerOptions(): Set<AnswerOption> =
        buildAnswerOptions(optionValuesFrom1to5)

    private fun buildModeOfWorkingAnswerOptions(): Set<AnswerOption> =
        buildAnswerOptions(listOf("Remote", "In-person", "Hybrid"))

    private fun buildGenderAnswerOptions(): Set<AnswerOption> =
        buildAnswerOptions(listOf("Female", "Male", "Other"))


    private val optionValuesFrom1to5 = listOf("1", "2", "3", "4", "5")

    private fun allDriverQuestionAliases(): List<Label> {
        return listOf(
            Q1TeamCompositionComplexity1,
            Q1TeamCompositionComplexity2,
            Q1TeamCompositionComplexity3,
            Q2TeamCompositionCompetence1,
            Q2TeamCompositionCompetence2,
            Q2TeamCompositionCompetence3,
            Q3MemberRolesRoleClarity1,
            Q3MemberRolesRoleClarity2,
            Q3MemberRolesRoleClarity3,
            Q4MemberRolesRoleFit1,
            Q4MemberRolesRoleFit2,
            Q4MemberRolesRoleFit3,
            Q5MemberRolesRoleLoad1,
            Q5MemberRolesRoleLoad2,
            Q5MemberRolesRoleLoad3,
            Q6CultureTeamAlignment1,
            Q6CultureTeamAlignment2,
            Q6CultureTeamAlignment3,
            Q7CultureTeamInteractionCommunication1,
            Q7CultureTeamInteractionCommunication2,
            Q7CultureTeamInteractionCommunication3,
            Q8CultureTeamInteractionKnowledgeExchange1,
            Q8CultureTeamInteractionKnowledgeExchange2,
            Q8CultureTeamInteractionKnowledgeExchange3,
            Q9CultureMemberPsychologicalSafetyAuthenticity1,
            Q9CultureMemberPsychologicalSafetyAuthenticity2,
            Q9CultureMemberPsychologicalSafetyAuthenticity3,
            Q10CultureMemberPsychologicalSafetySpeakingUp1,
            Q10CultureMemberPsychologicalSafetySpeakingUp2,
            Q10CultureMemberPsychologicalSafetySpeakingUp3,
            Q11CultureMemberPsychologicalSafetyEmbracingFailure1,
            Q11CultureMemberPsychologicalSafetyEmbracingFailure2,
            Q11CultureMemberPsychologicalSafetyEmbracingFailure3,
            Q12UseOfInformation1,
            Q12UseOfInformation2,
            Q12UseOfInformation3,
            Q13EfficiencyAndEffectivenessProcess1,
            Q13EfficiencyAndEffectivenessProcess2,
            Q13EfficiencyAndEffectivenessProcess3,
            Q14EfficiencyAndEffectivenessConsistency1,
            Q14EfficiencyAndEffectivenessConsistency2,
            Q14EfficiencyAndEffectivenessConsistency3,
            Q15EfficiencyAndEffectivenessPace1,
            Q15EfficiencyAndEffectivenessPace2,
            Q15EfficiencyAndEffectivenessPace3,
            Q16EfficiencyAndEffectivenessPerformance1,
            Q16EfficiencyAndEffectivenessPerformance2,
            Q16EfficiencyAndEffectivenessPerformance3,
            Q17AdaptabilityResilience1,
            Q17AdaptabilityResilience2,
            Q17AdaptabilityResilience3,
            Q18AdaptabilityIterativeWorking1,
            Q18AdaptabilityIterativeWorking2,
            Q18AdaptabilityIterativeWorking3,
            Q19AdaptabilityContinuousLearning1,
            Q19AdaptabilityContinuousLearning2,
            Q19AdaptabilityContinuousLearning3,
            Q20ProblemStatementProblemDefinition1,
            Q20ProblemStatementProblemDefinition2,
            Q20ProblemStatementProblemDefinition3,
            Q21ProblemStatementSolutionAlignment1,
            Q21ProblemStatementSolutionAlignment2,
            Q21ProblemStatementSolutionAlignment3,
            Q22ComplexityTask1,
            Q22ComplexityTask2,
            Q22ComplexityTask3,
            Q22ComplexityTask4,
            Q23ComplexityContextual1,
            Q23ComplexityContextual2,
            Q23ComplexityContextual3,
            Q23ComplexityContextual4,
            Q24Metrics1,
            Q24Metrics2,
            Q24Metrics3,
            Q25ToolsSuitability1,
            Q25ToolsSuitability2,
            Q25ToolsSuitability3,
            Q25ToolsSuitability4,
            Q26ToolsPerformance1,
            Q26ToolsPerformance2,
            Q26ToolsPerformance3,
            Q26ToolsPerformance4,
            Q27Environment1,
            Q27Environment2,
            Q27Environment3,
            Q27Environment4
        )
    }
}
