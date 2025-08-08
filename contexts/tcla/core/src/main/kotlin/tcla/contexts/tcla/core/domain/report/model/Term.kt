package tcla.contexts.tcla.core.domain.report.model

sealed class Term(val id: String, val name: String) {
    data object TeamCognitiveLoadDriver: Term(id = "team-cognitive-load-driver", name = "Team Cognitive Load driver")
    data object Cluster: Term(id = "cluster", name = "Cluster")
}
