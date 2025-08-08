package tcla.contexts.tcla.database.springdatajpa.question

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.question.model.Question

fun String.toDomainLabel(): Either<Failure, Question.Label> = when(this) {
    gender -> Question.Label.Gender.right()
    modeOfWorking -> Question.Label.ModeOfWorking.right()
    q10CultureMemberPsychologicalSafetySpeakingUp1 -> Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp1.right()
    q10CultureMemberPsychologicalSafetySpeakingUp2 -> Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp2.right()
    q10CultureMemberPsychologicalSafetySpeakingUp3 -> Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp3.right()
    q11CultureMemberPsychologicalSafetyEmbracingFailure1 -> Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure1.right()
    q11CultureMemberPsychologicalSafetyEmbracingFailure2 -> Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure2.right()
    q11CultureMemberPsychologicalSafetyEmbracingFailure3 -> Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure3.right()
    q12UseOfInformation1 -> Question.Label.Q12UseOfInformation1.right()
    q12UseOfInformation2 -> Question.Label.Q12UseOfInformation2.right()
    q12UseOfInformation3 -> Question.Label.Q12UseOfInformation3.right()
    q13EfficiencyAndEffectivenessProcess1 -> Question.Label.Q13EfficiencyAndEffectivenessProcess1.right()
    q13EfficiencyAndEffectivenessProcess2 -> Question.Label.Q13EfficiencyAndEffectivenessProcess2.right()
    q13EfficiencyAndEffectivenessProcess3 -> Question.Label.Q13EfficiencyAndEffectivenessProcess3.right()
    q14EfficiencyAndEffectivenessConsistency1 -> Question.Label.Q14EfficiencyAndEffectivenessConsistency1.right()
    q14EfficiencyAndEffectivenessConsistency2 -> Question.Label.Q14EfficiencyAndEffectivenessConsistency2.right()
    q14EfficiencyAndEffectivenessConsistency3 -> Question.Label.Q14EfficiencyAndEffectivenessConsistency3.right()
    q15EfficiencyAndEffectivenessPace1 -> Question.Label.Q15EfficiencyAndEffectivenessPace1.right()
    q15EfficiencyAndEffectivenessPace2 -> Question.Label.Q15EfficiencyAndEffectivenessPace2.right()
    q15EfficiencyAndEffectivenessPace3 -> Question.Label.Q15EfficiencyAndEffectivenessPace3.right()
    q16EfficiencyAndEffectivenessPerformance1 -> Question.Label.Q16EfficiencyAndEffectivenessPerformance1.right()
    q16EfficiencyAndEffectivenessPerformance2 -> Question.Label.Q16EfficiencyAndEffectivenessPerformance2.right()
    q16EfficiencyAndEffectivenessPerformance3 -> Question.Label.Q16EfficiencyAndEffectivenessPerformance3.right()
    q17AdaptabilityResilience1 -> Question.Label.Q17AdaptabilityResilience1.right()
    q17AdaptabilityResilience2 -> Question.Label.Q17AdaptabilityResilience2.right()
    q17AdaptabilityResilience3 -> Question.Label.Q17AdaptabilityResilience3.right()
    q18AdaptabilityIterativeWorking1 -> Question.Label.Q18AdaptabilityIterativeWorking1.right()
    q18AdaptabilityIterativeWorking2 -> Question.Label.Q18AdaptabilityIterativeWorking2.right()
    q18AdaptabilityIterativeWorking3 -> Question.Label.Q18AdaptabilityIterativeWorking3.right()
    q19AdaptabilityContinuousLearning1 -> Question.Label.Q19AdaptabilityContinuousLearning1.right()
    q19AdaptabilityContinuousLearning2 -> Question.Label.Q19AdaptabilityContinuousLearning2.right()
    q19AdaptabilityContinuousLearning3 -> Question.Label.Q19AdaptabilityContinuousLearning3.right()
    q1TeamCompositionComplexity1 -> Question.Label.Q1TeamCompositionComplexity1.right()
    q1TeamCompositionComplexity2 -> Question.Label.Q1TeamCompositionComplexity2.right()
    q1TeamCompositionComplexity3 -> Question.Label.Q1TeamCompositionComplexity3.right()
    q20ProblemStatementProblemDefinition1 -> Question.Label.Q20ProblemStatementProblemDefinition1.right()
    q20ProblemStatementProblemDefinition2 -> Question.Label.Q20ProblemStatementProblemDefinition2.right()
    q20ProblemStatementProblemDefinition3 -> Question.Label.Q20ProblemStatementProblemDefinition3.right()
    q21ProblemStatementSolutionAlignment1 -> Question.Label.Q21ProblemStatementSolutionAlignment1.right()
    q21ProblemStatementSolutionAlignment2 -> Question.Label.Q21ProblemStatementSolutionAlignment2.right()
    q21ProblemStatementSolutionAlignment3 -> Question.Label.Q21ProblemStatementSolutionAlignment3.right()
    q22ComplexityTask1 -> Question.Label.Q22ComplexityTask1.right()
    q22ComplexityTask2 -> Question.Label.Q22ComplexityTask2.right()
    q22ComplexityTask3 -> Question.Label.Q22ComplexityTask3.right()
    q22ComplexityTask4 -> Question.Label.Q22ComplexityTask4.right()
    q23ComplexityContextual1 -> Question.Label.Q23ComplexityContextual1.right()
    q23ComplexityContextual2 -> Question.Label.Q23ComplexityContextual2.right()
    q23ComplexityContextual3 -> Question.Label.Q23ComplexityContextual3.right()
    q23ComplexityContextual4 -> Question.Label.Q23ComplexityContextual4.right()
    q24Metrics1 -> Question.Label.Q24Metrics1.right()
    q24Metrics2 -> Question.Label.Q24Metrics2.right()
    q24Metrics3 -> Question.Label.Q24Metrics3.right()
    q25ToolsSuitability1 -> Question.Label.Q25ToolsSuitability1.right()
    q25ToolsSuitability2 -> Question.Label.Q25ToolsSuitability2.right()
    q25ToolsSuitability3 -> Question.Label.Q25ToolsSuitability3.right()
    q25ToolsSuitability4 -> Question.Label.Q25ToolsSuitability4.right()
    q26ToolsPerformance1 -> Question.Label.Q26ToolsPerformance1.right()
    q26ToolsPerformance2 -> Question.Label.Q26ToolsPerformance2.right()
    q26ToolsPerformance3 -> Question.Label.Q26ToolsPerformance3.right()
    q26ToolsPerformance4 -> Question.Label.Q26ToolsPerformance4.right()
    q27Environment1 -> Question.Label.Q27Environment1.right()
    q27Environment2 -> Question.Label.Q27Environment2.right()
    q27Environment3 -> Question.Label.Q27Environment3.right()
    q27Environment4 -> Question.Label.Q27Environment4.right()
    q2TeamCompositionCompetence1 -> Question.Label.Q2TeamCompositionCompetence1.right()
    q2TeamCompositionCompetence2 -> Question.Label.Q2TeamCompositionCompetence2.right()
    q2TeamCompositionCompetence3 -> Question.Label.Q2TeamCompositionCompetence3.right()
    q3MemberRolesRoleClarity1 -> Question.Label.Q3MemberRolesRoleClarity1.right()
    q3MemberRolesRoleClarity2 -> Question.Label.Q3MemberRolesRoleClarity2.right()
    q3MemberRolesRoleClarity3 -> Question.Label.Q3MemberRolesRoleClarity3.right()
    q4MemberRolesRoleFit1 -> Question.Label.Q4MemberRolesRoleFit1.right()
    q4MemberRolesRoleFit2 -> Question.Label.Q4MemberRolesRoleFit2.right()
    q4MemberRolesRoleFit3 -> Question.Label.Q4MemberRolesRoleFit3.right()
    q5MemberRolesRoleLoad1 -> Question.Label.Q5MemberRolesRoleLoad1.right()
    q5MemberRolesRoleLoad2 -> Question.Label.Q5MemberRolesRoleLoad2.right()
    q5MemberRolesRoleLoad3 -> Question.Label.Q5MemberRolesRoleLoad3.right()
    q6CultureTeamAlignment1 -> Question.Label.Q6CultureTeamAlignment1.right()
    q6CultureTeamAlignment2 -> Question.Label.Q6CultureTeamAlignment2.right()
    q6CultureTeamAlignment3 -> Question.Label.Q6CultureTeamAlignment3.right()
    q7CultureTeamInteractionCommunication1 -> Question.Label.Q7CultureTeamInteractionCommunication1.right()
    q7CultureTeamInteractionCommunication2 -> Question.Label.Q7CultureTeamInteractionCommunication2.right()
    q7CultureTeamInteractionCommunication3 -> Question.Label.Q7CultureTeamInteractionCommunication3.right()
    q8CultureTeamInteractionKnowledgeExchange1 -> Question.Label.Q8CultureTeamInteractionKnowledgeExchange1.right()
    q8CultureTeamInteractionKnowledgeExchange2 -> Question.Label.Q8CultureTeamInteractionKnowledgeExchange2.right()
    q8CultureTeamInteractionKnowledgeExchange3 -> Question.Label.Q8CultureTeamInteractionKnowledgeExchange3.right()
    q9CultureMemberPsychologicalSafetyAuthenticity1 -> Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity1.right()
    q9CultureMemberPsychologicalSafetyAuthenticity2 -> Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity2.right()
    q9CultureMemberPsychologicalSafetyAuthenticity3 -> Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity3.right()
    teamFamiliarity -> Question.Label.TeamFamiliarity.right()
    workFamiliarity -> Question.Label.WorkFamiliarity.right()
    else -> Failure.UnableToTransformIntoDomainData.QuestionLabel.left()
}

