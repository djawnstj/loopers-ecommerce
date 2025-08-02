package com.loopers.application.user

import com.loopers.domain.point.UserPointService
import com.loopers.application.user.command.UserCreateCommand
import com.loopers.application.user.command.UserCreateResult
import com.loopers.application.user.command.UserDetailResult
import com.loopers.domain.user.UserService
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

    fun searchDetailByLoginId(loginId: String): UserDetailResult =
        userService.getUserProfile(loginId)
            .let(UserDetailResult::invoke)
}
