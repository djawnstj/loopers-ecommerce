package com.loopers.application.like

import com.loopers.application.common.SimpleLockManager
import com.loopers.application.like.command.CreateProductLikeCommand
import com.loopers.application.like.command.DeleteProductLikeCommand
import com.loopers.domain.like.LikeServiceImpl
import com.loopers.domain.like.vo.TargetType
import com.loopers.domain.product.fake.TestProductService
import com.loopers.domain.user.fake.TestUserService
import com.loopers.fixture.product.ProductFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.infrastructure.like.fake.TestLikeRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import io.mockk.spyk
import io.mockk.verify
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
            val productService = TestProductService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val command = CreateProductLikeCommand(
                loginId = "nonexistent",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when then
            assertThatThrownBy {
                cut.createProductLike(command)
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
            val productService = TestProductService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))
            val product = ProductFixture.기본.toEntity()
            productService.addProducts(listOf(product))

            val command = CreateProductLikeCommand(
                loginId = "loginId",
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command)

            // then
            val actual = likeRepository.findAll()
            assertThat(actual).hasSize(1)
                .extracting("userId", "targetId", "target")
                .containsExactly(tuple(user.id, product.id, TargetType.PRODUCT))
        }

        @Test
        fun `동시에 같은 키로 요청해도 락으로 인해 중복 저장되지 않는다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val productService = TestProductService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))
            val product = ProductFixture.기본.toEntity()
            productService.addProducts(listOf(product))

            val command = CreateProductLikeCommand(
                loginId = "loginId",
                targetId = product.id,
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
                        cut.createProductLike(command)
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

        @Test
        fun `좋아요를 생성 하면 상품 좋아요 수를 증가한다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val testProductService = TestProductService()
            val productService = spyk(testProductService)
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))
            val product = ProductFixture.기본.toEntity()
            testProductService.addProducts(listOf(product))

            val command = CreateProductLikeCommand(
                loginId = "loginId",
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.createProductLike(command)

            // then
            verify { productService.increaseProductLikeCount(0L) }
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
            val productService = TestProductService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val command = DeleteProductLikeCommand(
                loginId = "nonexistent",
                targetId = 1L,
                target = TargetType.PRODUCT,
            )

            // when then
            assertThatThrownBy {
                cut.deleteProductLike(command)
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
            val productService = TestProductService()
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))
            val product = ProductFixture.기본.toEntity()
            productService.addProducts(listOf(product))

            val command = DeleteProductLikeCommand(
                loginId = "loginId",
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.deleteProductLike(command)

            // then
            val actual = likeRepository.findAll()
            assertThat(actual).isEmpty()
        }

        @Test
        fun `좋아요를 생성 하면 상품 좋아요 수를 증가한다`() {
            // given
            val likeRepository = TestLikeRepository()
            val likeService = LikeServiceImpl(likeRepository)
            val userService = TestUserService()
            val testProductService = TestProductService()
            val productService = spyk(testProductService)
            val lockManager = SimpleLockManager()
            val cut = LikeFacade(likeService, userService, productService, lockManager)

            val user = UserFixture.기본.toEntity()
            userService.addUsers(listOf(user))
            val product = ProductFixture.기본.toEntity()
            testProductService.addProducts(listOf(product))

            val command = DeleteProductLikeCommand(
                loginId = "loginId",
                targetId = product.id,
                target = TargetType.PRODUCT,
            )

            // when
            cut.deleteProductLike(command)

            // then
            verify { productService.decreaseProductLikeCount(product.id) }
        }
    }
}
