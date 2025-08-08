package tcla.contexts.tcla.webapi.springweb.report.search

import arrow.core.right
import com.google.gson.JsonParser
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tcla.contexts.tcla.core.application.report.search.SearchReportsQueryHandler
import tcla.contexts.tcla.core.application.report.search.SearchReportsSuccess
import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.BodyPartMother
import tcla.contexts.tcla.core.domain.report.model.ReportMother
import tcla.contexts.tcla.core.domain.report.model.SectionMother
import tcla.contexts.tcla.core.domain.report.model.SectionsMother
import java.util.UUID


@WebMvcTest(controllers = [SearchReportsController::class])
@ContextConfiguration(classes = [SearchReportsController::class])
class SearchReportsControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var searchReportsQueryHandler: SearchReportsQueryHandler

    @Test
    fun `search by assessment`() {
        val assessmentUuid = UUID.randomUUID()
        val quickSummaryBodyPart1 = BodyPartMother.default(
            order = BodyPart.Order(1),
            text = BodyPart.Text("quick summary body part 1 text"),
        )
        val introductionBodyPart1 = BodyPartMother.default()
        val overviewBodyPart1 = BodyPartMother.default()
        val analysisBodyPart1 = BodyPartMother.default()
        val annexBodyPart1 = BodyPartMother.default()
        val report1 = ReportMother.default(
            sections = SectionsMother.default(
                quickSummary = SectionMother.default(
                    body = listOf(quickSummaryBodyPart1)
                ),
                introduction = SectionMother.default(
                    body = listOf(introductionBodyPart1)
                ),
                overview = SectionMother.default(
                    body = listOf(overviewBodyPart1)
                ),
                analysis = listOf(
                    SectionMother.default(
                        body = listOf(analysisBodyPart1)
                    )
                ),
                annex = listOf(
                    SectionMother.default(
                        body = listOf(annexBodyPart1)
                    )
                )
            )
        )
        val reports = listOf(report1)

        every { searchReportsQueryHandler.execute(any()) } returns SearchReportsSuccess(reports = reports).right()

        val result = mockMvc.get("/reports") {
            param("filter[assessment]", assessmentUuid.toString())
        }.andExpect {
            status { isOk() }
            content {
                contentType("application/vnd.api+json")
            }
        }.andReturn()

        val expectedJsonAsString = """
{
  "data": [
    {
      "id": "${report1.id.value}",
      "type": "report",
      "attributes": {
        "assessmentTitle": "Spring",
        "copyrightText": "${report1.copyrightText.value}",
        "organizationName": "ACME",
        "period": {
            "end": "2023-12-14T19:46:15.184497860Z",
            "start": "2023-12-12T19:46:15.184489461Z"
        },
        "responsesStats": {
            "currentAmountCollected": 0,
            "currentRateCollected": 0,
            "maximumAmountToBeCollected": 1,
            "minimumAmountRequired": 1,
            "minimumRateRequired": 1
        },
        "shareableToken": "123",
        "teamName": "Avengers",
        "sections": {
          "quickSummary": {
            "title": "${report1.sections.quickSummary.title.value}",
            "body": [
              {
                "text": "${quickSummaryBodyPart1.text.value}",
                "order": ${quickSummaryBodyPart1.order.value}
              }
            ],
            "tclDriverScores": [
                {
                    "id": "${report1.sections.quickSummary.tclDriverAndScoreList.first().id}",
                    "name": "${report1.sections.quickSummary.tclDriverAndScoreList.first().name.value}",
                    "score": ${report1.sections.quickSummary.tclDriverAndScoreList.first().score.value}
                }
            ]
          },
          "introduction": {
            "title": "${report1.sections.introduction.title.value}",
            "body": [
              {
                "text": "${introductionBodyPart1.text.value}",
                "order": ${introductionBodyPart1.order.value}
              }
            ],
            "tclDriverScores": [
                {
                    "id": "${report1.sections.introduction.tclDriverAndScoreList.first().id}",
                    "name": "${report1.sections.introduction.tclDriverAndScoreList.first().name.value}",
                    "score": ${report1.sections.introduction.tclDriverAndScoreList.first().score.value}
                }
            ]
          },
          "overview": {
            "title": "${report1.sections.overview.title.value}",
            "body": [
              {
                "text": "${overviewBodyPart1.text.value}",
                "order": ${overviewBodyPart1.order.value}
              }
            ],
            "tclDriverScores": [
                {
                    "id": "${report1.sections.overview.tclDriverAndScoreList.first().id}",
                    "name": "${report1.sections.overview.tclDriverAndScoreList.first().name.value}",
                    "score": ${report1.sections.overview.tclDriverAndScoreList.first().score.value}
                }
            ]
          },
          "analysis": [
            {
              "title": "${report1.sections.analysis.first().title.value}",
              "body": [
                {
                  "text": "${analysisBodyPart1.text.value}",
                  "order": ${analysisBodyPart1.order.value}
                }
              ],
              "tclDriverScores": [
                  {
                      "id": "${report1.sections.analysis.first().tclDriverAndScoreList.first().id}",
                      "name": "${report1.sections.analysis.first().tclDriverAndScoreList.first().name.value}",
                      "score": ${report1.sections.analysis.first().tclDriverAndScoreList.first().score.value}
                  }
              ]
            }
          ],
          "annex": [
            {
              "title": "${report1.sections.annex.first().title.value}",
              "body": [
                {
                  "text": "${annexBodyPart1.text.value}",
                  "order": ${annexBodyPart1.order.value}
                }
              ],
              "tclDriverScores": [
                  {
                      "id": "${report1.sections.annex.first().tclDriverAndScoreList.first().id}",
                      "name": "${report1.sections.annex.first().tclDriverAndScoreList.first().name.value}",
                      "score": ${report1.sections.annex.first().tclDriverAndScoreList.first().score.value}
                  }
              ]
            }
          ]
        },
        "dictionary": [
          {
            "id": "team-characteristics",
            "term": "Team Characteristics",
            "definition": "With respect to Team Cognitive Load, three clusters of team characteristics seem to be important, namely: team composition, the roles members have, and the culture within the team. For example, if roles are ill defined, not fit for purpose or people feel overwhelmed by their role, this will slow down processes and lead to frustration and inefficiency, increasing individual and team level of Cognitive Load levels. Similarly if the team culture is characterized by ineffective communication or low levels of psychological safety, members will feel insecure and annoyed and dissatisfied, raising Cognitive Load."
          },
          {
            "id": "task-characteristics",
            "term": "Task Characteristics",
            "definition": "Task Characteristics can impact Team Cognitive Load. For example, it is well known that task complexity (of the task itself but also of the surrounding circumstances) increases Cognitive Load. Further, there is evidence that ambiguity around the problem and the right solution, as well as a lack of success metrics increases uncertainty and leads to ineffective collaboration and frustration; leading to higher Team Cognitive Load levels."
          },
          {
            "id": "work-practices-and-processes",
            "term": "Work Practices & Processes",
            "definition": "Ineffective, inconsistent and slow work practices and processes will result in team's frustration, poor results and fatigue; increasing Team Cognitive Load. Similarly, if information that is used is unreliable, not relevant or accessible the team will experience delays, uncertainty and it often makes it impossible to deliver valuable results - increasing the Team Cognitive Load."
          },
          {
            "id": "work-environment-and-tools",
            "term": "Work Environment & Tools",
            "definition": "The work environment can strongly affect Team Cognitive Load. For example loud noise, strong vibration, bad lightening, and potential physical discomfort or hazards will cause employees to be distracted and/or anxious, increasing their Cognitive Load. Further, using outdated, unsuitable, or low performing tools can lead to delays, sub-optimal work outputs and employees getting extremely frustrated - once again elevating Team Cognitive Load levels."
          },
          {
            "id": "team-complexity",
            "term": "Team Complexity",
            "definition": "With respect to cognitive load, the larger and more complex the team, the more transactive activities will be needed to coordinate members actions and the more communication is needed within the team increasing cognitive load."
          },
          {
            "id": "team-competence",
            "term": "Team Competence",
            "definition": "The composition of a team in terms of the team members' domain-specific knowledge or expertise also plays a role. If the current skill mix is not responding to the work demand, it likely leads to stress, frustration, low productivity and weak morale."
          },
          {
            "id": "role-clarity",
            "term": "Role Clarity",
            "definition": "By reducing coordination activities, clear roles should reduce the cognitive load incurred."
          },
          {
            "id": "role-fit",
            "term": "Role Fit",
            "definition": "If person-role-fit is low, employees will feel like they are always working hard to measure up, often increasing self-doubt and leading to performance anxiety. The skills the role demands are often secondary ones; and take extra energy, increasing cognitive load."
          },
          {
            "id": "role-load",
            "term": "Role Load",
            "definition": "Role overload is associated with various aspects of psychological strain, such as increased job stress, anxiety and a feeling of being mentally overwhelmed - all of which are linked to high levels of cognitive load."
          },
          {
            "id": "team-alignment",
            "term": "Team Alignment",
            "definition": "In order to work effectively, teams need a clear purpose and shared meaning. Without it, team members are inclined to go off in different directions and do their own thing, errors and delays turn into fear and distrust, and improvements become arguably impossible - all of which increases cognitive load."
          },
          {
            "id": "team-interaction",
            "term": "Team Interaction",
            "definition": "Ineffective team interaction leads to communication gaps, which causes confusion, distrust, wastes time, and reduces productivity, leading to increased cognitive load."
          },
          {
            "id": "member-psychological-safety",
            "term": "Member Psychological Safety",
            "definition": "A fundamental component of psychological safety is being able to speak up when you don't know something and to ask questions without fear of embarrassment or shame. Safely asking questions is key to reducing cognitive load. Even without actually needing to ask any questions, being in a psychologically safe enough environment that a participant knows they could ask questions should they need to, will help to improve cognition and learning capacity."
          },
          {
            "id": "problem-definition",
            "term": "Problem Definition",
            "definition": "The most difficult stage in the process of solving problems is their identification (including their causes), it is the definition of the problem that is most important - a wrongly defined problem leads to wrong solutions (long-term or not at all). If members have a different understanding of the problem at hand they will be unaligned on solutions resulting in ineffective work processes, slow decision-making and frustration - leading to high levels of cognitive load."
          },
          {
            "id": "solution-alignment",
            "term": "Solution Alignment",
            "definition": "Without a common understanding of the solution/deliverables may vary within the team. These discrepancies in expectations, ultimately lead to constant friction and with this slow/ineffective work - increasing cognitive load."
          },
          {
            "id": "task-complexity",
            "term": "Task Complexity",
            "definition": "Cognitive load depends on task complexity, because it is determined by the number of interacting information elements that have to be related, controlled, and kept active in working memory during task performance."
          },
          {
            "id": "contextual-complexity",
            "term": "Contextual Complexity",
            "definition": "Too much contextual complexity - e.g. changing requirements, external circumstances, interruptions - tends to result in many unexpected situations during project execution. It is known to be linked to project team conflict and confusion as well as low project performance."
          },
          {
            "id": "metrics",
            "term": "Metrics",
            "definition": "Agreeing on what success looks like does not mean that delivery will be successful. Without agreed upon, clearly defined metrics and monitoring teams (and stakeholders) will struggle to assess if the team is \"on course\" or not. Without clearly defined success metrics, it will be hard to achieve informed decision-making and resource allocation as well as maintaining credibility and leadership buy-in. Without, clear metrics, understanding the business value teams are driving is almost impossible, leading to demotivation and focus-loss."
          },
          {
            "id": "use-of-information",
            "term": "Use Of Information",
            "definition": "Unavailable, incomplete or unreliable information, causes team members to spend effort in searching information and/or asserting that information is complete and correct, increasing cognitive load."
          },
          {
            "id": "process",
            "term": "Process",
            "definition": "Ineffective and/or outdated work processes as well as lack of sufficient transparency, decreases team morale, increases frustration and slows down work and productivity - all leading to stress and high cognitive load."
          },
          {
            "id": "consistency",
            "term": "Consistency",
            "definition": "A lack of consistency and standardization creates more work, extra confusion, mistrust and worse results."
          },
          {
            "id": "pace",
            "term": "Pace",
            "definition": "If things \"drag\" and time is wasted over unimportant activities, delays become more likely. Delays in delivery - caused by clunky slow processes - can create frustration, duplication and generate additional cost."
          },
          {
            "id": "performance",
            "term": "Performance",
            "definition": "If teams do not produce clear benefits/value and get feedback from relevant stakeholders on this, motivation tends to suffer. Without positive feedback on certain performance indicators, insecurity and anxiety will increase and confidence will suffer."
          },
          {
            "id": "resilience",
            "term": "Resilience",
            "definition": "If failures are hard to fix and causes of failures are hard to identify, team resilience tends to be low and improvement is almost impossible. Low levels of team resilience are known to lead reactional behaviour, low levels of trust and ultimately high levels of anxiety and stress - all of which are positively related to member cognitive load levels."
          },
          {
            "id": "iterative-working",
            "term": "Iterative Working",
            "definition": "Progressing iteratively with feedback is important because it allows team members to learn, and understand more about their current efforts enabling learning and improvements. Failing to do so, will lead to reduced confidence, stagnation and frustration, resulting in high levels of cognitive load."
          },
          {
            "id": "continuous-learning",
            "term": "Continuous Learning",
            "definition": "Cognitive load theory helps us to understand how people generally learn and store new information, and the types of instructional practices that best support learning. Yet, this assumes that there is a \"learning culture\" in the first place. If employees are not encouraged to continuously learn new skills and practices, they are likely to remain stuck in their old ways of working. This, in turn is likely to increase cognitive load for themselves and others going forward."
          },
          {
            "id": "tool-suitability",
            "term": "Tool Suitability",
            "definition": "Tools must be suitable (fit for purpose), well understood/intuitive, consistent, and well integrated (with other systems). For example if tools are unintuitive, team members will find the tools hard to use, causing frustration. Inconsistent systems or systems that don't interact well, lead to the user to waste time and get frustrated as they repeatedly must determine how to solve a task, in the current context, specifically."
          },
          {
            "id": "tool-performance",
            "term": "Tool Performance",
            "definition": "Tools need to perform well; be responsive, reliable and stable. Cumbersome interactions with the tool - e.g. caused by missing functionality or implementation that is dysfunctional/suboptimal - will force the user to waste energy doing what is considered unnecessary work."
          },
          {
            "id": "environment",
            "term": "Environment",
            "definition": "Cognitive load is known to increase based on external factors such as physical hazards, discomfort and/or exposure to distracting background noise and vibration."
          },
          {
            "id": "team-cognitive-load-driver",
            "term": "Team Cognitive Load driver",
            "definition": "A Team Cognitive Load driver is a measurable fact that influences the overall Team Cognitive Load. By understanding the impact of a Team Cognitive Load driver, the team can take actions aiming to reduce that impact."
          },
          {
            "id": "cluster",
            "term": "Cluster",
            "definition": "A Team Cognitive Load cluster is a logical grouping of related Team Cognitive Load drivers. A cluster helps to categorize the drivers into four meaningful areas."
          }
        ]
      }
    }
  ]
}
                """.trimIndent()

        val actualBody = JsonParser.parseString(result.response.contentAsString)
        val expectedBody = JsonParser.parseString(expectedJsonAsString)
        assertThat(actualBody).isEqualTo(expectedBody)
    }
}
