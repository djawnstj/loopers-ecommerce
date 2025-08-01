package com.loopers.domain.brand

import com.loopers.fixture.brand.BrandFixture
import com.loopers.infrastructure.brand.JpaBrandRepository
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrandServiceIntegrationTest(
    private val cut: BrandService,
    private val jpaBrandRepository: JpaBrandRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `활성 브랜드 상세 정보를 조회할 때` {

        @Test
        fun `존재하는 활성 브랜드 ID로 조회하면 해당 브랜드를 반환한다`() {
            // given
            val brand = BrandFixture.`활성 브랜드`.toEntity()
            val savedBrand = jpaBrandRepository.saveAndFlush(brand)

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

        @Test
        fun `비활성 브랜드 ID로 조회하면 CoreException BRAND_NOT_FOUND 예외를 던진다`() {
            // given
            val inactiveBrand = BrandFixture.`비활성 브랜드`.toEntity()
            val savedBrand = jpaBrandRepository.saveAndFlush(inactiveBrand)

            // when then
            assertThatThrownBy {
                cut.getActiveBrandDetail(savedBrand.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.BRAND_NOT_FOUND,
                    "식별자가 ${savedBrand.id} 에 해당하는 브랜드 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `삭제된 브랜드 ID로 조회하면 CoreException BRAND_NOT_FOUND 예외를 던진다`() {
            // given
            val brand = BrandFixture.`활성 브랜드`.toEntity().also(Brand::delete)
            val savedBrand = jpaBrandRepository.saveAndFlush(brand)

            // when then
            assertThatThrownBy {
                cut.getActiveBrandDetail(savedBrand.id)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.BRAND_NOT_FOUND,
                    "식별자가 ${savedBrand.id} 에 해당하는 브랜드 정보를 찾지 못했습니다.",
                )
        }
    }
}
