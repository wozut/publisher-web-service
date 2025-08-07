package tcla.contexts.accounts.core.application.account.update

data class UpdateAccountCommand(
    val id: String,
    val fields: HashMap<String, String?>
)
