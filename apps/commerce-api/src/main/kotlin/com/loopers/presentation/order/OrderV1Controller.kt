package com.loopers.presentation.order

import com.loopers.application.order.OrderFacade
import com.loopers.presentation.auth.LoginId
import com.loopers.presentation.order.dto.CreateOrderRequest
import com.loopers.presentation.order.dto.CreateOrderResponse
import com.loopers.support.presentation.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderV1Controller(
    private val orderFacade: OrderFacade,
) {

    @PostMapping("/api/v1/orders")
    fun createOrder(
        @LoginId loginId: String,
        @RequestBody request: CreateOrderRequest,
    ): ApiResponse<CreateOrderResponse> {
        val result = orderFacade.createOrder(request.toCommand(loginId))
        return ApiResponse.success(CreateOrderResponse(result))
    }
}
