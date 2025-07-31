package com.loopers.infrastructure.like

import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class LikeRepositoryImplTest(
    private val cut: LikeRepository,
    private val jpaRepository: JpaLikeRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class Save {

        @Test
        fun `좋아요 정보를 저장할 수 있다`() {
            // given
            val like = LikeFixture.기본.toEntity()

            // when
            cut.save(like)

            // then
            val actual = jpaRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `좋아요 정보를 저장 후 해당 좋아요 조회하면 동일한 정보를 조회할 수 있다`() {
            // given
            val like = LikeFixture.기본.toEntity()

            // when
            cut.save(like)

            // then
            val actual = jpaRepository.findByIdOrNull(like.id)
            assertThat(actual)
                .extracting("userId", "targetId", "target")
                .containsExactly(1L, 1L, TargetType.PRODUCT)
        }
    }
}
