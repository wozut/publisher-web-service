package tcla.contexts.tcla.database.springdatajpa.message.accountmessage

sealed class AccountMessageToJpaFailure {
    data object NotSerializable : AccountMessageToJpaFailure()
}
