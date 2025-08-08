package tcla.contexts.tcla.core.domain.report.buildreport

fun getTclLevelQualificationLabel(tclLevelScore: Double): String =
    when {
        tclLevelScore >= 3.00 -> "slightly concerning"
        1.50 < tclLevelScore && tclLevelScore < 3.00 -> "within a reasonable range"
        tclLevelScore <= 1.5 -> "under control"
        else -> ""
    }
