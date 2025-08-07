package tcla.contexts.tcla.database.springdatajpa.message

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import tcla.contexts.tcla.core.domain.message.Channel

private const val emailString = "email"

fun Channel.toJpa(): String =
    when (this) {
        Channel.EMAIL -> emailString
    }


fun String.toDomainChannel(): Either<ChannelToDomainFailure, Channel> =
    when (this) {
        emailString -> Channel.EMAIL.right()
        else -> ChannelToDomainFailure.DatabaseNotConformsDomain.left()
    }


sealed class ChannelToDomainFailure {
    data object DatabaseNotConformsDomain : ChannelToDomainFailure()

}
