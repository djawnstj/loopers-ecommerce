TRUNCATE TABLE likes;
TRUNCATE TABLE product;
TRUNCATE TABLE brand;

-- 파라미터
SET @BRANDS := 2000; -- 생성할 브랜드 수
SET @PRODUCTS_PER_BRAND := 5000; -- 브랜드당 상품 수
SET @LIKES_PER_PRODUCT := 5; -- 상품당 좋아요 수(균등)
SET @USERS := 50000; -- user_id 범위
SET @DELETED_RATIO := 0.05; -- 좋아요 중 DELETED 비율

SET SESSION cte_max_recursion_depth = 1000000;

START TRANSACTION;

-- 1) BRAND 생성
INSERT INTO brand (name, status, created_at, updated_at, deleted_at)
SELECT CONCAT('Brand_', s.n),
       CASE
           WHEN RAND() < 0.15 THEN 'INACTIVE'
           ELSE 'ACTIVE'
           END AS status,
       NOW(6),
       NOW(6),
       NULL
FROM (WITH RECURSIVE seq(n) AS (SELECT 1
                                UNION ALL
                                SELECT n + 1
                                FROM seq
                                WHERE n < (SELECT @BRANDS))
      SELECT n
      FROM seq) AS s;

-- 2) PRODUCT 생성 (각 브랜드당 균등 개수)
INSERT INTO product (brand_id, name, sale_start_at, status, created_at, updated_at, deleted_at)
SELECT b.id,
       CONCAT('Product_', b.id, '_', ps.n),
       DATE_ADD(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) AS sale_start_at,
       CASE
           WHEN RAND() < 0.15 THEN 'INACTIVE'
           ELSE 'ACTIVE'
           END AS status,
       NOW(6),
       NOW(6),
       NULL
FROM brand b
         CROSS JOIN (SELECT n
                     FROM (WITH RECURSIVE seq(n) AS (SELECT 1
                                                     UNION ALL
                                                     SELECT n + 1
                                                     FROM seq
                                                     WHERE n < (SELECT @PRODUCTS_PER_BRAND))
                           SELECT n
                           FROM seq) x) ps;

-- 3) LIKES 생성 (상품당 @LIKES_PER_PRODUCT개 균등 분포)
INSERT INTO likes (user_id, target_id, target, created_at, updated_at, deleted_at)
SELECT 1 + MOD(p.id * 131071 + ls.n * 524287, (SELECT @USERS))                     AS user_id,
       p.id                                                                         AS target_id,
       'PRODUCT'                                                                    AS target,
       NOW(6),
       NOW(6),
       CASE WHEN RAND() < (SELECT @DELETED_RATIO) THEN NOW(6) ELSE NULL END        AS deleted_at
FROM product p
         CROSS JOIN (SELECT n
                     FROM (WITH RECURSIVE seq(n) AS (SELECT 1
                                                     UNION ALL
                                                     SELECT n + 1
                                                     FROM seq
                                                     WHERE n < (SELECT @LIKES_PER_PRODUCT))
                           SELECT n
                           FROM seq) x) ls;

COMMIT;

-- 확인
SELECT COUNT(*) AS brands
FROM brand;
SELECT COUNT(*) AS products
FROM product;
SELECT COUNT(*) AS likes
FROM likes;