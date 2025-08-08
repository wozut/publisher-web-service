package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.haveVerbForm
import tcla.contexts.tcla.core.domain.report.model.Cluster
import tcla.contexts.tcla.core.domain.report.model.Clusters
import tcla.contexts.tcla.core.domain.report.model.Driver
import tcla.contexts.tcla.core.domain.report.model.TclDriverAndScore
import tcla.contexts.tcla.core.domain.report.model.Term
import tcla.contexts.tcla.core.domain.report.shouldIncludeQuantifier
import tcla.contexts.tcla.core.domain.report.shouldPluraliseDeterminer
import tcla.contexts.tcla.core.domain.report.shouldPluralizeRegularNoun
import tcla.contexts.tcla.core.domain.report.shouldUseFirstOrThirdFormOfIrregularVerb
import tcla.contexts.tcla.core.domain.report.shouldUseFirstOrThirdFormOfRegularVerb
import tcla.contexts.tcla.core.domain.report.shouldUseSingularOrPluralThirdPersonPronoun
import tcla.contexts.tcla.core.domain.report.toEnumeration
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriverscore.model.TclDriverScore

@Named
class ReportTextBuilder {

    //TODO remove default Code Sherpas team name
    fun buildOverviewIntroductionSection(tclClusterScorePairs: List<Pair<TclDriver, TclDriverScore>>): String {
        return """
            ### Results
            
            The **current Team Cognitive Load levels** in the four clusters of drivers are **${
            getTclLevelQualificationLabel(
                calculateTclLevelScoreForAllTheClusters(tclClusterScorePairs)
            )
        }**.
        """.trimIndent()
    }

    fun buildOverviewResultsSection(tclClusterScorePairs: List<Pair<TclDriver, TclDriverScore>>): String {
        val resultsLines = tclClusterScorePairs.filter { it.first.isCluster }.toResultsLines()
        var resultsParagraph = ""
        for (line in resultsLines) {
            resultsParagraph += "\n".plus(line)
        }

        return resultsParagraph
    }

    private fun calculateTclLevelScoreForAllTheClusters(tclClusterScorePairs: List<Pair<TclDriver, TclDriverScore>>) =
        tclClusterScorePairs.map {
            it.second.value.value
        }.average()

    private fun List<Pair<TclDriver, TclDriverScore>>.toResultsLines(): List<String> = this.map {
        val clusterName = it.first.name.value
        val tclLevelScore = it.second.value.value
        val tclLevelsQualifier = getTclLevelQualificationLabel(tclLevelScore)
        """* The levels of TCL in the ${
            clusterName.toCluster()?.markUp()
        } cluster are **${tclLevelsQualifier}**, with a score of **${tclLevelScore}**.""".trimIndent()
    }

    fun buildTailoredAssessmentInOverviewSection(tclClusterDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>): String =
        tclClusterDriverScorePairs
            .filter { it.first.isCluster }
            .filter { pair -> pair.second.value.value >= 3.0 }
            .let { pairs ->
                when {
                    pairs.isEmpty() -> tclClusterDriverScorePairs
                        .filter { it.first.isCluster }
                        .filter { pair -> 2.30 < pair.second.value.value && pair.second.value.value < 3.00 }
                        .let { reasonableRangeClusters ->
                            when {
                                reasonableRangeClusters.isNotEmpty() -> buildOverviewSoftRecommendationsBody(
                                    reasonableRangeClusters.map { it.first.name.value })

                                else -> ""
                            }
                        }

                    else -> buildOverviewRecommendationsBody(pairs.map { it.first.name.value })
                }
            }

    private fun buildOverviewSoftRecommendationsBody(clusterNames: List<String>): String =
        """
            ### Recommendations
        
            The ${
            clusterNames.mapNotNull(String::toCluster).markUpClusterEnumeration()
        } ${"cluster".shouldPluralizeRegularNoun(clusterNames)} of drivers ${
            "is".shouldUseFirstOrThirdFormOfIrregularVerb(
                clusterNames
            )
        } slightly higher than the others, making ${clusterNames.shouldUseSingularOrPluralThirdPersonPronoun()} the **main ${
            "influencer".shouldPluralizeRegularNoun(
                clusterNames
            )
        } of Team Cognitive Load on the team**.
            * To maintain the team's current level of performance, **we recommend addressing ${clusterNames.shouldPluraliseDeterminer()} ${
            "area".shouldPluralizeRegularNoun(
                clusterNames
            )
        } first.**
            * Leaving ${clusterNames.shouldUseSingularOrPluralThirdPersonPronoun()} unchecked could lead to an increase in Team Cognitive Load.
            * This, in turn, might significantly affect the team's ability to perform at its current levels.
        """.trimIndent()


