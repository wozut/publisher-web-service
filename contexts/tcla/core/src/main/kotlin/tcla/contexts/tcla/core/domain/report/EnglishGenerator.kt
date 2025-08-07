package tcla.contexts.tcla.core.domain.report

fun List<String>.shouldPluraliseDeterminer(): String =
    when {
        size == 1 -> "this"
        size > 1 -> "these"
        else -> ""
    }

fun String.shouldPluralizeRegularNoun(nouns: Collection<Any?>): String =
    setOf("cluster", "driver", "influencer", "area").let {
        when {
            it.contains(this) && nouns.size > 1 -> "${this}s"
            it.contains(this) && nouns.size == 1 -> this
            else -> ""
        }
    }

fun haveVerbForm(nouns: List<Any>): String =
    when {
        nouns.size > 1 -> "have"
        nouns.size == 1 -> "has"
        else -> ""
    }

fun String.shouldUseFirstOrThirdFormOfRegularVerb(nouns: List<String>): String =
    setOf("stand").contains(this).let {
        when {
            nouns.size > 1 -> this
            nouns.size == 1 -> "${this}s"
            else -> ""
        }
    }
fun String.shouldUseFirstOrThirdFormOfIrregularVerb(nouns: List<Any>): String =
    setOf("is").contains(this).let {
        when {
            nouns.size > 1 -> "are"
            nouns.size == 1 -> this
            else -> ""
        }
    }

fun List<String>.shouldUseSingularOrPluralThirdPersonPronoun(): String =
    when(size) {
        0 -> ""
        1 -> "it"
        else -> "them"
    }


fun List<String>.shouldIncludeQuantifier(): String =
    when(size) {
        0 -> ""
        1 -> "a "
        else -> ""
    }

fun List<String>.toEnumeration(): String =
    when {
        this.size == 1 -> this.first()
        this.size == 2 -> "${this.first()} and ${this.last()}"
        this.size > 2 -> {
            this
                .dropLast(1)
                .joinToString{ it }
                .let { "$it, and ${this.last()}" }
        }
        else -> ""
    }

fun String.toPossessiveForm(): String =
    this.last().let {
        when(it) {
            's' -> "$this'"
            else -> "$this's"
        }
    }
