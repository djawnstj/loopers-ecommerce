package com.loopers.batch.service

import com.loopers.batch.processor.ProductInfo
import com.loopers.batch.processor.ProductInfoService
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service

@Service
class ProductInfoServiceImpl : ProductInfoService {
    
    @PersistenceContext
    private lateinit var entityManager: EntityManager
    
    override fun getProductInfo(productId: Long): ProductInfo? {
        val query = entityManager.createQuery(
            """
            SELECT new com.loopers.batch.processor.ProductInfo(p.id, p.name, p.brandId)
            FROM Product p 
            WHERE p.id = :productId AND p.deletedAt IS NULL
            """,
            ProductInfo::class.java
        )
        query.setParameter("productId", productId)
        
        return try {
            query.singleResult
        } catch (e: Exception) {
            null
        }
    }
}