package tcla.contexts.analytics.onboarding.find

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.analytics.Failure
import tcla.contexts.analytics.AnalyticsRequestIsAuthenticatedRule
import tcla.contexts.analytics.jsonapi.toFailureResponse
import tcla.contexts.analytics.onboarding.JpaOnboarding
import tcla.contexts.analytics.onboarding.JpaOnboardingRepository
import tcla.contexts.analytics.onboarding.toResource
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor

@RestController
class FindOnboardingController(
    private val jpaOnboardingRepository: JpaOnboardingRepository,
    private val requestIsAuthenticatedRule: AnalyticsRequestIsAuthenticatedRule,
    private val transactionExecutor: TransactionExecutor
) {
    @GetMapping("/onboardings/{id}", produces = ["application/vnd.api+json"])
    fun execute(
        @PathVariable id: String
    ): ResponseEntity<Any> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.READ_COMMITTED) {
            when (id) {
                "mine" -> {
                    requestIsAuthenticatedRule.ensure()
                        .flatMap { requesterId ->
                            Either.catch { jpaOnboardingRepository.findAllByOwnerId(requesterId) }
                                .mapLeft { Failure.DatabaseException(it).nel() }
                        }.flatMap { jpaOnboardings: List<JpaOnboarding> -> jpaOnboardings.firstOrNull().right() }
                        .flatMap { jpaOnboarding: JpaOnboarding? ->
                            when (jpaOnboarding) {
                                null -> Failure.EntityNotFound.Onboarding.nel().left()
                                else -> jpaOnboarding.right()
                            }
                        }
                }

                else -> Failure.InvalidId.nel().left()
            }.fold({ it.toFailureResponse() }, { it.toSuccessResponse() })
        }

    private fun JpaOnboarding.toSuccessResponse(): ResponseEntity<Any> =
        OnboardingDocument(data = this.toResource()).let { ResponseEntity.ok(it) }
}
