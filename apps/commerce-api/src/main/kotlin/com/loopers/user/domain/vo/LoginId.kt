package com.loopers.user.domain.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

@JvmInline
value class LoginId(val value: String) {
    init {
        if (!USER_ID_REGEX.matches(value)) {
            throw CoreException(ErrorType.INVALID_USER_ID_FORMAT)
        }
    }

    companion object {
        private val USER_ID_REGEX = "^[a-zA-Z0-9]{6,10}$".toRegex()
    }
}
