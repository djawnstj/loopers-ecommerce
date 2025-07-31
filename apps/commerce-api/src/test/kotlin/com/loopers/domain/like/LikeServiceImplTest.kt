package com.loopers.domain.like

import com.loopers.domain.like.vo.TargetType
import com.loopers.fixture.like.LikeFixture
import com.loopers.infrastructure.like.fake.TestLikeRepository
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test

class LikeServiceImplTest {

    @Test
    fun `좋아요를 등록할 수 있다`() {
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
