package com.loopers.infrastructure.user

import com.loopers.domain.user.User
import com.loopers.domain.user.UserRepository
import com.loopers.domain.user.vo.LoginId
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {
    override fun existsByLoginId(loginId: LoginId): Boolean = jpaUserRepository.existsByLoginId(loginId)
    override fun save(user: User): User = jpaUserRepository.save(user)
    override fun findByLoginId(loginId: LoginId): User? = jpaUserRepository.findByLoginId(loginId)
}
