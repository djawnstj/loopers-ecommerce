package com.loopers.point.presentation

import com.loopers.auth.presentation.UserId
import com.loopers.point.application.UserPointFacade
import com.loopers.point.presentation.dto.BalanceResponse
import com.loopers.point.presentation.dto.ChargePointRequest
import com.loopers.point.presentation.dto.ChargePointResponse
import com.loopers.support.presentation.ApiResponse
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
        @UserId userId: String,
        @RequestBody request: ChargePointRequest,
    ): ApiResponse<ChargePointResponse> {
        val result = userPointFacade.increasePoint(userId, request.amount)
        return ApiResponse.success(ChargePointResponse(result.balance))
    }

    @GetMapping("/api/v1/points")
    fun getBalance(
        @UserId userId: String,
    ): ApiResponse<BalanceResponse> {
        val result = userPointFacade.getPointBalance(userId)
        return ApiResponse.success(BalanceResponse(result.balance))
    }
}
