# 상품 목록 조회
```mermaid
sequenceDiagram
    actor U as User
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository

    U ->> PC: 상품 목록 조회 요청
    activate PC
    PC ->> PS: 상품 목록 조회
    activate PS
    deactivate PC
    PS ->> PR: 상품 목록 조회
    activate PR
    deactivate PS
    PR -->> U: 상품 목록 응답
    deactivate PR
```

# 상품 상세 조회
```mermaid
sequenceDiagram
    actor U as User
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository

    U ->> PC: 상품 상세 조회 요청
    activate PC
    PC ->> PS: 상품 상세 조회
    activate PS
    PS ->> PR: 상품 상세 조회
    activate PR
    opt 상품이 없음
        PS -->> PC: 404 Not Found
        deactivate PS
        deactivate PC
    end
    PR ->> U: 상품 상세 응답
    deactivate PR
```

# 브랜드 조회
```mermaid
sequenceDiagram
    actor U as User
    participant BC as BrandController
    participant BS as BrandService
    participant BR as BrandRepository

    U ->> BC: 브랜드 조회 요청
    activate BC
    BC ->> BS: 브랜드 조회
    activate BS
    BS ->> BR: 브랜드 조회
    activate BR
    opt 브랜드가 없음
        BS -->> BC: 404 Not Found
        deactivate BS
        deactivate BC
    end
    BR ->> U: 브랜드 응답
    deactivate BR
```

# 상품 좋아요 등록
```mermaid
sequenceDiagram
    actor U as User
    participant PLC as ProductLikeController
    participant US as UserService
    participant PS as ProductService
    participant PLS as ProductLikeService
    participant PLR as ProductLikeRepository

    U ->> PLC: 좋아요 등록 요청
    activate PLC
    PLC ->> US: 사용자 확인
    activate US
    opt 사용자 확인 실패
        US -->> PLC: 401 Unauthorized
    end
    US ->> PLC: 사용자 정보 반환
    deactivate US
    PLC ->> PS: 상품 정보 조회
    activate PS
    opt 상품 없음
        PS -->> PLC: 404 Not Found
    end
    PS ->> PLC: 상품 정보 반환
    deactivate PS
    PLC ->> PLS: 좋아요 등록
    activate PLS
    deactivate PLC
    alt 이미 좋아요 있는 경우
        PLS ->> PLR: 저장하지 않음
        activate PLR
    else 좋아요 없는 경우
        PLS ->> PLR: 좋아요 저장
        deactivate PLS
    end
    PLR ->> U: 좋아요 저장 성공
    deactivate PLR
```

# 상품 좋아요 취소
```mermaid
sequenceDiagram
    actor U as User
    participant PLC as ProductLikeController
    participant US as UserService
    participant PS as ProductService
    participant PLS as ProductLikeService
    participant PLR as ProductLikeRepository

    U ->> PLC: 좋아요 취소 요청
    activate PLC
    PLC ->> US: 사용자 확인
    activate US
    opt 사용자 확인 실패
        US -->> PLC: 401 Unauthorized
    end
    US ->> PLC: 사용자 정보 반환
    deactivate US
    PLC ->> PS: 상품 정보 조회
    activate PS
    opt 상품 없음
        PS -->> PLC: 404 Not Found
    end
    PS ->> PLC: 상품 정보 반환
    deactivate PS
    PLC ->> PLS: 좋아요 취소
    activate PLS
    deactivate PLC
    alt 이미 좋아요 있는 경우
        PLS ->> PLR: 좋아요 삭제
        activate PLR
    else 좋아요 없는 경우
        PLS ->> PLR: 삭제하지 않음
        deactivate PLS
    end
    PLR ->> U: 좋아요 삭제 성공
    deactivate PLR
```

# 주문서 생성
```mermaid
sequenceDiagram
    actor U as User
    participant OC as OrderController
    participant US as UserService
    participant PS as ProductService
    participant OS as OrderService
    participant OSR as OrderSheetRepository
    participant PSR as ProductSnapshotRepository
        
    U ->> OC: 주문서 생성 요청
    activate OC
    OC ->> US: 사용자 확인
    activate US
    opt 사용자 확인 실패
        US -->> OC: 401 Unauthorized
    end
    US ->> OC: 사용자 정보 반환
    deactivate US
    OC ->> PS: 상품 정보 조회
    activate PS
    alt 상품 없음
        PS -->> OC: 404 Not Found
    else 상품이 판매중이 아님
        PS -->> OC: 409 Conflict
    else 상품 재고가 부족함
        PS -->> OC: 409 Conflict
    end
    PS ->> OC: 상품 정보 반환
    deactivate PS
    OC ->> OS: 주문서 생성
    activate OS
    deactivate OC
    OS ->> OSR: 주문서 저장
    activate OSR
    OSR ->> OS: 주문서 저장 성공
    deactivate OSR
    OS ->> PSR: 상품 스냅샷 저장
    deactivate OS
    activate PSR
    PSR ->> U: 상품 스냅샷 저장 성공
    deactivate PSR
```

# 주문 생성
```mermaid
sequenceDiagram
    actor U as User
    participant OC as OrderController
    participant US as UserService
    participant PS as ProductService
    participant OS as OrderService
    participant OR as OrderRepository

    U ->> OC: 주문 생성 요청
    activate OC
    OC ->> US: 사용자 확인
    activate US
    opt 사용자 확인 실패
        US -->> OC: 401 Unauthorized
    end
    US ->> OC: 사용자 정보 반환
    deactivate US
    OC ->> PS: 상품 정보 조회
    activate PS
    alt 상품 없음
        PS -->> OC: 404 Not Found
    else 상품이 판매중이 아님
        PS -->> OC: 409 Conflict
    else 상품 재고가 부족함
        PS -->> OC: 409 Conflict
    end
    deactivate PS
    OC ->> OS: 주문 생성
    deactivate OC
    activate OS
    OS ->> OR: 주문 저장
    deactivate OS
    activate OR
    OR ->> U: 주문서 저장 성공
    deactivate OR
```

# 결제
```mermaid
sequenceDiagram
    actor U as User
    participant PC as PaymentController
    participant US as UserService
    participant UPS as UserPointService
    participant PS as ProductService
    participant PS as ProductService
    participant PaS as PaymentService
    participant PR as PaymentRepository

    U ->> PC: 주문서 생성 요청
    activate PC
    PC ->> US: 사용자 확인
    activate US
    opt 사용자 확인 실패
        US -->> PC: 401 Unauthorized
    end
    US ->> PC: 사용자 정보 반환
    deactivate US
    PC ->> UPS: 포인트 차감
    activate UPS
    alt 포인트 정보 없음
        UPS -->> PC: 404 Not Found
    else 보인트 잔액 부족
        UPS -->> PC: 409 Conflict
    end
    UPS ->> PC: 포인트 차감 완료
    deactivate UPS
    PC ->> PS: 상품 재고 차감
    activate PS
    alt 상품 없음
        PS -->> PC: 404 Not Found
    else 상품이 판매중이 아님
        PS -->> PC: 409 Conflict
    else 상품 재고가 부족함
        PS -->> PC: 409 Conflict
    end
    PS ->> PC: 상품 재고 차감 완료
    deactivate PS
    PC ->> PaS: 결제 내역 생성
    activate PaS
    deactivate PC
    PaS ->> PR: 결제 내역 저장
    activate PR
    deactivate PaS
    PR ->> U: 결제 내역 저장 완료
    deactivate PR
```
