package com.loopers.domain.like

import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.infrastructure.like.JpaLikeRepository
import com.loopers.support.IntegrationTestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LikeServiceIntegrationTest(
    private val cut: LikeService,
    private val jpaLikeRepository: JpaLikeRepository,
) : IntegrationTestSupport() {

    @Test
    fun `좋아요 등록하면 좋아요 정보를 저장 한다`() {
        // given
        val like = LikeFixture.기본.toEntity()

        // when
        cut.addLike(like)

        // then
        val savedLikes = jpaLikeRepository.findAll()
        assertThat(savedLikes).hasSize(1)
        assertThat(savedLikes[0])
            .extracting("userId", "targetId", "target")
            .containsExactly(1L, 1L, TargetType.PRODUCT)
    }
}
