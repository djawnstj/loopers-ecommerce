package com.loopers.infrastructure.brand.fake

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.vo.BrandStatusType

class TestBrandRepository : BrandRepository {
    private val brands = mutableListOf<Brand>()
    private var nextId = 1L

    fun save(brand: Brand): Brand {
        val idField = brand.javaClass.superclass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(brand, nextId++)

        this.brands.add(brand)
        return brand
    }

    override fun findActiveBrandById(id: Long): Brand? {
        return brands.find { 
            it.id == id && it.status == BrandStatusType.ACTIVE && it.deletedAt == null 
        }
    }
}
