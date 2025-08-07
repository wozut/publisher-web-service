package tcla.contexts.tcla.database.springdatajpa.question

import arrow.core.Either
import jakarta.persistence.Column
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
import tcla.contexts.tcla.core.domain.question.model.Question
import tcla.contexts.tcla.database.springdatajpa.questionnaire.JpaQuestionnaire
import java.util.UUID

@Entity(name = "question")
@Table(name = "question", schema = "tcla")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class JpaQuestion(
    @Id
    open val id: UUID,
    @Column(name = "\"label\"", nullable = false)
    open val label: String,
    @Column(name = "\"order\"", nullable = false)
    open val order: Int,
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id", foreignKey = ForeignKey(name = "fk_tcla_survey"))
    open val questionnaire: JpaQuestionnaire
) {
    abstract fun toDomain(): Either<Failure, Question>
}
