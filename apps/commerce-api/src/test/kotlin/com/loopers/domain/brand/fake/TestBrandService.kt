package com.loopers.domain.brand.fake

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.BrandService
import com.loopers.domain.brand.vo.BrandStatusType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class TestBrandService : BrandService {
    private val brands = mutableMapOf<Long, Brand>()
    private var nextId = 1L

    fun addBrand(name: String, status: BrandStatusType = BrandStatusType.ACTIVE): Brand {
        val brand = Brand(name, status).apply {
            val idField = this::class.java.superclass.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(this, nextId)
        }
        brands[nextId] = brand
        nextId++
        return brand
    }

    fun clear() {
        brands.clear()
        nextId = 1L
    }

    override fun getActiveBrandDetail(id: Long): Brand {
        val brand = brands[id] ?: throw CoreException(
            ErrorType.BRAND_NOT_FOUND,
            "식별자가 $id 에 해당하는 브랜드 정보를 찾지 못했습니다.",
        )

        if (brand.status != BrandStatusType.ACTIVE) {
            throw CoreException(
                ErrorType.BRAND_NOT_FOUND,
                "식별자가 $id 에 해당하는 브랜드 정보를 찾지 못했습니다.",
            )
        }

        return brand
    }
}
