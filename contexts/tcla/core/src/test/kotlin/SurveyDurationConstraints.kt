
import arrow.core.Either
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import tcla.contexts.tcla.core.application.failures.ResponseAcceptanceIntervalFailure
import java.time.Duration
import java.time.Instant

class SurveyDurationConstraints {
    private val responseAcceptanceInterval = ResponseAcceptanceInterval(Instant.now(), Instant.now().plus(Duration.ofDays(4))).fold({ TODO() },{it})
    private var result: Either<ResponseAcceptanceIntervalFailure, ResponseAcceptanceInterval>? = null
    private var resultOfModifyingStartDate: Either<Failure, ResponseAcceptanceInterval>? = null

    @Given("the minimum duration of a survey is {int} day")
    fun theMinimumDurationOfASurveyIsDay(arg0: Int) {
    }

    @When("a team owner creates a survey with a duration of {int} hours")
    fun aTeamOwnerCreatesASurveyWithADurationOfHours(hours: Int) {
        val start = Instant.now()
        result = ResponseAcceptanceInterval(start = start, end = start.plus(Duration.ofHours(hours.toLong())))
    }
    @Then("failure occurs because it is not possible")
    fun `failure Occurs Because It Is Not Possible`() {
        assertThat(result!!.isLeft()).isTrue()
    }

    @Given("the maximum duration of a survey is {int} days")
    fun theMaximumDurationOfASurveyIsDays(arg0: Int) {
    }

    @When("a team owner creates a survey with a duration of {int} days")
    fun aTeamOwnerCreatesASurveyWithADurationOfDays(days: Int) {
        val start = Instant.now()
        result = ResponseAcceptanceInterval(start = start, end = start.plus(Duration.ofDays(days.toLong())))
    }

    @Given("a survey that has already started")
    fun aSurveyThatHasAlreadyStarted() {
    }

    @When("a team owner tries to modify the start date of that survey")
    fun aTeamOwnerModifiesTheStartDateOfThatSurvey() {
        resultOfModifyingStartDate = responseAcceptanceInterval.update(newStart = Instant.now(), newEnd = responseAcceptanceInterval.end)
    }

    @Then("failure occurs because start date cannot be modified after survey has already started")
    fun failureOccursBecauseStartDateCannotBeModifiedAfterSurveyHasAlreadyStarted() {
        assertThat(resultOfModifyingStartDate!!.isLeft())
    }
}
