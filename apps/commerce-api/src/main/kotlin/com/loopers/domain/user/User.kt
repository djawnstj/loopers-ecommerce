package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.user.vo.BirthDay
import com.loopers.domain.user.vo.Email
import com.loopers.domain.user.vo.GenderType
import com.loopers.domain.user.vo.LoginId
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "member")
class User private constructor(
    loginId: LoginId,
    email: Email,
    birthDay: BirthDay,
    gender: GenderType,
) : BaseEntity() {
    var loginId: LoginId = loginId
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
            loginId: String,
            email: String,
            birthDay: String,
            gender: GenderType,
        ): User =
            User(
                LoginId(loginId),
                Email(email),
                BirthDay(birthDay),
                gender,
            )
    }
}
