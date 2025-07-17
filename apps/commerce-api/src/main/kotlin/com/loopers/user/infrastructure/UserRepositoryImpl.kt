package com.loopers.user.infrastructure

import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import com.loopers.user.domain.vo.LoginId
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {
    override fun existsByLoginId(loginId: LoginId): Boolean = jpaUserRepository.existsByLoginId(loginId)
    override fun save(user: User): User = jpaUserRepository.save(user)
    override fun findByLoginId(loginId: LoginId): User? = jpaUserRepository.findByLoginId(loginId)
}
