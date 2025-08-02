package com.loopers.presentation.point

import com.loopers.presentation.auth.LoginId
import com.loopers.application.point.UserPointFacade
import com.loopers.presentation.point.dto.BalanceResponse
import com.loopers.presentation.point.dto.ChargePointRequest
import com.loopers.presentation.point.dto.ChargePointResponse
import com.loopers.support.presentation.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserPointV1Controller(
    private val userPointFacade: UserPointFacade,
) {

    @PostMapping("/api/v1/points/charge")
    fun chargePoint(
        @LoginId loginId: String,
        @RequestBody @Valid request: ChargePointRequest,
    ): ApiResponse<ChargePointResponse> {
        val result = userPointFacade.increasePoint(loginId, request.amount)
        return ApiResponse.success(ChargePointResponse(result.balance))
    }

    @GetMapping("/api/v1/points")
    fun getBalance(
        @LoginId loginId: String,
    ): ApiResponse<BalanceResponse> {
        val result = userPointFacade.getPointBalance(loginId)
        return ApiResponse.success(BalanceResponse(result.balance))
    }
}
