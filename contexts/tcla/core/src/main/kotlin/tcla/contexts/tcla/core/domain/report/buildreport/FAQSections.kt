package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Clusters
import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.model.DriverTexts

enum class FAQSections {
    ABOUT_TCLA,
    TCLA_METHODOLOGY,
    TEAM_CHARACTERISTICS_ABOUT,
    TASK_CHARACTERISTICS_ABOUT,
    WORK_PROCESSES_PRACTICES_ABOUT,
    WORK_ENVIRONMENT_TOOLS_ABOUT,
}

object AboutTcla {
    val text = """
        #### Cognitive Load Theory, the foundation of Team Cognitive Load
        
        Cognitive load” is a 30-plus-year-old theory stating that people have trouble performing tasks and learning new things if they are trying to
        process too much information at once.
        
        #### Information Overload and Workplace Productivity
        
        A 2009 review of [information overload research](https://europepmc.org/article/med/19736853) suggested that too much information is harmful to performance. As such, employees with a high cognitive load could be less productive in the workplace.
        
        [Research from the University of North Carolina](https://pamspam.com/wp-content/uploads/2011/06/2010_CIHB_TechOverload.pdf) published in 2010 found that workers who felt overloaded with digital communications were less productive, and this decrease was especially strong for workers who
        depended on technology.
        We were likely already losing productivity to cognitive load before this rapid digital transformation, but this problem could be getting worse.

        #### The Impact of Cognitive Load in Software Engineering
        
        In software engineering for example, the complexity added by composing systems of systems, 
        the increasing and constant change enabled by agile methodologies and DevOps, the more and more powerful tools for software engineering – 
        all add to the cognitive load of developers and managers of software engineering projects.      

        #### Sustained cognitive load results in cognitive drain
        
        Cognitive overload is the cognitive equivalent of electrical overload, i.e. a higher cognitive load than what is sustainable. If we see cognitive
        overload as momentary, the longitudinal consequence would be cognitive drain. The phenomenon itself, and its consequences in a workplace
        setting are widely known - actually the reason human factors engineering was created - to figure out why perfectly functioning pilots insisted on
        crashing perfectly functioning aeroplanes. (Also see [Kirsh](https://interruptions.net/literature/Kirsh-Intellectica00-30.pdf): e.g. two thirds of managers report tension with work colleagues, and loss of job
        satisfaction because of stress associated with cognitive overload and almost two thirds (62%) of managers testify their personal relationships
        suffer as a direct result).
        
        #### Creating a positive impact on well-being and productivity by managing Cognitive Load

        Further, too much cognitive load impacts other aspects of human life, and we see that evidence in [reports (paywall)](https://www.wsj.com/articles/sunday-night-is-the-new-monday-morning-and-workers-are-miserable-11562497212) of employee burnout and
        anxiety. As such, cognitive load is not just a measure of productivity for the business, but also a measure of well-being for the individual. If
        someone hits their maximum cognitive load at work, how are they experiencing life at home, after hours or on the weekends? Keeping cognitive
        load in check is a win for both organisations and employees. But how do we do it?
    """.trimIndent()
}

object TclaMethodologyAbout {
    val text = """
        #### The drivers of Team Cognitive Load

        We have identified team cognitive load drivers capturing the root causes of unnecessary cognitive load in a work team context. Understanding
        the drivers of cognitive load, we can now look at ways to manage it. In an attempt to more broadly address the cognitive load induced in the
        workplace, we identified four clusters of drivers, namely ${Clusters.all.markUpClusterEnumeration()}.

        The rigorous measurement of cognitive load, will also enable organisations to connect individual and team cognitive load
        to outcomes of importance such as productivity and revenue, but also factors such as employee engagement and attrition.
        
        #### Measuring Team Cognitive Load drivers
        
        Team Cognitive Load is measured using 4 clusters of psychological aspects of work:
        * ${Cluster.TeamCharacteristics.markUp()}
        * ${Cluster.WorkPracticesAndProcesses.markUp()}
        * ${Cluster.TaskCharacteristics.markUp()}
        * ${Cluster.WorkEnvironmentAndTools.markUp()}

        For each cluster, specific drivers of Team Cognitive Load such as ${Driver.TaskComplexity.markUp()} or ${Driver.RoleClarity.markUp()} have been identified.

        Each driver represents a specific type of employee experience that is linked to Team Cognitive Load. The quality of each type of employee
        experience is evaluated on a scale from 1 to 5 using a set of statements.

        Below are assessment statements for each Team Cognitive Load driver:
        * ${DriverTexts.taskComplexity.capitalized}: *Tasks have a high degree of uncertainty*
        * ${DriverTexts.roleClarity.capitalized}: *I know exactly what is expected of me.*
    """.trimIndent()

}

