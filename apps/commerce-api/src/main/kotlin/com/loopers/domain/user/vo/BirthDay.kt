package com.loopers.domain.user.vo

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.Clock
import java.time.LocalDate

@Embeddable
data class BirthDay protected constructor(
    @Column(name = "birth_day")
    val value: String,
) {
    companion object {
        private val BIRTHDAY_REGEX = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

        operator fun invoke(value: String, clock: Clock = Clock.systemDefaultZone()): BirthDay {
            if (!BIRTHDAY_REGEX.matches(value)) {
                throw CoreException(ErrorType.INVALID_USER_BIRTH_DAY_FORMAT)
            }

            val birthDate = LocalDate.parse(value)

            if (!birthDate.isBefore(LocalDate.now(clock))) {
                throw CoreException(ErrorType.REQUIRED_USER_BIRTH_DAY_AFTER_THEN_NOW)
            }

            return BirthDay(value)
        }
    }
}