private val gender: String = Question.Label.Gender.value
private val modeOfWorking: String = Question.Label.ModeOfWorking.value
private val q10CultureMemberPsychologicalSafetySpeakingUp1: String = Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp1.value
private val q10CultureMemberPsychologicalSafetySpeakingUp2: String = Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp2.value
private val q10CultureMemberPsychologicalSafetySpeakingUp3: String = Question.Label.Q10CultureMemberPsychologicalSafetySpeakingUp3.value
private val q11CultureMemberPsychologicalSafetyEmbracingFailure1: String = Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure1.value
private val q11CultureMemberPsychologicalSafetyEmbracingFailure2: String = Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure2.value
private val q11CultureMemberPsychologicalSafetyEmbracingFailure3: String = Question.Label.Q11CultureMemberPsychologicalSafetyEmbracingFailure3.value
private val q12UseOfInformation1: String = Question.Label.Q12UseOfInformation1.value
private val q12UseOfInformation2: String = Question.Label.Q12UseOfInformation2.value
private val q12UseOfInformation3: String = Question.Label.Q12UseOfInformation3.value
private val q13EfficiencyAndEffectivenessProcess1: String = Question.Label.Q13EfficiencyAndEffectivenessProcess1.value
private val q13EfficiencyAndEffectivenessProcess2: String = Question.Label.Q13EfficiencyAndEffectivenessProcess2.value
private val q13EfficiencyAndEffectivenessProcess3: String = Question.Label.Q13EfficiencyAndEffectivenessProcess3.value
private val q14EfficiencyAndEffectivenessConsistency1: String = Question.Label.Q14EfficiencyAndEffectivenessConsistency1.value
private val q14EfficiencyAndEffectivenessConsistency2: String = Question.Label.Q14EfficiencyAndEffectivenessConsistency2.value
private val q14EfficiencyAndEffectivenessConsistency3: String = Question.Label.Q14EfficiencyAndEffectivenessConsistency3.value
private val q15EfficiencyAndEffectivenessPace1: String = Question.Label.Q15EfficiencyAndEffectivenessPace1.value
private val q15EfficiencyAndEffectivenessPace2: String = Question.Label.Q15EfficiencyAndEffectivenessPace2.value
private val q15EfficiencyAndEffectivenessPace3: String = Question.Label.Q15EfficiencyAndEffectivenessPace3.value
private val q16EfficiencyAndEffectivenessPerformance1: String = Question.Label.Q16EfficiencyAndEffectivenessPerformance1.value
private val q16EfficiencyAndEffectivenessPerformance2: String = Question.Label.Q16EfficiencyAndEffectivenessPerformance2.value
private val q16EfficiencyAndEffectivenessPerformance3: String = Question.Label.Q16EfficiencyAndEffectivenessPerformance3.value
private val q17AdaptabilityResilience1: String = Question.Label.Q17AdaptabilityResilience1.value
private val q17AdaptabilityResilience2: String = Question.Label.Q17AdaptabilityResilience2.value
private val q17AdaptabilityResilience3: String = Question.Label.Q17AdaptabilityResilience3.value
private val q18AdaptabilityIterativeWorking1: String = Question.Label.Q18AdaptabilityIterativeWorking1.value
private val q18AdaptabilityIterativeWorking2: String = Question.Label.Q18AdaptabilityIterativeWorking2.value
private val q18AdaptabilityIterativeWorking3: String = Question.Label.Q18AdaptabilityIterativeWorking3.value
private val q19AdaptabilityContinuousLearning1: String = Question.Label.Q19AdaptabilityContinuousLearning1.value
private val q19AdaptabilityContinuousLearning2: String = Question.Label.Q19AdaptabilityContinuousLearning2.value
private val q19AdaptabilityContinuousLearning3: String = Question.Label.Q19AdaptabilityContinuousLearning3.value
private val q1TeamCompositionComplexity1: String = Question.Label.Q1TeamCompositionComplexity1.value
private val q1TeamCompositionComplexity2: String = Question.Label.Q1TeamCompositionComplexity2.value
private val q1TeamCompositionComplexity3: String = Question.Label.Q1TeamCompositionComplexity3.value
private val q20ProblemStatementProblemDefinition1: String = Question.Label.Q20ProblemStatementProblemDefinition1.value
private val q20ProblemStatementProblemDefinition2: String = Question.Label.Q20ProblemStatementProblemDefinition2.value
private val q20ProblemStatementProblemDefinition3: String = Question.Label.Q20ProblemStatementProblemDefinition3.value
private val q21ProblemStatementSolutionAlignment1: String = Question.Label.Q21ProblemStatementSolutionAlignment1.value
private val q21ProblemStatementSolutionAlignment2: String = Question.Label.Q21ProblemStatementSolutionAlignment2.value
private val q21ProblemStatementSolutionAlignment3: String = Question.Label.Q21ProblemStatementSolutionAlignment3.value
private val q22ComplexityTask1: String = Question.Label.Q22ComplexityTask1.value
private val q22ComplexityTask2: String = Question.Label.Q22ComplexityTask2.value
private val q22ComplexityTask3: String = Question.Label.Q22ComplexityTask3.value
private val q22ComplexityTask4: String = Question.Label.Q22ComplexityTask4.value
private val q23ComplexityContextual1: String = Question.Label.Q23ComplexityContextual1.value
private val q23ComplexityContextual2: String = Question.Label.Q23ComplexityContextual2.value
private val q23ComplexityContextual3: String = Question.Label.Q23ComplexityContextual3.value
private val q23ComplexityContextual4: String = Question.Label.Q23ComplexityContextual4.value
private val q24Metrics1: String = Question.Label.Q24Metrics1.value
private val q24Metrics2: String = Question.Label.Q24Metrics2.value
private val q24Metrics3: String = Question.Label.Q24Metrics3.value
private val q25ToolsSuitability1: String = Question.Label.Q25ToolsSuitability1.value
private val q25ToolsSuitability2: String = Question.Label.Q25ToolsSuitability2.value
private val q25ToolsSuitability3: String = Question.Label.Q25ToolsSuitability3.value
private val q25ToolsSuitability4: String = Question.Label.Q25ToolsSuitability4.value
private val q26ToolsPerformance1: String = Question.Label.Q26ToolsPerformance1.value
private val q26ToolsPerformance2: String = Question.Label.Q26ToolsPerformance2.value
private val q26ToolsPerformance3: String = Question.Label.Q26ToolsPerformance3.value
private val q26ToolsPerformance4: String = Question.Label.Q26ToolsPerformance4.value
private val q27Environment1: String = Question.Label.Q27Environment1.value
private val q27Environment2: String = Question.Label.Q27Environment2.value
private val q27Environment3: String = Question.Label.Q27Environment3.value
private val q27Environment4: String = Question.Label.Q27Environment4.value
private val q2TeamCompositionCompetence1: String = Question.Label.Q2TeamCompositionCompetence1.value
private val q2TeamCompositionCompetence2: String = Question.Label.Q2TeamCompositionCompetence2.value
private val q2TeamCompositionCompetence3: String = Question.Label.Q2TeamCompositionCompetence3.value
private val q3MemberRolesRoleClarity1: String = Question.Label.Q3MemberRolesRoleClarity1.value
private val q3MemberRolesRoleClarity2: String = Question.Label.Q3MemberRolesRoleClarity2.value
private val q3MemberRolesRoleClarity3: String = Question.Label.Q3MemberRolesRoleClarity3.value
private val q4MemberRolesRoleFit1: String = Question.Label.Q4MemberRolesRoleFit1.value
private val q4MemberRolesRoleFit2: String = Question.Label.Q4MemberRolesRoleFit2.value
private val q4MemberRolesRoleFit3: String = Question.Label.Q4MemberRolesRoleFit3.value
private val q5MemberRolesRoleLoad1: String = Question.Label.Q5MemberRolesRoleLoad1.value
private val q5MemberRolesRoleLoad2: String = Question.Label.Q5MemberRolesRoleLoad2.value
private val q5MemberRolesRoleLoad3: String = Question.Label.Q5MemberRolesRoleLoad3.value
private val q6CultureTeamAlignment1: String = Question.Label.Q6CultureTeamAlignment1.value
private val q6CultureTeamAlignment2: String = Question.Label.Q6CultureTeamAlignment2.value
private val q6CultureTeamAlignment3: String = Question.Label.Q6CultureTeamAlignment3.value
private val q7CultureTeamInteractionCommunication1: String = Question.Label.Q7CultureTeamInteractionCommunication1.value
private val q7CultureTeamInteractionCommunication2: String = Question.Label.Q7CultureTeamInteractionCommunication2.value
private val q7CultureTeamInteractionCommunication3: String = Question.Label.Q7CultureTeamInteractionCommunication3.value
private val q8CultureTeamInteractionKnowledgeExchange1: String = Question.Label.Q8CultureTeamInteractionKnowledgeExchange1.value
private val q8CultureTeamInteractionKnowledgeExchange2: String = Question.Label.Q8CultureTeamInteractionKnowledgeExchange2.value
private val q8CultureTeamInteractionKnowledgeExchange3: String = Question.Label.Q8CultureTeamInteractionKnowledgeExchange3.value
private val q9CultureMemberPsychologicalSafetyAuthenticity1: String = Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity1.value
private val q9CultureMemberPsychologicalSafetyAuthenticity2: String = Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity2.value
private val q9CultureMemberPsychologicalSafetyAuthenticity3: String = Question.Label.Q9CultureMemberPsychologicalSafetyAuthenticity3.value
private val teamFamiliarity: String = Question.Label.TeamFamiliarity.value
private val workFamiliarity: String = Question.Label.WorkFamiliarity.value
