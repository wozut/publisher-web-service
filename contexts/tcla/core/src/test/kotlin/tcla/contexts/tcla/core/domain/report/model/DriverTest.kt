package tcla.contexts.tcla.core.domain.report.model

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class DriverTest {
    @Test
    fun `there is an exact number of drivers`() {
        assertThat(Driver::class.sealedSubclasses).hasSize(24)
    }

    @Test
    fun `driver ids are unique`() {
        val ids: List<String> = Driver::class.sealedSubclasses
            .map { sealedSubclass ->
                sealedSubclass.memberProperties
                    .filter { memberProperty -> memberProperty.visibility == KVisibility.PUBLIC }
                    .first { memberProperty -> memberProperty.name == "id" }
                    .let { memberProperty ->
                        val objectInstance: Driver? = sealedSubclass.objectInstance
                        if (objectInstance == null) Assertions.fail<String>("")
                        memberProperty.getter.call(objectInstance)!! as String
                    }
            }

        val distinctIds = ids.distinct()

        assertThat(ids.size).isEqualTo(distinctIds.size)
    }
}
