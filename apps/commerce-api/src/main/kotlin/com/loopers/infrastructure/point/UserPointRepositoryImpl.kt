package com.loopers.infrastructure.point

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.loopers.domain.point.UserPoint
import com.loopers.domain.point.UserPointRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserPointRepositoryImpl(
    private val jpaUserPointRepository: JpaUserPointRepository,
    private val entityManager: EntityManager,
) : UserPointRepository {
    override fun save(userPoint: UserPoint): UserPoint = jpaUserPointRepository.save(userPoint)

    override fun findByUserId(userId: Long): UserPoint? = jpaUserPointRepository.findAll {
        select(
            entity(UserPoint::class),
        ).from(
            entity(UserPoint::class),
        ).whereAnd(
            path(UserPoint::userId).eq(userId),
            path(UserPoint::deletedAt).isNull(),
        )
    }.firstOrNull()

    @Transactional
    override fun findByUserIdWithPessimisticWrite(userId: Long): UserPoint? {
        val query = jpql {
            select(
                entity(UserPoint::class),
            ).from(
                entity(UserPoint::class),
            ).whereAnd(
                path(UserPoint::userId).eq(userId),
                path(UserPoint::deletedAt).isNull(),
            )
        }

        return entityManager.createQuery(query, JpqlRenderContext())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .resultList.firstOrNull()
    }
}
