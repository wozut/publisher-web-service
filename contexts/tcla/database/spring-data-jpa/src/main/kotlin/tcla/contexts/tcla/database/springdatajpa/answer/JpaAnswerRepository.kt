package tcla.contexts.tcla.database.springdatajpa.answer

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaAnswerRepository: JpaRepository<JpaAnswer, UUID>
