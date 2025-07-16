package com.loopers.user.infrastructure

import com.loopers.user.domain.User
import com.loopers.user.domain.UserRepository
import com.loopers.user.domain.vo.UserId
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {
    override fun existsByUserId(userId: UserId): Boolean = jpaUserRepository.existsByUserId(userId)
    override fun save(user: User): User = jpaUserRepository.save(user)
    override fun findByUserId(userId: UserId): User? = jpaUserRepository.findByUserId(userId)
}
