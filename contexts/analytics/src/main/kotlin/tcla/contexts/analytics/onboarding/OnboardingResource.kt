package tcla.contexts.analytics.onboarding


data class OnboardingResource(
    val id: String,
    val attributes: OnboardingResourceAttributes,
) {
    val type: String = ONBOARDING_JSON_API_TYPE
}


fun JpaOnboarding.toResource(): OnboardingResource =
    OnboardingResource(
        id = id.toString(),
        attributes = OnboardingResourceAttributes(
            userHasSeenDemo = userHasSeenDemo
        )
    )
