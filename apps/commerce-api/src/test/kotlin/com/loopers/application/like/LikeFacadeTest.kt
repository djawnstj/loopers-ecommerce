package com.loopers.application.like

import com.loopers.application.common.SimpleLockManager
import com.loopers.application.like.command.CreateLikeCommand
import com.loopers.application.like.command.DeleteLikeCommand
import com.loopers.domain.like.LikeServiceImpl
import com.loopers.domain.like.vo.TargetType
import com.loopers.domain.user.fake.TestUserService
import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.like.fake.TestLikeRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class LikeFacadeTest {

    @Nested
    inner class `좋아요를 생성할 때` {

        @Test
        fun `존재하지 않는 사용자로 요청하면 CoreException USER_NOT_FOUND 예외를 던진다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, lockManager)

            val command = CreateLikeCommand(
                loginId = "nonexistent",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when & then
            assertThatThrownBy {
                cut.createLike(command)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.USER_NOT_FOUND,
                    "로그인 ID가 nonexistent 에 해당하는 유저 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `사용자가 존재하고 락 획득에 성공하면 좋아요를 저장한다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val command = CreateLikeCommand(
                loginId = "loginId",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createLike(command)

            // then
            val actual = likeRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "targetId", "target")
                .containsExactly(tuple(user.id, 1L, TargetType.PRODUCT))
        }

        @Test
        fun `동시에 같은 키로 요청해도 락으로 인해 중복 저장되지 않는다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val command = CreateLikeCommand(
                loginId = "loginId",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            val threadCount = 5
            val executor = Executors.newFixedThreadPool(threadCount)
            val startLatch = CountDownLatch(1)
            val endLatch = CountDownLatch(threadCount)

            // when
            repeat(threadCount) {
                executor.submit {
                    try {
                        startLatch.await()
                        cut.createLike(command)
                    } finally {
                        endLatch.countDown()
                    }
                }
            }

            startLatch.countDown()
            endLatch.await(10, TimeUnit.SECONDS)

            // then
            val actual = likeRepository.findAll()
            assertThat(actual).hasSize(1)
            executor.shutdown()
        }
    }

    @Nested
    inner class `좋아요를 삭제할 때` {

        @Test
        fun `존재하지 않는 사용자로 요청하면 CoreException USER_NOT_FOUND 예외를 던진다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, lockManager)

            val command = DeleteLikeCommand(
                loginId = "nonexistent",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when & then
            assertThatThrownBy {
                cut.deleteLike(command)
            }.isInstanceOf(CoreException::class.java)
                .extracting("errorType", "message")
                .containsExactly(
                    ErrorType.USER_NOT_FOUND,
                    "로그인 ID가 nonexistent 에 해당하는 유저 정보를 찾지 못했습니다.",
                )
        }

        @Test
        fun `사용자가 존재하고 락 획득에 성공하면 좋아요를 삭제 한다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))

            val command = DeleteLikeCommand(
                loginId = "loginId",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when
            cut.deleteLike(command)

            // then
            val actual = likeRepository.findAll()
            assertThat(actual).isEmpty()
        }
    }
}
