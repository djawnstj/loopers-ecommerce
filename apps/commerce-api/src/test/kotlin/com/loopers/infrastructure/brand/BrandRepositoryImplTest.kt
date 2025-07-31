package com.loopers.infrastructure.brand

import com.loopers.domain.brand.Brand
import com.loopers.domain.brand.BrandRepository
import com.loopers.fixture.brand.BrandFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrandRepositoryImplTest(
    private val cut: BrandRepository,
    private val jpaRepository: JpaBrandRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `ID로 활성 브랜드를 조회할 때` {

        @Test
        fun `존재하는 활성 브랜드 ID로 조회하면 해당 브랜드를 반환한다`() {
            // given
            val brand = BrandFixture.`활성 브랜드`.toEntity()
            val savedBrand = jpaRepository.saveAndFlush(brand)

            // when
            val actual =cut.findActiveBrandById(savedBrand.id)

            // then
            assertThat(actual).isNotNull
                .extracting("name", "status")
                .containsExactly("활성브랜드", com.loopers.domain.brand.vo.BrandStatusType.ACTIVE)
        }

        @Test
        fun `존재하지 않는 ID로 조회하면 null을 반환한다`() {
            // given
            val nonExistentId = 999L

            // when
            val actual =cut.findActiveBrandById(nonExistentId)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `비활성 브랜드 ID로 조회하면 null을 반환한다`() {
            // given
            val inactiveBrand = BrandFixture.`비활성 브랜드`.toEntity()
            val savedBrand = jpaRepository.saveAndFlush(inactiveBrand)

            // when
            val actual =cut.findActiveBrandById(savedBrand.id)

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `삭제된 브랜드 ID로 조회하면 null을 반환한다`() {
            // given
            val brand = BrandFixture.`활성 브랜드`.toEntity().also(Brand::delete)
            val savedBrand = jpaRepository.saveAndFlush(brand)

            // when
            val actual =cut.findActiveBrandById(savedBrand.id)

            // then
            assertThat(actual).isNull()
        }
    }
}
