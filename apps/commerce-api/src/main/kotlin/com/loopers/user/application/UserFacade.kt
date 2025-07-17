package com.loopers.user.application

import com.loopers.point.domain.UserPointService
import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.application.command.UserCreateResult
import com.loopers.user.application.command.UserDetailResult
import com.loopers.user.domain.UserService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class UserFacade(
    private val userService: UserService,
    private val userPointService: UserPointService,
) {
    @Transactional
    fun createUser(command: UserCreateCommand): UserCreateResult {
        val user = userService.signUp(command.toEntity())
        userPointService.createInitialPoint(user.id)

        return UserCreateResult(user)
    }

    fun searchDetailByUserId(userId: String): UserDetailResult =
        userService.getUserProfile(userId)
            .let(UserDetailResult::invoke)
}
