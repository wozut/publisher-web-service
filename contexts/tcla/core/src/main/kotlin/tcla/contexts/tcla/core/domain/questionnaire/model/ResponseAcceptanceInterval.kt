package tcla.contexts.tcla.core.domain.questionnaire.model

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.right
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.ResponseAcceptanceIntervalFailure
import java.time.Duration
import java.time.Instant

class ResponseAcceptanceInterval private constructor(val start: Instant, val end: Instant) {
    companion object {
        val MAXIMUM_DURATION: Duration = Duration.ofDays(14)
        val MINIMUM_DURATION: Duration = Duration.ofDays(1)
        operator fun invoke(
            start: Instant,
            end: Instant
        ): Either<ResponseAcceptanceIntervalFailure, ResponseAcceptanceInterval> =
            validateDuration(ResponseAcceptanceInterval(start, end))

        private val valid = Unit.right()
    }
    fun hasStarted(): Boolean = Instant.now() >= start
    fun hasEnded(): Boolean = Instant.now() > end

    fun overlaps(other: ResponseAcceptanceInterval): Boolean = start <= other.end && other.start <= end

    fun update(
        newStart: Instant,
        newEnd: Instant
    ): Either<Failure, ResponseAcceptanceInterval> {
        val now = Instant.now()
        return validateNewStart(newStart, now)
            .flatMap { validateNewEnd(newEnd, now) }
            .flatMap { validateDuration(ResponseAcceptanceInterval(newStart, newEnd)) }
    }

    private fun validateNewStart(newStart: Instant, now: Instant): Either<Failure, Unit> =
        when (newStart == this.start) {
            true -> valid
            false -> whenNewStartIsDifferent(now, newStart)
        }

    private fun whenNewStartIsDifferent(
        now: Instant,
        newStart: Instant
    ): Either<ResponseAcceptanceIntervalFailure, Unit> =
        when (now.isBefore(start)) {
            true -> when (now <= newStart) {
                true -> valid
                false -> ResponseAcceptanceIntervalFailure.StartDateMustBeEqualToOrAfterNow.left()
            }

            false -> ResponseAcceptanceIntervalFailure.UnableModifyStartDateWhenItHasAlreadyStarted.left()
        }

    private fun validateNewEnd(newEnd: Instant, now: Instant): Either<Failure, Unit> =
        when (newEnd == this.end) {
            true -> valid
            false -> whenNewEndIsDifferent(now, newEnd)
        }

    private fun whenNewEndIsDifferent(now: Instant, newEnd: Instant): Either<Failure, Unit> =
        when (now.isAfter(end)) {
            true -> ResponseAcceptanceIntervalFailure.UnableModifyEndDateWhenItHasAlreadyFinished.left()
            false -> when (now <= newEnd) {
                true -> valid
                false -> ResponseAcceptanceIntervalFailure.EndDateMustBeEqualToOrAfterNow.left()
            }
        }
}

private fun validateDuration(responseAcceptanceInterval: ResponseAcceptanceInterval): Either<ResponseAcceptanceIntervalFailure, ResponseAcceptanceInterval> =
    either {
        val desiredDuration = Duration.between(responseAcceptanceInterval.start, responseAcceptanceInterval.end)
        ensure(desiredDuration <= ResponseAcceptanceInterval.MAXIMUM_DURATION) { ResponseAcceptanceIntervalFailure.InvalidDuration }
        ensure(desiredDuration >= ResponseAcceptanceInterval.MINIMUM_DURATION) { ResponseAcceptanceIntervalFailure.InvalidDuration }
        responseAcceptanceInterval
    }
