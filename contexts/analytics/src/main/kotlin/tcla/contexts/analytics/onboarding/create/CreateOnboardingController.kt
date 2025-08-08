package tcla.contexts.analytics.onboarding.create

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import arrow.core.left
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.analytics.AnalyticsRequestIsAuthenticatedRule
import tcla.contexts.analytics.CreateOnboardingFailure
import tcla.contexts.analytics.Failure
import tcla.contexts.analytics.deserializeDocumentAs
import tcla.contexts.analytics.jsonapi.toFailureResponse
import tcla.contexts.analytics.onboarding.JpaOnboarding
import tcla.contexts.analytics.onboarding.JpaOnboardingRepository
import tcla.contexts.analytics.onboarding.ONBOARDING_JSON_API_TYPE
import tcla.contexts.analytics.onboarding.create.jsonapi.OnboardingPostRequestDocument
import tcla.libraries.transactional.IsolationLevel
import tcla.libraries.transactional.TransactionExecutor
import java.net.URI
import java.util.UUID

private val ALLOWED_JSON_API_TYPES = setOf(ONBOARDING_JSON_API_TYPE)

@RestController
class CreateOnboardingController(
    private val jpaOnboardingRepository: JpaOnboardingRepository,
    private val analyticsRequestIsAuthenticatedRule: AnalyticsRequestIsAuthenticatedRule,
    private val transactionExecutor: TransactionExecutor
) {

    @PostMapping("/onboardings")
    fun execute(@RequestBody requestBody: String): ResponseEntity<Any> =
        transactionExecutor.transactional(isolationLevel = IsolationLevel.REPEATABLE_READ) {
            analyticsRequestIsAuthenticatedRule.ensure()
                .flatMap { requesterId ->
                    requestBody.deserializeDocumentAs(OnboardingPostRequestDocument::class)
                        .flatMap { document -> ensureAllowedJsonApiType(document) }
                        .flatMap { document ->
                            Either.catch { jpaOnboardingRepository.existsByOwnerId(requesterId) }
                                .mapLeft { Failure.DatabaseException(it).nel() }
                                .flatMap { exists: Boolean ->
                                    when(exists) {
                                        true -> CreateOnboardingFailure.AtMostOneOnboardingPerAccount.nel().left()
                                        false -> document.right()
                                    }
                                }
                        }.flatMap { document ->
                            JpaOnboarding(
                                id = UUID.randomUUID(),
                                userHasSeenDemo = document.data.attributes.userHasSeenDemo,
                                ownerId = requesterId
                            ).right()
                        }
                }.flatMap { jpaOnboarding ->
                    //TODO ensure id generated not exists yet
                    jpaOnboarding.right()
                }.flatMap { jpaOnboarding ->
                    Either.catch { jpaOnboardingRepository.save(jpaOnboarding) }
                        .mapLeft { throwable -> Failure.DatabaseException(throwable).nel() }
                }.fold(
                    { failure -> failure.toFailureResponse() },
                    { jpaOnboarding -> jpaOnboarding.toSuccessResponse() }
                )
        }

    private fun ensureAllowedJsonApiType(document: OnboardingPostRequestDocument): Either<NonEmptyList<Failure>, OnboardingPostRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { Failure.JsonApiTypeNotAllowed.nel() }
            document
        }

    private fun JpaOnboarding.toSuccessResponse(): ResponseEntity<Any> =
        ResponseEntity.created(URI.create("/${this.id}"))
            .contentType(MediaType("application", "vnd.api+json"))
            .build()
}
