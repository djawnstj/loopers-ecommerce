package com.loopers.domain.like

import com.loopers.domain.BaseEntity
import com.loopers.domain.like.vo.TargetType
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "likes")
class Like(
    userId: Long,
    targetId: Long,
    target: TargetType,
) : BaseEntity() {
    var userId: Long = userId
        protected set
    var targetId: Long = targetId
        protected set
    var target: TargetType = target
        protected set
}