object TeamCharacteristicsAbout {
    val text = """
        #### Composition
        * **Team Complexity** - With respect to cognitive load, the larger and more complex the team, the more transactive activities will be
        needed to coordinate members actions and the more communication is needed within the team increasing cognitive load.
        * **Team Competence** - The composition of a team in terms of the team members’ domain-specific knowledge or expertise also
        plays a role. If the current skill mix is not responding to the work demand, it likely leads to stress, frustration, low productivity and
        weak morale.

        #### Roles
        * **Member Role Clarity** - By reducing coordination activities, clear roles should reduce the cognitive load incurred.
        * **Member Role Fit** - If person-role-fit is low, employees will feel like they are always working hard to measure up, often increasing self-doubt and leading to performance anxiety. The skills the role demands are often secondary ones; and take extra energy,
        increasing cognitive load.
        * **Member Role Load** - Role overload is associated with various aspects of psychological strain, such as increased job stress,
        anxiety and a feeling of being mentally overwhelmed - all of which are linked to high levels of cognitive load.

        #### Culture
        * **Team Alignment** - In order to work effectively, teams need a clear purpose and shared meaning. Without it, team members are
        inclined to go off in different directions and do their own thing, errors and delays turn into fear and distrust, and improvements
        become arguably impossible - all of which increases cognitive load.
        * **Team Interaction** - Ineffective team interaction leads to communication gaps, which causes confusion, distrust, wastes time, and
        reduces productivity, leading to increased cognitive load.
        * **Member Psychological Safety** - A fundamental component of psychological safety is being able to speak up when you don't
        know something and to ask questions without fear of embarrassment or shame. Safely asking questions is key to reducing
        cognitive load. Even without actually needing to ask any questions, being in a psychologically safe enough environment that a
        participant knows they could ask questions should they need to, will help to improve cognition and learning capacity.
        #### Composition
        * **Team Complexity** - With respect to cognitive load, the larger and more complex the team, the more transactive activities will be
        needed to coordinate members actions and the more communication is needed within the team increasing cognitive load.
        * **Team Competence** - The composition of a team in terms of the team members’ domain-specific knowledge or expertise also
        plays a role. If the current skill mix is not responding to the work demand, it likely leads to stress, frustration, low productivity and
        weak morale.
        
        #### Roles
        * **Member Role Clarity** - By reducing coordination activities, clear roles should reduce the cognitive load incurred.
        * **Member Role Fit** - If person-role-fit is low, employees will feel like they are always working hard to measure up, often increasing self-doubt and leading to performance anxiety. The skills the role demands are often secondary ones; and take extra energy,
        increasing cognitive load.
        * **Member Role Load** - Role overload is associated with various aspects of psychological strain, such as increased job stress,
        anxiety and a feeling of being mentally overwhelmed - all of which are linked to high levels of cognitive load.
        
        #### Culture
        * **Team Alignment** - In order to work effectively, teams need a clear purpose and shared meaning. Without it, team members are
        inclined to go off in different directions and do their own thing, errors and delays turn into fear and distrust, and improvements
        become arguably impossible - all of which increases cognitive load.
        * **Team Interaction** - Ineffective team interaction leads to communication gaps, which causes confusion, distrust, wastes time, and
        reduces productivity, leading to increased cognitive load.
        * **Member Psychological Safety** - A fundamental component of psychological safety is being able to speak up when you don't
        know something and to ask questions without fear of embarrassment or shame. Safely asking questions is key to reducing
        cognitive load. Even without actually needing to ask any questions, being in a psychologically safe enough environment that a
        participant knows they could ask questions should they need to, will help to improve cognition and learning capacity.
    """.trimIndent()
}

