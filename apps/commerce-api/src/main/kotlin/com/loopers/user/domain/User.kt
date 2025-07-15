package com.loopers.user.domain

import com.loopers.domain.BaseEntity
import com.loopers.user.domain.vo.BirthDay
import com.loopers.user.domain.vo.Email
import com.loopers.user.domain.vo.GenderType
import com.loopers.user.domain.vo.UserId
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class User private constructor(
    userId: UserId,
    email: Email,
    birthDay: BirthDay,
    gender: GenderType,
) : BaseEntity() {
    var userId: UserId = userId
        protected set
    var email: Email = email
        protected set

    @Embedded
    var birthDay: BirthDay = birthDay
        protected set
    var gender: GenderType = gender
        protected set

    companion object {
        operator fun invoke(
            userId: String,
            email: String,
            birthDay: String,
            gender: GenderType,
        ): User =
            User(
                UserId(userId),
                Email(email),
                BirthDay(birthDay),
                gender,
            )
    }
}
