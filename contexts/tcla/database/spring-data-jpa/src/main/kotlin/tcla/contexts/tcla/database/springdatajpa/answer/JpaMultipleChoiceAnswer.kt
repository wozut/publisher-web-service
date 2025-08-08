package tcla.contexts.tcla.database.springdatajpa.answer

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrimaryKeyJoinColumn
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.answer.model.Answer
import tcla.contexts.tcla.core.domain.answer.model.AnswerId
import tcla.contexts.tcla.core.domain.answer.model.MultipleChoiceAnswer
import tcla.contexts.tcla.core.domain.answerforanalysis.model.AnswerForAnalysisId
import tcla.contexts.tcla.core.domain.answerforanalysis.model.MultipleChoiceAnswerForAnalysis
import tcla.contexts.tcla.database.springdatajpa.answeroption.JpaAnswerOption
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFilling
import java.util.UUID

@Entity(name = "multiple_choice_answer")
@Table(name = "multiple_choice_answer", schema = "tcla")
@PrimaryKeyJoinColumn(foreignKey = ForeignKey(name = "fk_tcla_answer"))
class JpaMultipleChoiceAnswer(
    id: UUID,
    questionnaireFilling: JpaQuestionnaireFilling,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "answer_option_id", foreignKey = ForeignKey(name = "fk_tcla_answer_option"))
    val answerOption: JpaAnswerOption
) : JpaAnswer(
    id = id,
    questionnaireFilling = questionnaireFilling
) {
    override fun toDomainForAnalysis(): Either<Failure, MultipleChoiceAnswerForAnalysis> {
        return answerOption.toDomainForAnalysis()
            .flatMap {
                MultipleChoiceAnswerForAnalysis(
                    id = AnswerForAnalysisId(id),
                    answerOption = it
                ).right()
            }
    }

    override fun toDomain(): Answer = MultipleChoiceAnswer(
        id = AnswerId(id),
        answerOption = answerOption.toDomain()
    )
}

fun MultipleChoiceAnswer.toJpa(
    jpaQuestionnaireFilling: JpaQuestionnaireFilling,
    jpaAnswerOption: JpaAnswerOption
): JpaMultipleChoiceAnswer =
    JpaMultipleChoiceAnswer(
        id = id.value,
        questionnaireFilling = jpaQuestionnaireFilling,
        answerOption = jpaAnswerOption
    )
