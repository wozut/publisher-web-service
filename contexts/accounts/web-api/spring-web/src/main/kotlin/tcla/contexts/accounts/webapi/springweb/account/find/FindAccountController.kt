package tcla.contexts.accounts.webapi.springweb.account.find


import arrow.core.flatMap
import arrow.core.right
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import tcla.contexts.accounts.core.application.account.find.FindAccountQuery
import tcla.contexts.accounts.core.application.account.find.FindAccountQueryHandler
import tcla.contexts.accounts.core.domain.account.model.Account
import tcla.contexts.accounts.webapi.springweb.account.find.jsonapi.AccountDocument
import tcla.contexts.accounts.webapi.springweb.account.jsonapi.toResource


@RestController
class FindAccountController(private val findAccountQueryHandler: FindAccountQueryHandler) {
    @GetMapping("/accounts/{id}", produces = ["application/vnd.api+json"])
    fun execute(@PathVariable id: String): ResponseEntity<Any> =
        findAccountQueryHandler.execute(FindAccountQuery(id))
            .mapLeft { TODO() }
            .flatMap { querySuccess -> querySuccess.account.right() }
            .flatMap { account: Account -> account.toSuccessResponse().right() }
            .fold(ifLeft = { it }, ifRight = { it })

    private fun Account.toSuccessResponse(): ResponseEntity<Any> =
        AccountDocument(toResource()).let { ResponseEntity.ok(it) }
}
