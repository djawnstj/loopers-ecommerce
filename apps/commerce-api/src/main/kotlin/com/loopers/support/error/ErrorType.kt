package com.loopers.support.error

import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: String, val message: String) {
    // User
    INVALID_USER_ID_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "로그인 ID 는 6자 이상 10자 이내의 영문 및 숫자만 허용됩니다.",
    ),
    INVALID_USER_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "이메일 형식이 올바르지 않습니다."),
    INVALID_USER_BIRTH_DAY_FORMAT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "생년월일 형식은 'yyyy-MM-dd' 형식이어야 합니다.",
    ),
    REQUIRED_USER_BIRTH_DAY_AFTER_THEN_NOW(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "생년월일은 현재 날짜보다 이전이어야 합니다.",
    ),
    EXISTS_USER_LOGIN_ID(HttpStatus.CONFLICT, HttpStatus.CONFLICT.reasonPhrase, "이미 가입 된 ID 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "회원 정보를 찾을 수 없습니다."),

    // Auth
    REQUIRED_LOGIN_ID_HEADER(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "loginId 가 누락되었습니다."),

    // UserPoint
    REQUIRED_ZERO_OR_POSITIVE_POINT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "포인트는 0 이상이어야 합니다."),
    REQUIRED_POSITIVE_POINT_CHARGE_AMOUNT(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "포인트 충전은 0 보다 커야합니다."),
    POINT_BALANCE_EXCEEDED(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "사용 가능한 포인트를 초과했습니다."),

    // Brand
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "브랜드 정보를 찾을 수 없습니다."),

    // Product
    INVALID_PRICE_VALUE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "가격은 0 이상이어야 합니다."),
    INVALID_QUANTITY_VALUE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "수량은 0 이상이어야 합니다."),
    PRODUCT_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "상품 아이템 정보를 찾을 수 없습니다."),
    REQUIRED_ZERO_OR_POSITIVE_PRODUCT_LIKE_COUNT(
        HttpStatus.BAD_REQUEST,
        HttpStatus.BAD_REQUEST.reasonPhrase,
        "상품 좋아요는 0 이상이어야 합니다.",
    ),
    INSUFFICIENT_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "상품 재고가 부족합니다."),

    // Order
    INVALID_MONEY_VALUE(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "가격은 0 이상이어야 합니다."),
    REQUIRED_NOT_EMPTY_ORDER_ITEMS(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "주문 시 주문 아이템은 1개 이상이어야 합니다."),

    /** 범용 에러 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, "일시적인 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, "존재하지 않는 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.reasonPhrase, "이미 존재하는 리소스입니다."),
    USER_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.reasonPhrase, ""),
}
