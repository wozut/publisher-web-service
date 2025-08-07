package tcla.contexts.tcla.core.domain.report.buildreport

import tcla.contexts.tcla.core.domain.report.model.Driver

val tclDriverAndQuestionsPairs: Set<Pair<Driver, List<String>>> = setOf(
    Pair(
        Driver.TeamComplexity,
        listOf(
            "Is the team quite large (10+ members)?",
            "Does the team support multiple stakeholders with different needs and expectations?",
        )
    ),
    Pair(
        Driver.TeamCompetence,
        listOf(
            "Does the team have the right skill mix for their mission?",
            "Does the team have the experience levels required to perform their tasks?"
        )
    ),
    Pair(
        Driver.RoleClarity,
        listOf(
            "Are the team’s roles and responsibilities clearly defined?",
            "Do team members feel like they know what is expected from them?"
        )
    ),
    Pair(
        Driver.RoleFit,
        listOf(
            "Do team members feel comfortable with their skills contribution to the team?",
            "Do team members feel able to fully handle the demands of their job?"
        )
    ),
    Pair(
        Driver.RoleLoad,
        listOf(
            "Do team members feel that they need to juggle too many things?",
            "Do team members feel they are constantly under time pressure?"
        )
    ),
    Pair(
        Driver.TeamAlignment,
        listOf(
            "Does everyone in the team agree on what the team’s purpose is?",
            "Does the team share a sense of ownership of what they deliver?",
            "Does the team have a clear view on what success looks like?"
        )
    ),
    Pair(
        Driver.TeamInteraction,
        listOf(
            "Has the team agreed on internal communication patterns?",
            "Do team members feel like they communicate enough?",
            "Do team members feel like they communicate too much?",
            "Do team members actively share knowledge or only when strictly necessary?"
        )
    ),
    Pair(
        Driver.MemberPsychologicalSafety,
        listOf(
            "Are team members able to challenge the status quo?",
            "Are team members afraid to make mistakes?",
            "Are team members comfortable asking for help?"
        )
    ),
    Pair(
        Driver.ProblemDefinition,
        listOf(
            "Can the team describe the problem they are solving?",
            "Does the team have a shared understanding of the problem or opportunity they are tackling?",
            "Are problems described in a concise manner?"
        )
    ),
    Pair(
        Driver.SolutionAlignment,
        listOf(
            "Can the team describe what success looks like?",
            "Do the team members have an understanding of the expectations on what has to be delivered?",
            "Can the team members describe what are the results after a solution has been implemented?"
        )
    ),
    Pair(
        Driver.TaskComplexity,
        listOf(
            "Is the team task execution blocked by other teams?",
            "How does the team deals with tasks with a high degree of uncertainty?",
            "What techniques and practices does the team uses to tackle tasks that have different mental demands?"
        )
    ),
    Pair(
        Driver.ContextualComplexity,
        listOf(
            "Is the environment around the team constantly changing?",
            "Is the team able to clearly map out their operating context?",
            "How does the team manage a fast pace of change?"
        )
    ),
    Pair(
        Driver.Metrics,
        listOf(
            "Can the team make decisions based on metrics?",
            "How accurate are the metrics used by the team?",
            "Are the metrics shared between the team and stakeholders?",
            "Can the team and the stakeholders evolve the metrics to be used?",
            "Are the metrics linked to a business outcome?"
        )
    ),
    Pair(
        Driver.UseOfInformation,
        listOf(
            "How easy is it for the team members to find the relevant information to do their job?",
            "How reliable is that information?"
        )
    ),
    Pair(
        Driver.Process,
        listOf(
            "Does the team have work processes that allow fast flow?",
            "Is the team able to evolve their practices to achieve fast flow?",
            "Does the team documents and broadcast their practices?",
            "Is the team able to find and get inspiration from other team’s practices?"
        )
    ),
    Pair(
        Driver.Consistency,
        listOf(
            "Does the team create common ways of working?",
            "Are the working processes consistent for things that are well-known?",
            "Does the team seek alignment when they discover new ways of doing things?"
        )
    ),
    Pair(
        Driver.Pace,
        listOf(
            "Is the team able to focus on important tasks?",
            "Does the team have a healthy balance between tasks with different priorities?",
            "Is the team able to create a steady pace for their work?",
            "Is the team able to identify if there are delays to deliver their work?",
            "Is the team able to proactively remove delays that affect their work?"
        )
    ),
    Pair(
        Driver.Performance,
        listOf(
            "Does the team celebrates when things are delivered?",
            "Are the stakeholders involved when the team celebrates their successes?",
            "Does the team feels they produce measurable results?"
        )
    ),
    Pair(
        Driver.Resilience,
        listOf(
            "Are the team members stressed when things fail?",
            "Is the team able to quickly identify things when they fail?",
            "Does the team have the necessary tools to facilitate finding things that fail?",
            "Is the team able to quickly fix what is failing?"
        )
    ),
    Pair(
        Driver.IterativeWorking,
        listOf(
            "Does the team reserve and use the reserved time to improve their ways of working?",
            "Does the team have ways to have continuous feedback?",
            "Is the team able to share their progress with stakeholders and improve along the way?"
        )
    ),
    Pair(
        Driver.ContinuousLearning,
        listOf(
            "Are team members encouraged to learn new skills, techniques or practices?",
            "Does the team consciously learn from their daily collaboration?",
            "Is learning a goal of team members work?"
        )
    ),
    Pair(
        Driver.ToolSuitability,
        listOf(
            "Do the team members have access to tools that suit their tasks and preferences?",
            "Does the tool stack evolve to accommodate new needs of the team members?",
            "Do the team members understand how the tools work?",
            "Are the tools properly integrated into the workflow?",
            "Do the team members use tools that remove redundant and/or manual work?"
        )
    ),
    Pair(
        Driver.ToolPerformance,
        listOf(
            "Are the tools used by the team stable?",
            "Are the tools used by the team reliable?",
            "Are the tools responsive and perform well?"
        )
    ),
    Pair(
        Driver.Environment,
        listOf(
            "Does the team have adequate conditions to carry their work?",
            "Does the team members' workspace fits their work?",
            "Are there distractions (noise, vibration) that affect the team's work?"
        )
    )
)
