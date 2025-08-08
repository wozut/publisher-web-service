package tcla.contexts.analytics

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.application.failures.FailureClassAndCodeMapping
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class FailureClassAndCodeMappingTest {
    @Test
    fun `failure codes must be unique`() {
        val numberOfFailureCodes = FailureClassAndCodeMapping.associations.size
        val numberOfDistinctFailureCodes =
            FailureClassAndCodeMapping.associations.distinctBy { failure -> failure.code }.size
        Assertions.assertThat(numberOfFailureCodes).isEqualTo(numberOfDistinctFailureCodes)
    }

    @Test
    fun `failure types must be unique`() {
        val numberOfFailureTypes = FailureClassAndCodeMapping.associations.size
        val numberOfDistinctFailureTypes =
            FailureClassAndCodeMapping.associations.distinctBy { failure -> failure.clazz }.size
        Assertions.assertThat(numberOfFailureTypes).isEqualTo(numberOfDistinctFailureTypes)
    }

    @Test
    fun `all failures have a not blank human readable summary`() {
        FailureClassAndCodeMapping.associations.forEach { association ->
            association.clazz.memberProperties
                .filter { memberProperty -> memberProperty.visibility == KVisibility.PUBLIC }
                .first { memberProperty -> memberProperty.name == "humanReadableSummary" }
                .let { memberProperty ->
                    val objectInstance: Failure? = association.clazz.objectInstance
                    if(objectInstance != null) {
                        memberProperty.getter.call(objectInstance)
                            .let { value: Any? -> value as String }
                            .let { stringValue -> Assertions.assertThat(stringValue).isNotBlank() }
                    } else {
                        //TODO create an object of each class and validate value
                    }
                }
        }

    }
}
