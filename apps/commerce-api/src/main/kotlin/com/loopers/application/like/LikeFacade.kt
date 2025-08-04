package com.loopers.application.like

import com.loopers.application.common.LockManager
import com.loopers.application.like.command.CreateProductLikeCommand
import com.loopers.application.like.command.DeleteProductLikeCommand
import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeService
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class LikeFacade(
    private val likeService: LikeService,
    private val userService: UserService,
    private val productService: ProductService,
    private val lockManager: LockManager,
) {

    fun createProductLike(command: CreateProductLikeCommand) {
        val lockSuccess = lockManager.tryLock(command.loginId, command.targetId.toString(), command.target.name)

        if (!lockSuccess) return

        try {
            val user = userService.getUserProfile(command.loginId)

            likeService.addLike(Like(user.id, command.targetId, command.target))

            productService.increaseProductLikeCount(command.targetId)
        } finally {
            lockManager.unlock(command.loginId, command.targetId.toString(), command.target.name)
        }
    }

    fun deleteProductLike(command: DeleteProductLikeCommand) {
        val lockSuccess = lockManager.tryLock(command.loginId, command.targetId.toString(), command.target.name)

        if (!lockSuccess) return

        try {
            val user = userService.getUserProfile(command.loginId)

            likeService.cancelLike(Like(user.id, command.targetId, command.target))

            productService.decreaseProductLikeCount(command.targetId)
        } finally {
            lockManager.unlock(command.loginId, command.targetId.toString(), command.target.name)
        }
    }
}
