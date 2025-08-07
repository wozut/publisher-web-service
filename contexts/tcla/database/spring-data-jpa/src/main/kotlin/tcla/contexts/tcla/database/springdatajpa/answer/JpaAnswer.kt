package tcla.contexts.tcla.database.springdatajpa.answer

import arrow.core.Either
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.answer.model.Answer
import tcla.contexts.tcla.core.domain.answerforanalysis.model.AnswerForAnalysis
import tcla.contexts.tcla.database.springdatajpa.questionnairefilling.JpaQuestionnaireFilling
import java.util.UUID

@Entity(name = "answer")
@Table(name = "answer", schema = "tcla")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class JpaAnswer(
    @Id
    open val id: UUID,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_filling_id", foreignKey = ForeignKey(name = "fk_tcla_response"))
    open val questionnaireFilling: JpaQuestionnaireFilling
) {
    abstract fun toDomainForAnalysis(): Either<Failure, AnswerForAnalysis>
    abstract fun toDomain(): Answer
}
