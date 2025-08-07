package tcla.contexts.tcla.core.domain.report.model

object Dictionary {
    val entries: List<Entry> = listOf(
        Entry(
            id = Cluster.TeamCharacteristics.id,
            term = ClusterTexts.teamCharacteristics.capitalized,
            definition = "With respect to Team Cognitive Load, three clusters of team characteristics seem to be important, namely: team composition, the roles members have, and the culture within the team. For example, if roles are ill defined, not fit for purpose or people feel overwhelmed by their role, this will slow down processes and lead to frustration and inefficiency, increasing individual and team level of Cognitive Load levels. Similarly if the team culture is characterized by ineffective communication or low levels of psychological safety, members will feel insecure and annoyed and dissatisfied, raising Cognitive Load."
        ),
        Entry(
            id = Cluster.TaskCharacteristics.id,
            term = ClusterTexts.taskCharacteristics.capitalized,
            definition = "Task Characteristics can impact Team Cognitive Load. For example, it is well known that task complexity (of the task itself but also of the surrounding circumstances) increases Cognitive Load. Further, there is evidence that ambiguity around the problem and the right solution, as well as a lack of success metrics increases uncertainty and leads to ineffective collaboration and frustration; leading to higher Team Cognitive Load levels."
        ),
        Entry(
            id = Cluster.WorkPracticesAndProcesses.id,
            term = ClusterTexts.workPracticesProcesses.capitalized,
            definition = "Ineffective, inconsistent and slow work practices and processes will result in team's frustration, poor results and fatigue; increasing Team Cognitive Load. Similarly, if information that is used is unreliable, not relevant or accessible the team will experience delays, uncertainty and it often makes it impossible to deliver valuable results - increasing the Team Cognitive Load."
        ),
        Entry(
            id = Cluster.WorkEnvironmentAndTools.id,
            term = ClusterTexts.workEnvironmentTools.capitalized,
            definition = "The work environment can strongly affect Team Cognitive Load. For example loud noise, strong vibration, bad lightening, and potential physical discomfort or hazards will cause employees to be distracted and/or anxious, increasing their Cognitive Load. Further, using outdated, unsuitable, or low performing tools can lead to delays, sub-optimal work outputs and employees getting extremely frustrated - once again elevating Team Cognitive Load levels."
        ),
        Entry(
            id = Driver.TeamComplexity.id,
            term = DriverTexts.teamComplexity.capitalized,
            definition = "With respect to cognitive load, the larger and more complex the team, the more transactive activities will be needed to coordinate members actions and the more communication is needed within the team increasing cognitive load."
        ),
        Entry(
            id = Driver.TeamCompetence.id,
            term = DriverTexts.teamCompetence.capitalized,
            definition = "The composition of a team in terms of the team members' domain-specific knowledge or expertise also plays a role. If the current skill mix is not responding to the work demand, it likely leads to stress, frustration, low productivity and weak morale."
        ),
        Entry(
            id = Driver.RoleClarity.id,
            term = DriverTexts.roleClarity.capitalized,
            definition = "By reducing coordination activities, clear roles should reduce the cognitive load incurred."
        ),
        Entry(
            id = Driver.RoleFit.id,
            term = DriverTexts.roleFit.capitalized,
            definition = "If person-role-fit is low, employees will feel like they are always working hard to measure up, often increasing self-doubt and leading to performance anxiety. The skills the role demands are often secondary ones; and take extra energy, increasing cognitive load."
        ),
        Entry(
            id = Driver.RoleLoad.id,
            term = DriverTexts.roleLoad.capitalized,
            definition = "Role overload is associated with various aspects of psychological strain, such as increased job stress, anxiety and a feeling of being mentally overwhelmed - all of which are linked to high levels of cognitive load."
        ),
        Entry(
            id = Driver.TeamAlignment.id,
            term = DriverTexts.teamAlignment.capitalized,
            definition = "In order to work effectively, teams need a clear purpose and shared meaning. Without it, team members are inclined to go off in different directions and do their own thing, errors and delays turn into fear and distrust, and improvements become arguably impossible - all of which increases cognitive load."
        ),
        Entry(
            id = Driver.TeamInteraction.id,
            term = DriverTexts.teamInteraction.capitalized,
            definition = "Ineffective team interaction leads to communication gaps, which causes confusion, distrust, wastes time, and reduces productivity, leading to increased cognitive load."
        ),
        Entry(
            id = Driver.MemberPsychologicalSafety.id,
            term = DriverTexts.memberPsychologicalSafety.capitalized,
            definition = "A fundamental component of psychological safety is being able to speak up when you don't know something and to ask questions without fear of embarrassment or shame. Safely asking questions is key to reducing cognitive load. Even without actually needing to ask any questions, being in a psychologically safe enough environment that a participant knows they could ask questions should they need to, will help to improve cognition and learning capacity."
        ),
        Entry(
            id = Driver.ProblemDefinition.id,
            term = DriverTexts.problemDefinition.capitalized,
            definition = "The most difficult stage in the process of solving problems is their identification (including their causes), it is the definition of the problem that is most important - a wrongly defined problem leads to wrong solutions (long-term or not at all). If members have a different understanding of the problem at hand they will be unaligned on solutions resulting in ineffective work processes, slow decision-making and frustration - leading to high levels of cognitive load."
        ),
        Entry(
            id = Driver.SolutionAlignment.id,
            term = DriverTexts.solutionAlignment.capitalized,
            definition = "Without a common understanding of the solution/deliverables may vary within the team. These discrepancies in expectations, ultimately lead to constant friction and with this slow/ineffective work - increasing cognitive load."
        ),
        Entry(
            id = Driver.TaskComplexity.id,
            term = DriverTexts.taskComplexity.capitalized,
            definition = "Cognitive load depends on task complexity, because it is determined by the number of interacting information elements that have to be related, controlled, and kept active in working memory during task performance."
        ),
        Entry(
            id = Driver.ContextualComplexity.id,
            term = DriverTexts.contextualComplexity.capitalized,
            definition = "Too much contextual complexity - e.g. changing requirements, external circumstances, interruptions - tends to result in many unexpected situations during project execution. It is known to be linked to project team conflict and confusion as well as low project performance."
        ),
        Entry(
            id = Driver.Metrics.id,
            term = DriverTexts.metrics.capitalized,
            definition = "Agreeing on what success looks like does not mean that delivery will be successful. Without agreed upon, clearly defined metrics and monitoring teams (and stakeholders) will struggle to assess if the team is \"on course\" or not. Without clearly defined success metrics, it will be hard to achieve informed decision-making and resource allocation as well as maintaining credibility and leadership buy-in. Without, clear metrics, understanding the business value teams are driving is almost impossible, leading to demotivation and focus-loss."
        ),
        Entry(
            id = Driver.UseOfInformation.id,
            term = DriverTexts.useOfInformation.capitalized,
            definition = "Unavailable, incomplete or unreliable information, causes team members to spend effort in searching information and/or asserting that information is complete and correct, increasing cognitive load."
        ),
        Entry(
            id = Driver.Process.id,
            term = DriverTexts.process.capitalized,
            definition = "Ineffective and/or outdated work processes as well as lack of sufficient transparency, decreases team morale, increases frustration and slows down work and productivity - all leading to stress and high cognitive load."
        ),
        Entry(
            id = Driver.Consistency.id,
            term = DriverTexts.consistency.capitalized,
            definition = "A lack of consistency and standardization creates more work, extra confusion, mistrust and worse results."
        ),
        Entry(
            id = Driver.Pace.id,
            term = DriverTexts.pace.capitalized,
            definition = "If things \"drag\" and time is wasted over unimportant activities, delays become more likely. Delays in delivery - caused by clunky slow processes - can create frustration, duplication and generate additional cost."
        ),
        Entry(
            id = Driver.Performance.id,
            term = DriverTexts.performance.capitalized,
            definition = "If teams do not produce clear benefits/value and get feedback from relevant stakeholders on this, motivation tends to suffer. Without positive feedback on certain performance indicators, insecurity and anxiety will increase and confidence will suffer."
        ),
        Entry(
            id = Driver.Resilience.id,
            term = DriverTexts.resilience.capitalized,
            definition = "If failures are hard to fix and causes of failures are hard to identify, team resilience tends to be low and improvement is almost impossible. Low levels of team resilience are known to lead reactional behaviour, low levels of trust and ultimately high levels of anxiety and stress - all of which are positively related to member cognitive load levels."
        ),
        Entry(
            id = Driver.IterativeWorking.id,
            term = DriverTexts.iterativeWorking.capitalized,
            definition = "Progressing iteratively with feedback is important because it allows team members to learn, and understand more about their current efforts enabling learning and improvements. Failing to do so, will lead to reduced confidence, stagnation and frustration, resulting in high levels of cognitive load."
        ),
        Entry(
            id = Driver.ContinuousLearning.id,
            term = DriverTexts.continuousLearning.capitalized,
            definition = "Cognitive load theory helps us to understand how people generally learn and store new information, and the types of instructional practices that best support learning. Yet, this assumes that there is a \"learning culture\" in the first place. If employees are not encouraged to continuously learn new skills and practices, they are likely to remain stuck in their old ways of working. This, in turn is likely to increase cognitive load for themselves and others going forward."
        ),
        Entry(
            id = Driver.ToolSuitability.id,
            term = DriverTexts.toolSuitability.capitalized,
            definition = "Tools must be suitable (fit for purpose), well understood/intuitive, consistent, and well integrated (with other systems). For example if tools are unintuitive, team members will find the tools hard to use, causing frustration. Inconsistent systems or systems that don't interact well, lead to the user to waste time and get frustrated as they repeatedly must determine how to solve a task, in the current context, specifically."
        ),
        Entry(
            id = Driver.ToolPerformance.id,
            term = DriverTexts.toolPerformance.capitalized,
            definition = "Tools need to perform well; be responsive, reliable and stable. Cumbersome interactions with the tool - e.g. caused by missing functionality or implementation that is dysfunctional/suboptimal - will force the user to waste energy doing what is considered unnecessary work."
        ),
        Entry(
            id = Driver.Environment.id,
            term = DriverTexts.environment.capitalized,
            definition = "Cognitive load is known to increase based on external factors such as physical hazards, discomfort and/or exposure to distracting background noise and vibration."
        ),
        Entry(
            id = Term.TeamCognitiveLoadDriver.id,
            term = Term.TeamCognitiveLoadDriver.name,
            definition = "A Team Cognitive Load driver is a measurable fact that influences the overall Team Cognitive Load. By understanding the impact of a Team Cognitive Load driver, the team can take actions aiming to reduce that impact."
        ),
        Entry(
            id = Term.Cluster.id,
            term = Term.Cluster.name,
            definition = "A Team Cognitive Load cluster is a logical grouping of related Team Cognitive Load drivers. A cluster helps to categorize the drivers into four meaningful areas."
        )
    )
}

data class Entry(val id: String, val term: String, val definition: String)
