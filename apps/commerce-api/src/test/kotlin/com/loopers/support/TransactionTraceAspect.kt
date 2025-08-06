package com.loopers.support

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Aspect
@Component
class TransactionTraceAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun traceTransaction(joinPoint: ProceedingJoinPoint): Any? {
        var rollbackOnly = false
        var exceptionOccurred = false

        try {
            val result = joinPoint.proceed()

            rollbackOnly = TransactionAspectSupport.currentTransactionStatus().isRollbackOnly

            return result
        } catch (t: Throwable) {
            exceptionOccurred = true

            runCatching {
                rollbackOnly = TransactionAspectSupport.currentTransactionStatus().isRollbackOnly
            }

            throw t
        } finally {
            val signature = joinPoint.signature

            val methodName = signature.toShortString()
            val args = joinPoint.args

            val info = TransactionTraceInfo(methodName, args, rollbackOnly, exceptionOccurred)

            TransactionTraceHolder.set(info)
        }
    }
}