    fun buildWhereHighestDriversComeFromText(
        tclDriverScorePairs: List<Pair<TclDriver, TclDriverScore>>,
        range: Double
    ): String {
        val highestDrivers: List<TclDriverAndScore> =
            tclDriverScorePairs.findRelevantDriverScorePairsWithHighestScore(range)
        val driversGroupedByCluster: Map<Cluster?, List<TclDriverAndScore>> = highestDrivers
            .groupBy { tclDriverNameAndScore: TclDriverAndScore ->
                when (val driver: Driver? = tclDriverNameAndScore.name.value.toRelevantDriver()) {
                    null -> null
                    else -> driver.cluster
                }
            }

        val firstPart =
            "${Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load drivers")} are organized in four different ${
                Term.Cluster.markUp("clusters")
            }: " +
                    "${Clusters.all.markUpClusterEnumeration()}." +
                    "\n" +
                    "\n" +
                    "The highest drivers highlighted in the Quick summary above are coming from the following ${
                        "cluster".shouldPluralizeRegularNoun(
                            driversGroupedByCluster.keys
                        )
                    }:"
        val clusterDriverList = driversGroupedByCluster
            .map { entry: Map.Entry<Cluster?, List<TclDriverAndScore>> ->
                "* ${entry.key?.name}: ${
                    entry.value.map { it.name.value }.toEnumeration()
                }"
            }.joinToString("\n")

        val highestClusters: List<TclDriverAndScore> =
            tclDriverScorePairs.findClusterScorePairsWithHighestScore(range)

        val clusterList = "The results per cluster, calculated as the average of all drivers per cluster, indicate that " +
                "the highest ${"cluster".shouldPluralizeRegularNoun(highestClusters)} " +
                "${"is".shouldUseFirstOrThirdFormOfIrregularVerb(highestClusters)}: ${highestClusters.map { it.name.value }.toEnumeration()}."

        val highestClustersWithoutHighestDrivers = highestClusters.filterNot { cluster ->
            driversGroupedByCluster.keys.mapNotNull { it?.name }.contains(cluster.name.value)
        }

        val highestClustersWithoutHighestDriversParagraph = when(highestClustersWithoutHighestDrivers.isEmpty()) {
            true -> ""
            false -> "\n\nNote that ${"cluster".shouldPluralizeRegularNoun(highestClustersWithoutHighestDrivers)}" +
                    " ${highestClustersWithoutHighestDrivers.map { it.name.value }.toEnumeration()}" +
                    " ${haveVerbForm(highestClustersWithoutHighestDrivers)} an overall high influence on the team’s " +
                    "cognitive load despite not including any of the highest drivers mentioned above." +
                    " This is an area of concern that should be investigated along with the highest drivers."
        }

        return firstPart + "\n\n" + clusterDriverList + "\n\n" + clusterList + highestClustersWithoutHighestDriversParagraph

    }

    private fun buildOverviewRecommendationsBody(clusterNames: List<String>): String =
        """            
            The ${
            clusterNames.mapNotNull(String::toCluster).markUpClusterEnumeration()
        } ${"cluster".shouldPluralizeRegularNoun(clusterNames)} of drivers ${
            "stand".shouldUseFirstOrThirdFormOfRegularVerb(
                clusterNames
            )
        } out today as ${clusterNames.shouldIncludeQuantifier()}**strong ${
            "influencer".shouldPluralizeRegularNoun(
                clusterNames
            )
        } of Team Cognitive Load on the team**.
            * To maintain the team's current level of performance, **we recommend addressing ${clusterNames.shouldPluraliseDeterminer()} ${
            "area".shouldPluralizeRegularNoun(
                clusterNames
            )
        } first.**
            * Leaving ${clusterNames.shouldUseSingularOrPluralThirdPersonPronoun()} unchecked could lead to an increase in Team Cognitive Load.
            * This, in turn, might significantly affect the team's ability to perform at its current levels.
        """.trimIndent()
}
