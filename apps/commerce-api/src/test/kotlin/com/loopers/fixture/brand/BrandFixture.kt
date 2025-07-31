package com.loopers.fixture.brand

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.vo.BrandStatusType

sealed class BrandFixture(
    val name: String = "브랜드",
    val status: BrandStatusType = BrandStatusType.ACTIVE,
) {
    data object 기본 : BrandFixture()
    data object `활성 브랜드` : BrandFixture(name = "활성브랜드", status = BrandStatusType.ACTIVE)
    data object `비활성 브랜드` : BrandFixture(name = "비활성브랜드", status = BrandStatusType.INACTIVE)

    fun toEntity(): Brand = Brand(name, status)

    companion object {
        fun create(
            name: String = "브랜드",
            status: BrandStatusType = BrandStatusType.ACTIVE,
        ): Brand = Brand(name, status)
    }
}