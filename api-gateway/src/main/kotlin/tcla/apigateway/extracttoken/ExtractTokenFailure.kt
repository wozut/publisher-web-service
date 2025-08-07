package tcla.apigateway.extracttoken

sealed class ExtractTokenFailure {
    data object InvalidValueSyntax : ExtractTokenFailure()
}
