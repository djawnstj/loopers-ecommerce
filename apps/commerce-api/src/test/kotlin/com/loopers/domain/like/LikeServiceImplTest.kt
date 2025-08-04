package com.loopers.domain.like

import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.infrastructure.like.fake.TestLikeRepository
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LikeServiceImplTest {

    @Nested
    inner class `좋아요를 등록할 때` {
        @Test
        fun `동일한 정보의 좋아요가 있다면 저장하지 않는다`() {
            // given
            val originalRepository = TestLikeRepository()
            val likeRepository = spyk(originalRepository)
            val cut = LikeServiceImpl(likeRepository)
            val like = LikeFixture.기본.toEntity()

            originalRepository.save(like)

            // when
            cut.addLike(like)

            // then
            verify(exactly = 0) {
                likeRepository.save(
                    match {
                        it.userId == 1L &&
                                it.targetId == 1L &&
                                it.target == TargetType.PRODUCT
                    },
                )
            }
        }

        @Test
        fun `동일한 정보의 좋아요가 없다면 좋아요를 저장 한다`() {
            // given
            val likeRepository = spyk(TestLikeRepository())
            val cut = LikeServiceImpl(likeRepository)
            val like = LikeFixture.기본.toEntity()

            // when
            cut.addLike(like)

            // then
            verify(exactly = 1) {
                likeRepository.save(
                    match {
                        it.userId == 1L &&
                                it.targetId == 1L &&
                                it.target == TargetType.PRODUCT
                    },
                )
            }
        }
    }

    @Test
    fun `좋아요를 취소할 수 있다`() {
        // given
        val likeRepository = spyk(TestLikeRepository())
        val cut = LikeServiceImpl(likeRepository)
        val like = LikeFixture.기본.toEntity()

        // when
        cut.cancelLike(like)

        // then
        verify(exactly = 1) { likeRepository.delete(like) }
    }
}
