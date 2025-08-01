package com.loopers.domain.brand

import com.loopers.fixture.brand.BrandFixture
import com.loopers.infrastructure.brand.fake.TestBrandRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrandServiceImplTest {

    @Nested
    inner class `활성 브랜드 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 브랜드 ID로 조회하면 해당 브랜드를 반환한다`() {
            // given
            val brandRepository = TestBrandRepository()
            val cut = BrandServiceImpl(brandRepository)

            val brand = BrandFixture.`활성 브랜드`.toEntity()
            val savedBrand = brandRepository.save(brand)

            // when
            val actual = cut.getActiveBrandDetail(savedBrand.id)

            // then
            assertThat(actual).isNotNull
                .extracting("name", "status")
                .containsExactly("활성브랜드", com.loopers.domain.brand.vo.BrandStatusType.ACTIVE)
        }

        @Test
        fun `존재하지 않는 브랜드 ID로 조회하면 CoreException BRAND_NOT_FOUND 예외를 던진다`() {
            // given
            val brandRepository = TestBrandRepository()
            val cut = BrandServiceImpl(brandRepository)

            val nonExistentId = 999L

            // when then
            assertThatThrownBy {
                cut.getActiveBrandDetail(nonExistentId)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.BRAND_NOT_FOUND,
                    "식별자가 999 에 해당하는 브랜드 정보를 찾지 못했습니다.",
                )
        }
    }
}
