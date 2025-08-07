package tcla.contexts.tcla.database.springdatajpa.answeroption

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaAnswerOptionRepository: JpaRepository<JpaAnswerOption, UUID>
