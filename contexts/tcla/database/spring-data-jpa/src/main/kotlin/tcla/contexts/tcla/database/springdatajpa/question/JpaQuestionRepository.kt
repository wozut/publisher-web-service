package tcla.contexts.tcla.database.springdatajpa.question

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaQuestionRepository: JpaRepository<JpaQuestion, UUID>
