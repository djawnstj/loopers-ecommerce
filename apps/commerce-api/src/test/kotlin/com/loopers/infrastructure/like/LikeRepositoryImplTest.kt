package com.loopers.infrastructure.like

import com.loopers.domain.like.Like
import com.loopers.domain.like.LikeRepository
import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
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

    @Nested
    @DisplayName("회원 식별자와 좋아요 대상 식별자, 대상 타입으로 좋아요가 존재하는지 조회할 때")
    inner class Exists {
        @Test
        fun `해당 정보가 이미 존재 한다면 true 를 반환 한다`() {
            // given
            val userId: Long = 1
            val targetId: Long = 1
            val target = TargetType.PRODUCT

            jpaRepository.saveAndFlush(Like(userId, targetId, target))

            // when
            val actual = cut.existsByUserIdAndTargetIdAndTarget(userId, targetId, target)

            // then
            assertThat(actual).isTrue
        }

        @Test
        fun `해당 정보가 없다면 false 를 반환 한다`() {
            // given
            val userId: Long = 1
            val targetId: Long = 1
            val target = TargetType.PRODUCT

            // when
            val actual = cut.existsByUserIdAndTargetIdAndTarget(userId, targetId, target)

            // then
            assertThat(actual).isFalse
        }
    }

    @Nested
    inner class `좋아요를 삭제할 때` {
        @Test
        fun `회원 식별자, 좋아요 대상 식별자, 대상 타입이 같은 좋아요 정보가 존재한다면 삭제 된다`() {
            // given
            val like = jpaRepository.saveAndFlush(LikeFixture.기본.toEntity())

            // when
            cut.delete(LikeFixture.기본.toEntity())

            // then
            val actual = jpaRepository.findByIdOrNull(like.id)
            assertThat(actual).isNull()
        }
    }
}
