package com.loopers.user.application

import com.loopers.user.application.command.UserCreateCommand
import com.loopers.user.application.command.UserCreateResult
import com.loopers.user.application.command.UserDetailResult
import com.loopers.user.domain.UserService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService,
) {
    fun createUser(command: UserCreateCommand): UserCreateResult = userService.signUp(command.toEntity())
        .let(UserCreateResult::invoke)


    fun searchDetailByUserId(userId: String): UserDetailResult =
        userService.getUserProfile(userId)
            .let(UserDetailResult::invoke)
}
