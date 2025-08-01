package com.loopers.presentation.like

import com.loopers.application.like.LikeFacade
import com.loopers.application.like.command.CreateLikeCommand
import com.loopers.application.like.command.DeleteLikeCommand
import com.loopers.domain.like.vo.TargetType
import com.loopers.presentation.auth.LoginId
import com.loopers.presentation.like.dto.CreateProductLikeResponse
import com.loopers.presentation.like.dto.DeleteProductLikeResponse
import com.loopers.support.presentation.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LikeV1Controller(
    private val likeFacade: LikeFacade,
) {

    @PostMapping("/api/v1/like/products/{productId}")
    fun createProductLike(
        @LoginId loginId: String,
        @PathVariable productId: Long,
    ): ApiResponse<CreateProductLikeResponse> {
        likeFacade.createLike(CreateLikeCommand(loginId, productId, TargetType.PRODUCT))
        return ApiResponse.success(CreateProductLikeResponse())
    }

    @DeleteMapping("/api/v1/like/products/{productId}")
    fun deleteProductLike(
        @LoginId loginId: String,
        @PathVariable productId: Long,
    ): ApiResponse<DeleteProductLikeResponse> {
        likeFacade.deleteLike(DeleteLikeCommand(loginId, productId, TargetType.PRODUCT))
        return ApiResponse.success(DeleteProductLikeResponse())
    }
}
