package com.loopers.user.presentation

import com.loopers.auth.presentation.UserId
import com.loopers.support.presentation.ApiResponse
import com.loopers.user.application.UserService
import com.loopers.user.presentation.dto.MyDetailResponse
import com.loopers.user.presentation.dto.SignUpRequest
import com.loopers.user.presentation.dto.SignUpResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserV1Controller(
    private val userService: UserService,
) {

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @RequestBody request: SignUpRequest,
    ): ApiResponse<SignUpResponse> {
        val signUpResult = userService.signUp(request.toCommand())
        return ApiResponse.success(SignUpResponse(signUpResult))
    }

    @GetMapping("/api/v1/users/me")
    fun searchMyDetail(
        @UserId userId: String,
    ): ApiResponse<MyDetailResponse> {
        val searchDetailResult = userService.searchDetailByUserId(userId)
        return ApiResponse.success(MyDetailResponse(searchDetailResult))
    }
}
