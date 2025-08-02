package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import com.loopers.domain.brand.vo.BrandStatusType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "brand")
class Brand(
    name: String,
    status: BrandStatusType = BrandStatusType.ACTIVE,
) : BaseEntity() {
    var name: String = name
        protected set

    @Enumerated(EnumType.STRING)
    var status: BrandStatusType = status
        protected set
}
