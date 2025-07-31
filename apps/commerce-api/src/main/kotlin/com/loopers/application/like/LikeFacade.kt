package com.loopers.application.like

import com.loopers.application.common.LockManager
import com.loopers.application.like.command.CreateLikeCommand
import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class LikeFacade(
    private val likeService: LikeService,
    private val userService: UserService,
    private val lockManager: LockManager,
) {

    fun createLike(command: CreateLikeCommand) {
        val lockResult = lockManager.tryLock(command.loginId, command.targetId.toString(), command.target.name)

        if (!lockResult) return

        try {
            val user = userService.getUserProfile(command.loginId)

            likeService.addLike(Like(user.id, command.targetId, command.target))
        } finally {
            lockManager.unlock(command.loginId, command.targetId.toString(), command.target.name)
        }
    }
}
