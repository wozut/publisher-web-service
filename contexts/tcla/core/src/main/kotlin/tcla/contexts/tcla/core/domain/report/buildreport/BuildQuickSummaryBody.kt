package tcla.contexts.tcla.core.domain.report.buildreport

import jakarta.inject.Named
import tcla.contexts.tcla.core.domain.report.model.BodyPart
import tcla.contexts.tcla.core.domain.report.model.Term
import tcla.contexts.tcla.core.domain.report.shouldPluraliseDeterminer
import tcla.contexts.tcla.core.domain.report.shouldPluralizeRegularNoun
import tcla.contexts.tcla.core.domain.report.shouldUseFirstOrThirdFormOfIrregularVerb

@Named
class BuildQuickSummaryBody {

    fun execute(
        tclDriverNames: List<String>,
        tooManyHighDriversThreshold: Int
    ): List<BodyPart> {
        val driverWord = "driver".shouldPluralizeRegularNoun(tclDriverNames)
        val driverEnumeration = tclDriverNames.mapNotNull(String::toRelevantDriver).markUpDriverEnumeration()
        val isOrAre = "is".shouldUseFirstOrThirdFormOfIrregularVerb(tclDriverNames)
        return when(tclDriverNames.size) {
            in 1..tooManyHighDriversThreshold -> "In this assessment, the highest" +
                    " ${Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load $driverWord")}" +
                    " for this team $isOrAre" +
                    " ${driverEnumeration}." +
                    " While other drivers might be negatively impacting the team as well," +
                    " ${tclDriverNames.shouldPluraliseDeterminer()} ${tclDriverNames.size} $driverWord should probably be investigated first." +
                    " If ${tclDriverNames.shouldPluraliseDeterminer()} $driverWord $isOrAre left unchecked, the Team Cognitive Load is likely to grow and impair" +
                    " the team’s ability to perform adequately in the future."
            else -> "In this assessment, ${tclDriverNames.size} different" +
                    " ${Term.TeamCognitiveLoadDriver.markUp("Team Cognitive Load $driverWord")}" +
                    " were found to be strongly influencing" +
                    " the team’s cognitive load. Given this is an unusual number of highest drivers," +
                    " the team probably should **invest the time now to understand** their environment and map" +
                    " out potential causes for their highest drivers." +
                    "\n" +
                    "\n" +
                    "If most of the drivers are coming from the same ${Term.Cluster.markUp("cluster")} (as detailed in the next section)" +
                    " that might be a good starting point. If the highest drivers are spread over multiple clusters " +
                    "then walk through all the drivers and decide which ones should/could be addressed first. " +
                    "We strongly recommend to re-assess the team’s cognitive load after some improvement actions " +
                    "have been taken.\n" +
                    "\n" +
                    "\n" +
                    "**Beware that the number of reported highest drivers puts the performance and well-being of the" +
                    " team at risk right now**."
        }.let {
            listOf(
                BodyPart(
                    text = BodyPart.Text(it),
                    order = BodyPart.Order(1)
                )
            )
        }
    }
}
