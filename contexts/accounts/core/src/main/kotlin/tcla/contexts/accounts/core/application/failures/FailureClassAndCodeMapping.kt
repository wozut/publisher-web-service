package tcla.contexts.accounts.core.application.failures

import kotlin.reflect.KClass


object FailureClassAndCodeMapping {
    private const val CONTEXT_CODE_PREFIX = "2"

    val associations: List<FailureClassAndCodeAssociation> = listOf(

    )

    fun getCodeFor(clazz: KClass<*>): String? = associations
        .firstOrNull { association -> association.clazz == clazz }
        ?.code
        ?.let { code: Int -> CONTEXT_CODE_PREFIX.plus(code.toString()) }

}

data class FailureClassAndCodeAssociation(val clazz: KClass<out Failure>, val code: Int)