object TaskCharacteristicsAbout {
    val text = """
        #### Problem Statement

        * **Problem Definition** - The most difficult stage in the process of solving problems is their identification (including their causes), it
        is the definition of the problem that is most important - a wrongly defined problem leads to wrong solutions (long-term or not at
        all). If members have a different understanding of the problem at hand they will be unaligned on solutions resulting in ineffective
        work processes, slow decision-making and frustration - leading to high levels of cognitive load.
        * **Solution Alignment** - Without a common understanding of the solution/deliverables may vary within the team. These
        discrepancies in expectations, ultimately lead to constant friction and with this slow/ineffective work - increasing cognitive load.

        #### Complexity

        * **Task Complexity** - Cognitive load depends on task complexity, because it is determined by the number of interacting information
        elements that have to be related, controlled, and kept active in working memory during task performance.
        * **Contextual Complexity** - Too much contextual complexity - e.g. changing requirements, external circumstances, interruptions -
        tends to result in many unexpected situations during project execution. It is known to be linked to project team conflict and
        confusion as well as low project performance.
        * **Metrics** - Agreeing on what success looks like does not mean that delivery will be successful. Without agreed upon, clearly defined
        metrics and monitoring teams (and stakeholders) will struggle to assess if the team is “on course” or not. Without clearly defined
        success metrics, it will be hard to achieve informed decision-making and resource allocation as well as maintaining credibility and
        leadership buy-in. Without, clear metrics, understanding the business value teams are driving is almost impossible, leading to
        demotivation and focus-loss.
    """.trimIndent()
}

object WorkProcessesPracticesAbout {
    val text: String = """
        #### Use of Information

        Unavailable, incomplete or unreliable information, causes team members to spend effort in searching information
        and/or asserting that information is complete and correct, increasing cognitive load.
        #### Efficiency & Effectiveness

        * **Process** - Ineffective and/or outdated work processes as well as lack of sufficient transparency, decreases team morale,
        increases frustration and slows down work and productivity - all leading to stress and high cognitive load.
        * **Consistency** - A lack of consistency and standardization creates more work, extra confusion, mistrust and worse results.
        * **Pace** - If things “drag” and time is wasted over unimportant activities, delays become more likely. Delays in delivery - caused by
        clunky slow processes - can create frustration, duplication and generate additional cost.
        * **Performance** - If teams do not produce clear benefits/value and get feedback from relevant stakeholders on this, motivation
        tends to suffer. Without positive feedback on certain performance indicators, insecurity and anxiety will increase and confidence
        will suffer.

        #### Adaptability
        * **Resilience** - If failures are hard to fix and causes of failures are hard to identify, team resilience tends to be low and
        improvement is almost impossible. Low levels of team resilience are known to lead reactional behaviour, low levels of trust and
        ultimately high levels of anxiety and stress - all of which are positively related to member cognitive load levels.
        * **Iterative Working** - Progressing iteratively with feedback is important because it allows team members to learn, and understand
        more about their current efforts enabling learning and improvements. Failing to do so, will lead to reduced confidence, stagnation
        and frustration, resulting in high levels of cognitive load.
        * **Continuous Learning** - Cognitive load theory helps us to understand how people generally learn and store new information,
        and the types of instructional practices that best support learning. Yet, this assumes that there is a "learning culture" in the first
        place. If employees are not encouraged to continuously learn new skills and practices, they are likely to remain stuck in their old
        ways of working. This, in turn is likely to increase cognitive load for themselves and others going forward.
    """.trimIndent()
}

object WorkEnvironmentToolsAbout {
    val text: String = """
        #### Tools

        * **Tool Suitability** - Tools must be suitable (fit for purpose), well understood/intuitive, consistent, and well integrated (with other
        systems). For example if tools are unintuitive, team members will find the tools hard to use, causing frustration. Inconsistent
        systems or systems that don't interact well, lead to the user to waste time and get frustrated as they repeatedly must determine
        how to solve a task, in the current context, specifically.
        * **Tool Performance** - Tools need to perform well; be responsive, reliable and stable. Cumbersome interactions with the tool - e.g.
        caused by missing functionality orimplementation that is dysfunctional/suboptimal - will force the user to waste energy doing
        what is considered unnecessary work.

        #### Environment
        
        Cognitive load is known to increase based on external factors such as physical hazards, discomfort and/or exposure to
        distracting background noise and vibration.
    """.trimIndent()
}
