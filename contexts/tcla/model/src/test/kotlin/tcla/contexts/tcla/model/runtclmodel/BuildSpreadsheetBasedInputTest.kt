package tcla.contexts.tcla.model.runtclmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.answerforanalysis.model.AnswerForAnalysisId
import tcla.contexts.tcla.core.domain.answerforanalysis.model.MultipleChoiceAnswerForAnalysis
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.AnswerOptionForAnalysis
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.AnswerOptionForAnalysisId
import tcla.contexts.tcla.core.domain.answeroptionforanalysis.model.ValueForAnalysis
import tcla.contexts.tcla.core.domain.question.model.*
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysis
import tcla.contexts.tcla.core.domain.questionnairefillingforanalysis.model.QuestionnaireFillingForAnalysisId
import java.util.UUID

class BuildSpreadsheetBasedInputTest {
    @Test
    fun `generates CSV file content`() {
        val expectedCsvFileContent = """
            Gender,Work_Familiarity,Squad_Familiarity,Mode_Of_Working,Squad_Name,Q1_Team-Composition_Complexity1,Q1_Team-Composition_Complexity2,Q1_Team-Composition_Complexity3,Q2_Team-Composition_Competence1,Q2_Team-Composition_Competence2,Q2_Team-Composition_Competence3,Q3_Member-Roles_Role-Clarity1,Q3_Member-Roles_Role-Clarity2,Q3_Member-Roles_Role-Clarity3,Q4_Member-Roles_Role-Fit1,Q4_Member-Roles_Role-Fit2,Q4_Member-Roles_Role-Fit3,Q5_Member-Roles_Role-Load1,Q5_Member-Roles_Role-Load2,Q5_Member-Roles_Role-Load3,Q6_Culture_Team-Alignment1,Q6_Culture_Team-Alignment2,Q6_Culture_Team-Alignment3,Q7_Culture_Team-Interaction_Communication1,Q7_Culture_Team-Interaction_Communication2,Q7_Culture_Team-Interaction_Communication3,Q8_Culture_Team-Interaction_Knowledge-Exchange1,Q8_Culture_Team-Interaction_Knowledge-Exchange2,Q8_Culture_Team-Interaction_Knowledge-Exchange3,Q9_Culture_Member-Psychological-Safety_Authenticity1,Q9_Culture_Member-Psychological-Safety_Authenticity2,Q9_Culture_Member-Psychological-Safety_Authenticity3,Q10_Culture_Member-Psychological-Safety_Speaking-Up1,Q10_Culture_Member-Psychological-Safety_Speaking-Up2,Q10_Culture_Member-Psychological-Safety_Speaking-Up3,Q11_Culture_Member-Psychological-Safety_Embracing-Failure1,Q11_Culture_Member-Psychological-Safety_Embracing-Failure2,Q11_Culture_Member-Psychological-Safety_Embracing-Failure3,Q12_Use-Of-Information1,Q12_Use-Of-Information2,Q12_Use-Of-Information3,Q13_Efficiency&Effectiveness_Process1,Q13_Efficiency&Effectiveness_Process2,Q13_Efficiency&Effectiveness_Process3,Q14_Efficiency&Effectiveness_Consistency1,Q14_Efficiency&Effectiveness_Consistency2,Q14_Efficiency&Effectiveness_Consistency3,Q15_Efficiency&Effectiveness_Pace1,Q15_Efficiency&Effectiveness_Pace2,Q15_Efficiency&Effectiveness_Pace3,Q16_Efficiency&Effectiveness_Performance1,Q16_Efficiency&Effectiveness_Performance2,Q16_Efficiency&Effectiveness_Performance3,Q17_Adaptability_Resilience1,Q17_Adaptability_Resilience2,Q17_Adaptability_Resilience3,Q18_Adaptability_Iterative-Working1,Q18_Adaptability_Iterative-Working2,Q18_Adaptability_Iterative-Working3,Q19_Adaptability_Continuous-Learning1,Q19_Adaptability_Continuous-Learning2,Q19_Adaptability_Continuous-Learning3,Q20_Problem-Statement_Problem-Definition1,Q20_Problem-Statement_Problem-Definition2,Q20_Problem-Statement_Problem-Definition3,Q21_Problem-Statement_Solution-Alignment1,Q21_Problem-Statement_Solution-Alignment2,Q21_Problem-Statement_Solution-Alignment3,Q22_Complexity_Task1,Q22_Complexity_Task2,Q22_Complexity_Task3,Q22_Complexity_Task4,Q23_Complexity_Contextual1,Q23_Complexity_Contextual2,Q23_Complexity_Contextual3,Q23_Complexity_Contextual4,Q24_Metrics1,Q24_Metrics2,Q24_Metrics3,Q25_Tools_Suitability1,Q25_Tools_Suitability2,Q25_Tools_Suitability3,Q25_Tools_Suitability4,Q26_Tools_Performance1,Q26_Tools_Performance2,Q26_Tools_Performance3,Q26_Tools_Performance4,Q27_Environment1,Q27_Environment2,Q27_Environment3,Q27_Environment4,Employee_Satisfaction,Employee_Burnout,Turnover_Intention,Employee_Net_Promoter_Score
            ,,,,Useless team name,2,,,,
            
        """.trimIndent()

        val q1TeamCompositionComplexity1Question = MultipleChoiceQuestion(
            id = QuestionId(UUID.randomUUID()),
            order = Order(value = 5),
            label = Question.Label.Q1TeamCompositionComplexity1,
            answerOptions = setOf()
        )
        val q1TeamCompositionComplexity1AnswerOption2 = AnswerOptionForAnalysis(
            id = AnswerOptionForAnalysisId(UUID.randomUUID()),
            value = ValueForAnalysis(value = "2"),
            multipleChoiceQuestion = q1TeamCompositionComplexity1Question
        )
        val multipleChoiceAnswerQ1TeamCompositionComplexity1 = MultipleChoiceAnswerForAnalysis(
            id = AnswerForAnalysisId(UUID.randomUUID()),
            answerOption = q1TeamCompositionComplexity1AnswerOption2
        )
        val questionnaireFillingForAnalysisList: List<QuestionnaireFillingForAnalysis> = listOf(
            QuestionnaireFillingForAnalysis(
                QuestionnaireFillingForAnalysisId(UUID.randomUUID()),
                answers = listOf(
                    multipleChoiceAnswerQ1TeamCompositionComplexity1
                )
            )
        )

        val actualCsvFileContent: String = buildCsvFileContent(questionnaireFillingForAnalysisList)

        assertThat(actualCsvFileContent).isEqualTo(expectedCsvFileContent)
    }
}
