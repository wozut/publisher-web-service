package tcla.contexts.tcla.core.domain.answeroption

import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOption
import tcla.contexts.tcla.core.domain.answeroption.model.AnswerOptionId
import java.util.UUID

object AnswerOptionMother {
    fun default(
        id: AnswerOptionId = AnswerOptionId(UUID.randomUUID()),
        value: AnswerOption.Value = AnswerOption.Value("1"),
        order: AnswerOption.Order = AnswerOption.Order(1)
    ): AnswerOption = AnswerOption(
        id = id,
        value = value,
        order = order
    )

    fun genderMale(
        id: UUID = UUID.randomUUID(),
        value: String ="Male",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )
    fun genderFemale(
        id: UUID = UUID.randomUUID(),
        value: String ="Female",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )

    fun genderOther(
        id: UUID = UUID.randomUUID(),
        value: String ="Other",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )

   fun modeOfWorkingRemote(
        id: UUID = UUID.randomUUID(),
        value: String ="Remote",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )
   fun modeOfWorkingHybrid(
        id: UUID = UUID.randomUUID(),
        value: String ="Hybrid",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )
   fun modeOfWorkingInPerson(
        id: UUID = UUID.randomUUID(),
        value: String ="In-person",
        order: Int = 1
    ): AnswerOption = AnswerOption(
        id = AnswerOptionId(id),
        value = AnswerOption.Value(value),
        order = AnswerOption.Order(order)
    )
}
