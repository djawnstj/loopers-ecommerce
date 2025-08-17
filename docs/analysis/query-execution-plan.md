# 상품 목록 조회 실행 계획 분석

상품 목록을 브랜드 ID 로 필터링 하여 좋아요 많은 순 정렬로 조회하는 쿼리를 분석하였습니다.
브랜드 데이터는 2,000개, 상품 데이터는 1,000만개로 준비하였고 각 브랜드 당 5,000개의 상품을 가지도록 준비하였습니다.
쿼리 순서는 다음과 같습니다.

1. 좋아요 카운트 비정규화 전 인덱스 없이 조인 쿼리
2. 좋아요 카운트 비정규화 전 인덱스 추가 조인 쿼리
3. 좋아요 카운트 비정규화 후 인덱스 없이 조인 쿼리
4. 좋아요 카운트 비정규화 후 인덱스 추가 조인 쿼리

## 1. 좋아요 카운트 비정규화 전 인덱스 없이 조인 쿼리

### 쿼리

```sql
select p.*
from product as p
         join product_like_count as plc
              on p.id = plc.product_id
where brand_id = 1
order by plc.count desc;
```

### 실행계획

| 테이블                | 접근방식 (type) | 접근 방식(key) | 스캔 행수 (rows) | 필터링  | 추가 정보(Extra)   |
|--------------------|-------------|------------|--------------|------|----------------|
| product            | ALL         | NULL       | 9,956,576행   | 10%  | Using where    |
| product_like_count | eq_ref      | PRIMARY    | 1행           | 100% | -              |
| 정렬                 | -           | -          | 969,940행     | -    | Using filesort |

**총계**  13 초

## 2. 좋아요 카운트 비정규화 전 인덱스 추가 조인 쿼리

### 쿼리

```sql
select p.*
from product as p
         join product_like_count as plc
              on p.id = plc.product_id
where brand_id = 1
order by plc.count desc;
```

### 인덱스

```sql
create index idx_product_brand_status_deleted
    on product (brand_id);

create index idx_product_like_count_composite
    on product_like_count (product_id, count desc);
```

### 실행계획

| 테이블                | 접근방식 (type) | 접근 방식(key)                       | 스캔 행수 (rows) | 필터링  | 추가 정보(Extra)                    |
|--------------------|-------------|----------------------------------|--------------|------|---------------------------------|
| product            | ref         | idx_product_brand_status_deleted | 5,000행       | 100% | -                               |
| product_like_count | ref         | idx_product_like_count_composite | 1행           | 100% | Using index                     |
| 정렬                 | -           | -                                | 5,000행       | -    | Using filesort; Using temporary |

**총계** 0.016초

## 3. 좋아요 카운트 비정규화 후 인덱스 없이 조인 쿼리

### 쿼리

```sql
select p.*
from product as p
where brand_id = 1
order by p.like_count desc;
```

### 실행계획

| 테이블     | 접근방식 (type) | 접근 방식(key) | 스캔 행수 (rows) | 필터링 | 추가 정보(Extra)   |
|---------|-------------|------------|--------------|-----|----------------|
| product | ALL         | NULL       | 9,699,404행   | 10% | Using where    |
| 정렬      | -           | -          | 969,940행     | -   | Using filesort |

**총계** 2-3초

## 4. 좋아요 카운트 비정규화 후 인덱스 추가 조인 쿼리

### 쿼리

```sql
select p.*
from product as p
where brand_id = 1
order by p.like_count desc;
```

### 인덱스

```sql
create index idx_product_brand_like_count
    on product (brand_id, like_count desc);
```

### 실행계획

| 테이블     | 접근방식 (type) | 접근 방식(key)                   | 스캔 행수 (rows) | 필터링  | 비용       | 추가 정보(Extra) |
|---------|-------------|------------------------------|--------------|------|----------|--------------|
| product | ref         | idx_product_brand_like_count | 5,000행       | 100% | 4,574.73 | -            |
| 정렬      | -           | -                            | -            | -    | 0        | Using index  |

**총계** 5,074.73 (약 0.05초)

## 비교 분석

### 성능 비교표

| 시나리오            | 쿼리 비용  | 스캔 행수      | 정렬방식     | 
|-----------------|--------|------------|----------|
| 1. JOIN (인덱스 전) | 13초    | 9,956,576행 | Filesort |
| 2. JOIN (인덱스 후) | 0.016초 | 5,000행     | Filesort |
| 3. 단순조회 (인덱스 전) | 2-3초   | 9,699,404행 | Filesort |
| 4. 단순조회 (인덱스 후) | 0.05초  | 5,000행     | Index    |

### 정리
인덱스를 사용하면서 속도 개선이 되었습니다. <br>

개인적으로 놀라운건 좋아요 카운트 비정규화 테이블이 빠를 것으로 예상하였으나 비정규화 하지 않은 테이블이 살짝 빠르다는 점 이었습니다. <br>
아무래도 product_like_count 테이블에도 인덱스를 추가한것이 커버링 인덱스를 사용하게 하여 살짝 빠른 결과를 만들어낸 것 같습니다. <br>
비정규화 테이블에서는 인덱스로 인해 정렬에서 이점을 보았지만 결국 인덱스 탐색 후 실제 row 를 조회하는 과정이 추가되어 살짝 느리다고 생각합니다. <br>

만약 실제 운영 환경이었다면 product_like_count 테이블에 인덱스를 추가하는 것은 쓰기 비용에서 단점이 생긴다고 생각합니다.  <br>
결론적으로는 운영에서 비정규화 테이블을 선택할 것 같지만 더 나아가 쓰기 테이블은 정규화 테이블을 유지하고 읽기 전용 테이블로 비정규화 하여 배치 업데이트 하는 방향을 선택할 것 같습니다. <br>
