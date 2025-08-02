package com.loopers.domain.user.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

@JvmInline
value class Email(val value: String) {
    init {
        if (!EMAIL_REGEX.matches(value)) {
            throw CoreException(ErrorType.INVALID_USER_EMAIL_FORMAT)
        }
    }

    companion object {
        private val EMAIL_REGEX = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$".toRegex()
    }
}
