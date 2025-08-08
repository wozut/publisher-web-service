package tcla.contexts.tcla.core.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import tcla.contexts.tcla.core.application.failures.InvalidEmail

@JvmInline
value class Email private constructor(val string: String) {

    companion object {
        operator fun invoke(value: String): Either<InvalidEmail, Email> =
            Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")
                .let { regex ->
                    when(regex.matches(value)) {
                        true -> Email(value).right()
                        false -> InvalidEmail.left()
                    }
                }
    }
}
