package com.loopers.domain.brand

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service

interface BrandService {
    fun getActiveBrandDetail(id: Long): Brand
}

@Service
class BrandServiceImpl(
    private val brandRepository: BrandRepository,
) : BrandService {
    override fun getActiveBrandDetail(id: Long): Brand =
        brandRepository.findActiveBrandById(id) ?: throw CoreException(
            ErrorType.BRAND_NOT_FOUND,
            "식별자가 $id 에 해당하는 브랜드 정보를 찾지 못했습니다.",
        )
}
