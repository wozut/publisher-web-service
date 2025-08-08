package tcla.contexts.tcla.core.domain.questiontothinkabout.model

import tcla.contexts.tcla.core.domain.questionstothinkaboutmap.model.QuestionsToThinkAboutMap
import tcla.contexts.tcla.core.domain.report.model.Driver
import java.util.UUID

data class QuestionToThinkAbout(val id: Id, val text: Text) {

    data class Id(val value: UUID)
    data class Text(val value: String)
}

private val map: Map<Driver, List<QuestionToThinkAbout>> = mapOf(
    Pair(
        Driver.TeamComplexity,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("4f43f0e0-a35d-4e29-aeab-40728d4b1907")),
                text = QuestionToThinkAbout.Text("Is the team quite large (10+ members)?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("20c20605-1642-4980-a359-081df618ef6e")),
                text = QuestionToThinkAbout.Text("Does the team support multiple stakeholders with different needs and expectations?")
            )
        )
    ),
    Pair(
        Driver.TeamCompetence,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("2f378614-b15f-49aa-8ca3-80ceebc94ef5")),
                text = QuestionToThinkAbout.Text("Does the team have the right skill mix for their mission?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("24e1da0d-920d-4409-83d9-df2a9f660b10")),
                text = QuestionToThinkAbout.Text("Does the team have the experience levels required to perform their tasks?")
            )
        )
    ),
    Pair(
        Driver.RoleClarity,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("e21079fe-ba73-4a85-b91d-26c3faf86379")),
                text = QuestionToThinkAbout.Text("Are the team’s roles and responsibilities clearly defined?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("df010b3a-3e81-4f03-b3c9-b82f6cbb30ad")),
                text = QuestionToThinkAbout.Text("Do team members feel like they know what is expected from them?")
            )
        )
    ),
    Pair(
        Driver.RoleFit,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("94af85e1-d24d-40fe-b1ad-2354001772b1")),
                text = QuestionToThinkAbout.Text("Do team members feel comfortable with their skills contribution to the team?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5519499a-f482-4259-9af4-63d9cfad433b")),
                text = QuestionToThinkAbout.Text("Do team members feel able to fully handle the demands of their job?")
            )
        )
    ),
    Pair(
        Driver.RoleLoad,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("7934cb7b-baf9-4e8b-b87a-269173b0bcc8")),
                text = QuestionToThinkAbout.Text("Do team members feel that they need to juggle too many things?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("2b51da16-2b80-4519-8b8f-2d2301769d22")),
                text = QuestionToThinkAbout.Text("Do team members feel they are constantly under time pressure?")
            )
        )
    ),
    Pair(
        Driver.TeamAlignment,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("3b8ee219-d89f-4219-ae5b-7c59a09c54e7")),
                text = QuestionToThinkAbout.Text("Does everyone in the team agree on what the team’s purpose is?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("550bf276-bb96-4da7-b070-c80370f6a9b1")),
                text = QuestionToThinkAbout.Text("Does the team share a sense of ownership of what they deliver?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("06e71081-1cd1-4e4d-b3cb-2422be3d1186")),
                text = QuestionToThinkAbout.Text("Does the team have a clear view on what success looks like?")
            )
        )
    ),
    Pair(
        Driver.TeamInteraction,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("ab56312c-e771-4adf-afe9-189b3983a5a0")),
                text = QuestionToThinkAbout.Text("Has the team agreed on internal communication patterns?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("257bc4ca-0573-4b0f-a43e-c32e848f536d")),
                text = QuestionToThinkAbout.Text("Do team members feel like they communicate enough?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("d1c43c51-161c-44d7-8614-5f09b7123072")),
                text = QuestionToThinkAbout.Text("Do team members feel like they communicate too much?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("3c2a5ad3-11b2-49a7-91bb-5e743cf4289f")),
                text = QuestionToThinkAbout.Text("Do team members actively share knowledge or only when strictly necessary?")
            )
        )
    ),
    Pair(
        Driver.MemberPsychologicalSafety,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5a1fdcc2-7ff1-49ae-b573-611cd9896d96")),
                text = QuestionToThinkAbout.Text("Are team members able to challenge the status quo?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("aed5331a-280d-4eac-86b4-1f6ffefae531")),
                text = QuestionToThinkAbout.Text("Are team members afraid to make mistakes?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("81b7e2de-27a5-4fe0-8a6d-0af7a61cec89")),
                text = QuestionToThinkAbout.Text("Are team members comfortable asking for help?")
            )
        )
    ),
    Pair(
        Driver.ProblemDefinition,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("160adb5c-0b9f-42a9-b2b1-5eee826c82ce")),
                text = QuestionToThinkAbout.Text("Can the team describe the problem they are solving?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("dab26970-6022-4505-acff-dedd93a57e83")),
                text = QuestionToThinkAbout.Text("Does the team have a shared understanding of the problem or opportunity they are tackling?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("8dda3af8-e6af-4fb1-b943-d7d0e97195fa")),
                text = QuestionToThinkAbout.Text("Are problems described in a concise manner?")
            )
        )
    ),
    Pair(
        Driver.SolutionAlignment,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("a67e4607-a1a2-4168-8e77-22f4bb7358c0")),
                text = QuestionToThinkAbout.Text("Can the team describe what success looks like?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("b36e0d08-33f4-4a78-99c9-35d71fafde8e")),
                text = QuestionToThinkAbout.Text("Do the team members have an understanding of the expectations on what has to be delivered?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("6d3b6756-5e6f-4f47-87bc-8ea06d017e20")),
                text = QuestionToThinkAbout.Text("Can the team members describe what are the results after a solution has been implemented?")
            )
        )
    ),
    Pair(
        Driver.TaskComplexity,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("31afce92-5f72-4759-a47a-de0a89ea9d61")),
                text = QuestionToThinkAbout.Text("Is the team task execution blocked by other teams?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("39406de1-df5f-4c16-8636-82ca9b104823")),
                text = QuestionToThinkAbout.Text("How does the team deals with tasks with a high degree of uncertainty?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("e7bfa6bb-7026-4555-8ef4-4ae11b2f3913")),
                text = QuestionToThinkAbout.Text("What techniques and practices does the team uses to tackle tasks that have different mental demands?")
            )
        )
    ),
    Pair(
        Driver.ContextualComplexity,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("c4797703-7a7f-4672-b46c-2ec10d46c813")),
                text = QuestionToThinkAbout.Text("Is the environment around the team constantly changing?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("007da0b5-de62-44a5-b7b2-b73b85dae0ba")),
                text = QuestionToThinkAbout.Text("Is the team able to clearly map out their operating context?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("2fe8830e-e21b-4491-80cc-ef4988e8eb24")),
                text = QuestionToThinkAbout.Text("How does the team manage a fast pace of change?")
            )
        )
    ),
    Pair(
        Driver.Metrics,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5f02cbec-0c85-41c0-85ac-04b811e8a268")),
                text = QuestionToThinkAbout.Text("Can the team make decisions based on metrics?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("91ecff32-64da-4b05-ab0c-9ae320df4dce")),
                text = QuestionToThinkAbout.Text("How accurate are the metrics used by the team?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("2c9f40e2-a7c7-461a-ac24-02d365dbedac")),
                text = QuestionToThinkAbout.Text("Are the metrics shared between the team and stakeholders?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("6b3de740-ead4-4374-919f-6e70e096ac44")),
                text = QuestionToThinkAbout.Text("Can the team and the stakeholders evolve the metrics to be used?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("56a5a1a2-f28d-4089-abb3-17fe8d70f191")),
                text = QuestionToThinkAbout.Text("Are the metrics linked to a business outcome?")
            )
        )
    ),
    Pair(
        Driver.UseOfInformation,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("8cbed545-fd88-4405-9774-f12912264a71")),
                text = QuestionToThinkAbout.Text("How easy is it for the team members to find the relevant information to do their job?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5d98a677-62da-4e8b-8fbe-216077f704b7")),
                text = QuestionToThinkAbout.Text("How reliable is that information?")
            )
        )
    ),
    Pair(
        Driver.Process,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5c5d5ae8-cb4e-4314-9087-34ca999db96c")),
                text = QuestionToThinkAbout.Text("Does the team have work processes that allow fast flow?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5aedc622-4dbc-4a4f-b76b-8b0bf1b86472")),
                text = QuestionToThinkAbout.Text("Is the team able to evolve their practices to achieve fast flow?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("6a46d73c-d450-415b-91d2-4c7317c8afd0")),
                text = QuestionToThinkAbout.Text("Does the team documents and broadcast their practices?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5e93d0dc-14cb-4b74-b929-b4e0418ff21c")),
                text = QuestionToThinkAbout.Text("Is the team able to find and get inspiration from other team’s practices?")
            )
        )
    ),
    Pair(
        Driver.Consistency,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("0f559f45-d946-4b78-ad73-b5106ea2a336")),
                text = QuestionToThinkAbout.Text("Does the team create common ways of working?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("873782ad-8a32-4ee8-a79d-4930249a8d2c")),
                text = QuestionToThinkAbout.Text("Are the working processes consistent for things that are well-known?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5c738945-cd97-4827-b555-3923d68488af")),
                text = QuestionToThinkAbout.Text("Does the team seek alignment when they discover new ways of doing things?")
            )
        )
    ),
    Pair(
        Driver.Pace,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("972d1ab8-fc98-4c92-ac1a-70babdfc46ab")),
                text = QuestionToThinkAbout.Text("Is the team able to focus on important tasks?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("bfafa118-e6a9-4f38-a672-7360bec52c20")),
                text = QuestionToThinkAbout.Text("Does the team have a healthy balance between tasks with different priorities?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("6194b434-5da6-40dd-9f47-f463525a8e31")),
                text = QuestionToThinkAbout.Text("Is the team able to create a steady pace for their work?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("9715b6cf-ecb3-438a-96a2-2a272bf1d5c0")),
                text = QuestionToThinkAbout.Text("Is the team able to identify if there are delays to deliver their work?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("409e59c6-6bf3-4902-9efb-3837634ecf66")),
                text = QuestionToThinkAbout.Text("Is the team able to proactively remove delays that affect their work?")
            )
        )
    ),
    Pair(
        Driver.Performance,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("2d3975f9-670f-443c-a50c-116c284231ba")),
                text = QuestionToThinkAbout.Text("Does the team celebrates when things are delivered?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("eff7b020-4983-47e6-914f-c996bb620cb5")),
                text = QuestionToThinkAbout.Text("Are the stakeholders involved when the team celebrates their successes?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("6d498e61-1dd8-4fa8-8096-6cd5b6671fb2")),
                text = QuestionToThinkAbout.Text("Does the team feels they produce measurable results?")
            )
        )
    ),
    Pair(
        Driver.Resilience,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("26f5674d-ca05-4255-b930-ca7211de40c2")),
                text = QuestionToThinkAbout.Text("Are the team members stressed when things fail?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("e0f41405-283e-46fb-9c3c-a4325b4c8418")),
                text = QuestionToThinkAbout.Text("Is the team able to quickly identify things when they fail?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("ce946fc4-3a0c-41dd-9d36-79170f0e4270")),
                text = QuestionToThinkAbout.Text("Does the team have the necessary tools to facilitate finding things that fail?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("fa8a0650-75ed-4fa3-abae-8b71b8fce350")),
                text = QuestionToThinkAbout.Text("Is the team able to quickly fix what is failing?")
            )
        )
    ),
    Pair(
        Driver.IterativeWorking,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("c4e2f239-8c10-4280-81b8-23308b5e6965")),
                text = QuestionToThinkAbout.Text("Does the team reserve and use the reserved time to improve their ways of working?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5da7f269-7898-4cd9-9bf4-e87566e2b1d4")),
                text = QuestionToThinkAbout.Text("Does the team have ways to have continuous feedback?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("ce1c8f11-5a5f-474d-9106-29ec754c1422")),
                text = QuestionToThinkAbout.Text("Is the team able to share their progress with stakeholders and improve along the way?")
            )
        )
    ),
    Pair(
        Driver.ContinuousLearning,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("ea59b811-d375-4a0a-b1f2-9daf5e1396e2")),
                text = QuestionToThinkAbout.Text("Are team members encouraged to learn new skills, techniques or practices?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("b06c1806-8a93-43df-8d95-0317af091876")),
                text = QuestionToThinkAbout.Text("Does the team consciously learn from their daily collaboration?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("df8dc846-e2ce-4fcc-a386-56eeea89ee54")),
                text = QuestionToThinkAbout.Text("Is learning a goal of team members work?")
            )
        )
    ),
    Pair(
        Driver.ToolSuitability,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("261a4f2b-57c4-45e1-b1c4-5b1de926aaed")),
                text = QuestionToThinkAbout.Text("Do the team members have access to tools that suit their tasks and preferences?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("446172ad-5cd3-4949-8a0a-a8ed4f49b053")),
                text = QuestionToThinkAbout.Text("Does the tool stack evolve to accommodate new needs of the team members?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("d82af36c-7023-4721-a2c3-0d67950198e4")),
                text = QuestionToThinkAbout.Text("Do the team members understand how the tools work?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("9df6cb7e-f75d-47b7-892d-b1306748f3db")),
                text = QuestionToThinkAbout.Text("Are the tools properly integrated into the workflow?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("79a605e4-eb87-4784-b899-5bc22c512f76")),
                text = QuestionToThinkAbout.Text("Do the team members use tools that remove redundant and/or manual work?")
            )
        )
    ),
    Pair(
        Driver.ToolPerformance,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("c6e99be2-92b3-4816-a696-158aa84325fb")),
                text = QuestionToThinkAbout.Text("Are the tools used by the team stable?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("b02f40e5-3584-4eab-8450-7590ce1443e2")),
                text = QuestionToThinkAbout.Text("Are the tools used by the team reliable?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("b8f2ea5c-4b31-4e55-8b70-f3da8ef44489")),
                text = QuestionToThinkAbout.Text("Are the tools responsive and perform well?")
            )
        )
    ),
    Pair(
        Driver.Environment,
        listOf(
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("16b39b8e-f4de-4021-95c7-ec530137e9e2")),
                text = QuestionToThinkAbout.Text("Does the team have adequate conditions to carry their work?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("5e7c6c54-1970-4880-a1b7-ea7f7b01983f")),
                text = QuestionToThinkAbout.Text("Does the team members' workspace fits their work?")
            ),
            QuestionToThinkAbout(
                id = QuestionToThinkAbout.Id(UUID.fromString("702687c2-34c0-45b2-9aa5-c2b17c750d71")),
                text = QuestionToThinkAbout.Text("Are there distractions (noise, vibration) that affect the team's work?")
            )
        )
    )

)


val questionsToThinkAboutMap: QuestionsToThinkAboutMap = QuestionsToThinkAboutMap(map)

