package tcla.contexts.analytics

import kotlin.reflect.KClass


object FailureClassAndCodeMapping {
    private const val CONTEXT_CODE_PREFIX = "3"

    val associations: List<FailureClassAndCodeAssociation> = listOf(
        FailureClassAndCodeAssociation(Failure.RequestNotAuthenticated::class, 1),
        FailureClassAndCodeAssociation(Failure.DatabaseException::class, 2),
        FailureClassAndCodeAssociation(Failure.EntityNotFound.Onboarding::class, 3),
        FailureClassAndCodeAssociation(Failure.InvalidId::class, 4),
        FailureClassAndCodeAssociation(CreateOnboardingFailure.AtMostOneOnboardingPerAccount::class, 5),
        FailureClassAndCodeAssociation(Failure.InvalidDocument::class, 6),
        FailureClassAndCodeAssociation(Failure.JsonApiTypeNotAllowed::class, 7)
    )

    fun getCodeFor(clazz: KClass<*>): String? = associations
        .firstOrNull { association -> association.clazz == clazz }
        ?.code
        ?.let { code: Int -> CONTEXT_CODE_PREFIX.plus(code.toString()) }

}

data class FailureClassAndCodeAssociation(val clazz: KClass<out Failure>, val code: Int)


