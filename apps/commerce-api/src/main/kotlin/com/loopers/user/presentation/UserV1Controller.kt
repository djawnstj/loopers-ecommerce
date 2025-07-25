package com.loopers.user.presentation

import com.loopers.auth.presentation.LoginId
import com.loopers.support.presentation.ApiResponse
import com.loopers.user.application.UserFacade
import com.loopers.user.presentation.dto.MyDetailResponse
import com.loopers.user.presentation.dto.SignUpRequest
import com.loopers.user.presentation.dto.SignUpResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserV1Controller(
    private val userFacade: UserFacade,
) {

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @RequestBody @Valid request: SignUpRequest,
    ): ApiResponse<SignUpResponse> {
        val result = userFacade.createUser(request.toCommand())
        return ApiResponse.success(SignUpResponse(result))
    }

    @GetMapping("/api/v1/users/me")
    fun searchMyDetail(
        @LoginId loginId: String,
    ): ApiResponse<MyDetailResponse> {
        val result = userFacade.searchDetailByLoginId(loginId)
        return ApiResponse.success(MyDetailResponse(result))
    }
}
