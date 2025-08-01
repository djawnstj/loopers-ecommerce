package com.loopers.domain.like

import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.infrastructure.like.JpaLikeRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull

class LikeServiceIntegrationTest(
    private val cut: LikeService,
    private val jpaLikeRepository: JpaLikeRepository,
) : IntegrationTestSupport() {

    @Nested
    inner class `좋아요를 등록할 때` {

        @Test
        fun `동일한 정보의 좋아요가 있다면 등록하지 않는다`() {
            // given
            val like = LikeFixture.기본.toEntity()
            jpaLikeRepository.saveAndFlush(like)

            // when
            cut.addLike(like)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `동일한 정보의 좋아요가 없다면 좋아요를 등록 한다`() {
            // given
            val like = LikeFixture.기본.toEntity()

            // when
            cut.addLike(like)

            // then
            val actual = jpaLikeRepository.findAll()
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `좋아요 등록하면 좋아요 정보를 저장 한다`() {
            // given
            val like = LikeFixture.기본.toEntity()

            // when
            cut.addLike(like)

            // then
            val actual = jpaLikeRepository.findByIdOrNull(like.id)
            assertThat(actual)
                .extracting("userId", "targetId", "target")
                .containsExactly(1L, 1L, TargetType.PRODUCT)
        }
    }

    @Nested
    inner class `좋아요를 취소할 때` {
        @Test
        fun `취소 후에는 동일한 좋아요 정보가 삭제 된다`() {
            // given
            val like = jpaLikeRepository.saveAndFlush(LikeFixture.기본.toEntity())

            // when
            cut.cancelLike(like)

            // then
            val actual = jpaLikeRepository.findByIdOrNull(like.id)
            assertThat(actual).isNull()
        }
    }
}
