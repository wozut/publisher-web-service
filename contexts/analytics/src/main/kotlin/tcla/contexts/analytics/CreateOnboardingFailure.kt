package tcla.contexts.analytics

sealed class CreateOnboardingFailure : Failure() {
    data object AtMostOneOnboardingPerAccount : CreateOnboardingFailure() {
        override val humanReadableSummary: String = "Another onboarding already exists for this account"
    }
}
