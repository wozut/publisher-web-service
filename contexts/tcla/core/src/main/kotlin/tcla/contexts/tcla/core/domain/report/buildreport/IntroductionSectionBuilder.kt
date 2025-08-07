package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.Clusters

@Named
class IntroductionSectionBuilder {
    fun title(): String = "# About the results"

    fun buildIntroductionBody(): String = Introduction.text
}

object Introduction {
    private val clusterEnumeration = Clusters.all.markUpClusterEnumeration()
    val text: String = """
        #### The role of Team Cognitive Load
        In today's dynamic work environments, enabling fast flow and fostering team performance and team members' well-being is a priority for organizations striving to stay competitive. One crucial aspect influencing these factors is Team Cognitive Load. Team Cognitive Load, in this context, refers to the mental effort and capacity required to process information and make decisions within a team setting.

        #### How is this report generated?
        In this report, we conduct a comprehensive assessment of the Team Cognitive Load within this team, explore its underlying drivers, and provide tailored recommendations based on the Team Cognitive Load levels observed in this team.

        Our assessment methodology combines both quantitative and qualitative approaches, aiming to provide a holistic view of Team Cognitive Load within your organization.

        In an attempt to more broadly address the cognitive load induced in the
        workplace, we identified four clusters of drivers, namely $clusterEnumeration.

        For each of these clusters, we've gathered valuable insights into the cognitive challenges this teams faces, through a series of structured questions posed to the members of this team.

        #### How is the information in this report organized?

        This report is organized into several sections, each offering specific insights and recommendations:

        **1. Where do these Drivers come from:** Here, we provide a brief overview of the main drivers of Team Cognitive Load for this team in the clusters of $clusterEnumeration.
            
        **2. Analysis of Team Cognitive Load Levels in each cluster of Team Cognitive Load drivers:** In this section, we identify and discuss the key drivers that contribute to Team Cognitive Load in this cluster and recommend actions to manage it.
            
        **3. Frequently Asked Questions about the Team Cognitive Load Assessment:** Here, we outline the rigorous methods employed in our assessment, ensuring transparency and reliability in our findings.

        We believe this report will serve as a valuable resource for understanding, managing, and optimizing Team Cognitive Load at scale, ultimately contributing to faster flow of work and organizational success.
    """.trimIndent()
}
