package com.loopers.domain.brand

import com.loopers.domain.brand.vo.BrandStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class BrandTest {

    @Nested
    inner class `브랜드를 생성할 때` {

        @Test
        fun `이름으로 브랜드를 생성할 수 있다`() {
            // given
            val name = "테스트 브랜드"

            // when
            val brand = Brand(name)

            // then
            assertAll(
                { assertThat(brand.name).isEqualTo("테스트 브랜드") },
                { assertThat(brand.status).isEqualTo(BrandStatusType.ACTIVE) },
            )
        }

        @Test
        fun `이름과 상태로 브랜드를 생성할 수 있다`() {
            // given
            val name = "테스트 브랜드"
            val status = BrandStatusType.INACTIVE

            // when
            val brand = Brand(name, status)

            // then
            assertAll(
                { assertThat(brand.name).isEqualTo("테스트 브랜드") },
                { assertThat(brand.status).isEqualTo(BrandStatusType.INACTIVE) },
            )
        }
    }
}
