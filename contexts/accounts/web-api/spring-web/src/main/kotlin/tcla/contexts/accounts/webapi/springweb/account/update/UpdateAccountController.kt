package tcla.contexts.accounts.webapi.springweb.account.update

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.accounts.core.application.account.update.UpdateAccountCommand
import tcla.contexts.accounts.core.application.account.update.UpdateAccountCommandHandler
import tcla.contexts.accounts.core.application.account.update.UpdateAccountFailure
import tcla.contexts.accounts.core.application.account.update.UpdateAccountSuccess
import tcla.contexts.accounts.webapi.springweb.account.jsonapi.ACCOUNT_JSON_API_TYPE
import tcla.contexts.accounts.webapi.springweb.account.update.jsonapi.AccountPatchRequestDocument
import tcla.libraries.jsonserialization.deserialization.deserializeJsonAs


private val ALLOWED_JSON_API_TYPES = setOf(ACCOUNT_JSON_API_TYPE)

@RestController
class UpdateAccountController(
    private val updateAccountCommandHandler: UpdateAccountCommandHandler
) {

    @PatchMapping("/accounts/{id}")
    fun execute(
        @PathVariable id: String,
        @RequestBody requestBody: String
    ): ResponseEntity<Any> =
        requestBody.deserializeJsonAs(AccountPatchRequestDocument::class)
            .mapLeft { TODO() }
            .flatMap { //TODO ensure path variable is equal to id in body
                it.right()
            }
            .flatMap { document -> ensureAllowedJsonApiType(document) }
            .flatMap { document ->
                when (document.data.attributes) {
                    null -> handleEmptyAttributes().right()
                    else -> handleNotEmptyAttributes(document.data.id, document.data.attributes)
                }
            }.fold({ it }, { it })

    private fun handleNotEmptyAttributes(
        id: String,
        attributes: Map<String, String?>
    ): Either<ResponseEntity<Any>, ResponseEntity<Any>> = executeCommand(id, attributes)
        .mapLeft { TODO() }
        .flatMap { successResponse().right() }

    private fun handleEmptyAttributes(): ResponseEntity<Any> = successResponse()

    private fun ensureAllowedJsonApiType(document: AccountPatchRequestDocument): Either<Nothing, AccountPatchRequestDocument> =
        either {
            ensure(ALLOWED_JSON_API_TYPES.contains(document.data.type)) { TODO() }
            document
        }

    private fun executeCommand(
        id: String,
        attributes: Map<String, String?>
    ): Either<UpdateAccountFailure, UpdateAccountSuccess> =
        UpdateAccountCommand(
            id = id,
            fields = HashMap(attributes)
        ).let { command ->
            updateAccountCommandHandler.execute(command)
                .mapLeft { TODO() }
        }

    private fun successResponse(): ResponseEntity<Any> {
        return ResponseEntity.noContent().build()
    }
}
