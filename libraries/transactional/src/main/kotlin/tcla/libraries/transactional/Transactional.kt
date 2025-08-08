package tcla.libraries.transactional

import arrow.core.Either
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionDefinition.ISOLATION_DEFAULT
import org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED
import org.springframework.transaction.TransactionDefinition.ISOLATION_READ_UNCOMMITTED
import org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ
import org.springframework.transaction.TransactionDefinition.ISOLATION_SERIALIZABLE
import org.springframework.transaction.TransactionDefinition.PROPAGATION_MANDATORY
import org.springframework.transaction.TransactionDefinition.PROPAGATION_NESTED
import org.springframework.transaction.TransactionDefinition.PROPAGATION_NEVER
import org.springframework.transaction.TransactionDefinition.PROPAGATION_NOT_SUPPORTED
import org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED
import org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW
import org.springframework.transaction.TransactionDefinition.PROPAGATION_SUPPORTS
import org.springframework.transaction.support.TransactionTemplate

private lateinit var privatePlatformTransactionManager: PlatformTransactionManager

enum class IsolationLevel {
    DEFAULT, READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE;
}

private fun IsolationLevel.toSpringFramework(): Int = when (this) {
    IsolationLevel.DEFAULT -> ISOLATION_DEFAULT
    IsolationLevel.READ_UNCOMMITTED -> ISOLATION_READ_UNCOMMITTED
    IsolationLevel.READ_COMMITTED -> ISOLATION_READ_COMMITTED
    IsolationLevel.REPEATABLE_READ -> ISOLATION_REPEATABLE_READ
    IsolationLevel.SERIALIZABLE -> ISOLATION_SERIALIZABLE
}

enum class PropagationBehavior {
    REQUIRED, SUPPORTS, MANDATORY, REQUIRES_NEW, NOT_SUPPORTED, NEVER, NESTED;
}

private fun PropagationBehavior.toSpringFramework(): Int = when (this) {
    PropagationBehavior.REQUIRED -> PROPAGATION_REQUIRED
    PropagationBehavior.SUPPORTS -> PROPAGATION_SUPPORTS
    PropagationBehavior.MANDATORY -> PROPAGATION_MANDATORY
    PropagationBehavior.REQUIRES_NEW -> PROPAGATION_REQUIRES_NEW
    PropagationBehavior.NOT_SUPPORTED -> PROPAGATION_NOT_SUPPORTED
    PropagationBehavior.NEVER -> PROPAGATION_NEVER
    PropagationBehavior.NESTED -> PROPAGATION_NESTED
}

const val TIMEOUT_DEFAULT: Int = TransactionDefinition.TIMEOUT_DEFAULT

interface TransactionExecutor {
    fun <R : Any> transactional(
        isolationLevel: IsolationLevel = IsolationLevel.DEFAULT,
        timeout: Int = TIMEOUT_DEFAULT,
        isReadOnly: Boolean = false,
        propagationBehavior: PropagationBehavior = PropagationBehavior.REQUIRED,
        function: () -> R
    ): R
}

@Component
@Primary
class SpringTransactionExecutor(platformTransactionManager: PlatformTransactionManager) : TransactionExecutor {
    init {
        privatePlatformTransactionManager = platformTransactionManager
    }

    override fun <R : Any> transactional(
        isolationLevel: IsolationLevel,
        timeout: Int,
        isReadOnly: Boolean,
        propagationBehavior: PropagationBehavior,
        function: () -> R
    ): R = transactional(
        isolationLevel = isolationLevel,
        timeout = timeout,
        isReadOnly = isReadOnly,
        propagationBehavior = propagationBehavior,
        enabled = true,
        function = function
    )
}


@Component
class FakeTransactionExecutor : TransactionExecutor {
    override fun <R : Any> transactional(
        isolationLevel: IsolationLevel,
        timeout: Int,
        isReadOnly: Boolean,
        propagationBehavior: PropagationBehavior,
        function: () -> R
    ): R = transactional(
        isolationLevel = isolationLevel,
        timeout = timeout,
        isReadOnly = isReadOnly,
        propagationBehavior = propagationBehavior,
        enabled = false,
        function = function
    )
}

fun <R : Any> transactional(
    isolationLevel: IsolationLevel = IsolationLevel.DEFAULT,
    timeout: Int = TIMEOUT_DEFAULT,
    isReadOnly: Boolean = false,
    propagationBehavior: PropagationBehavior = PropagationBehavior.REQUIRED,
    enabled: Boolean = true,
    function: () -> R
): R = when (enabled) {
    true -> TransactionTemplate(privatePlatformTransactionManager).apply {
        this.isolationLevel = isolationLevel.toSpringFramework()
        this.timeout = timeout
        this.isReadOnly = isReadOnly
        this.propagationBehavior = propagationBehavior.toSpringFramework()
    }.execute { transactionStatus ->
        function().also { result: R ->
            if (result is Either<*, *>) result.onLeft { transactionStatus.setRollbackOnly() }
        }
    }!!

    false -> function()
}
