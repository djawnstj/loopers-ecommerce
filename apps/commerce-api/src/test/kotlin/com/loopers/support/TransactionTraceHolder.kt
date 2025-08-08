package com.loopers.support

object TransactionTraceHolder {
    private val holder = ThreadLocal<TransactionTraceInfo>()

    fun set(info: TransactionTraceInfo) = holder.set(info)

    fun get(): TransactionTraceInfo = holder.get()

    fun cleat() = holder.remove()
}

class TransactionTraceInfo(
    val methodName: String,
    val arguments: Array<Any>,
    val rollbackOnly: Boolean,
    val exceptionOccurred: Boolean,
)
