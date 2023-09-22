## 사용기술 ##
- Java 11
- Spring Boot 2.7
- JPA
- MySQL
- H2(Local 환경)
----------------------------------------
# Content #
- [Application Architecture](#application-architecture)
- [Flow Chart](#flow-chart)
  * [회원](#회원)
    + [회원가입 요청](#회원가입-요청)
    + [이메일 인증](#이메일-인증)
    + [JWT 토큰](#JWT-토큰)
  * [장바구니](#장바구니)
    + [장바구니 상품등록](#장바구니-상품등록)
    + [장바구니 조회](#장바구니-조회)
  * [상품](#상품)
    + [상품 카테고리 조회](#상품-카테고리-조회)
    + [상품 카테고리별 상세조건 검색 조회](#상품-카테고리별-상세조건-검색-조회)
    + [상품 단건 조회](#상품-단건-조회)
  * [어드민](#어드민)
    + [상품 등록요청](#상품-등록요청)
    + [상품 승인상태 업데이트](#상품-승인상태-업데이트)
    + [등록요청 상품 조회](#등록요청-상품-조회)
- [API](#api)
----------------------------------------
## 요구사항

### 로그인
사용자는 로그인 시 이메일 과 비밀번호를 요청으로 보내면 JWT 인증용 토큰을 응답받 을 수 있다.

### 장바구니

장바구니 API 는 JWT 인증과정을 진행 후 사용 가능하다.

**조회**

- 장바구니 조회 시 해당하는 사용자의 장바구니 가 조회 되야한다.

**상품 담기**

- 상품담기 시 해당하는 상품이 사용자의 장바구니에 담겨 야한다.

**삭제**

- 장바구니 삭제 시 해당하는 상품이 사용자의 장바구니에서 삭제 되어야 한다.

### 검색

검색은 사용자 인증 과정없이 기능 사용이 가능하다.

모든 검색결과는 페이징 기능을 제공한다.

검색결과 정렬기준은 기본적으로 상품이 등록된 등록 순 이다.

**상세검색**
- 상품의 정보들(상품명,브랜드명,등록일자 등)의 기준으로 검색이 가능하다.
- 상품의 정보를 기준으로 오름차순 또는 내림차 순으로 정렬 할 수 있다.

---
## ERD
![ERD](./docs/erd.png)
---

# Application Architecture #
![Structure](./docs/architecture.png)

---
# Flow Chart #

## 회원
### 회원가입 요청
```mermaid
    sequenceDiagram
    autonumber

    reqeust ->> AuthController : /api/auth/sign-up POST
    AuthController->> AuthService : signUp()
    AuthService ->> SignUpService : cachingSignUpInfo() with generate UUID
    SignUpService ->> Redis : caching email passwod and code  
    AuthController ->> EmailSQSEventPublisher : pub email and code
    AuthController -->> reqeust : response status code 200
```

### 이메일 인증
```mermaid
    sequenceDiagram
    autonumber

    reqeust ->> AuthController : /api/auth/verified/{code} GET
    AuthController->> AuthService : verifiedEmailCode()
    AuthService ->> SignUpService : verifiedCodeAndSaveUser()
    SignUpService ->> Redis : find by verified code for sign data
    Redis -->> SignUpService : hit
    SignUpService ->> LoginRepository : save user
    AuthService -->> AuthController : return email and verified time
    AuthController -->> reqeust : response status code 200
```

### JWT 토큰
```mermaid
    sequenceDiagram
    autonumber

    reqeust ->> AuthController : /api/auth/token POST
    AuthController->> AuthService : createToken()
    AuthService ->> UserRepository : find By email
    UserRepository ->> AuthService : find user
    AuthService ->> tokenProvider : createToken()
    tokenProvider -->> AuthService : jwt access token
    AuthService -->> AuthController : return token
    AuthController ->> reqeust: response status code 200
```
---
## 장바구니
### 장바구니 상품등록
```mermaid
    sequenceDiagram
    autonumber

    request ->> JwtAuthenticationInterceptor : /api/cart/add-product POST
    JwtAuthenticationInterceptor -->> request : if failed authentication status code 400 response
    JwtAuthenticationInterceptor ->> CartController : Successful authentication
    CartController->> CartService : addProduct()
    CartService ->> UserRepository : find by userEmail
    UserRepository -->> CartService : find User
    CartService ->> CartRepository : save Cart
    CartService -->>  CartController: return add cart info
    CartController -->> request : response status code 200
```

### 장바구니 조회
```mermaid
    sequenceDiagram
    autonumber

    request ->> JwtAuthenticationInterceptor : /api/cart GET
    JwtAuthenticationInterceptor -->> request : if failed authentication status code 400 response
    JwtAuthenticationInterceptor ->> CartController : Successful authentication
    CartController->> CartService : findCartByUserEmail
    CartService ->> UserRepository : existsByEmail()
    CartService ->> CartRepository : find by user email
    CartRepository -->> CartService: return cart list
    CartService -->>  CartController: return cart list
    CartController -->> request : response status code 200
```
---
## 상품

### 상품 카테고리 조회
```mermaid
    sequenceDiagram
    autonumber

    request ->> ProductSearchController : /api/categories/{category} GET
    ProductSearchController->> ProductService : findWithCategoryAndPagination()
    ProductService ->> ProductQueryDslRepository : findByCategoryWithPaginationOrderByBrandNew()
    ProductQueryDslRepository -->> ProductService : return category products for page 1
    ProductService -->>  ProductSearchController: return product list
    ProductSearchController -->> request : response status code 200
```

### 상품 카테고리별 상세조건 검색 조회
```mermaid
    sequenceDiagram
    autonumber

    request ->> ProductSearchController : /api/categories/{category}/detail GET
    ProductSearchController->> ProductService : findWithSearchCondition()
    ProductService ->> ProductQueryDslRepository : findBySearchCondition()
    ProductQueryDslRepository -->> ProductService : return category products
    ProductService -->>  ProductSearchController: return product list
    ProductSearchController -->> request : response status code 200
```

### 상품 단건 조회
```mermaid
    sequenceDiagram
    autonumber

    request ->> ProductSearchController : /api/product/{id} GET
    ProductSearchController->> ProductService : findOne()
    ProductService ->> ProductRepository : findById()
    ProductRepository -->> ProductService : return product
    ProductService -->>  ProductSearchController: return response product
    ProductSearchController -->> request : response status code 200
```
---
## 어드민
### 상품 등록요청
```mermaid
    sequenceDiagram
    autonumber

    request ->> AdminController : /api/add-request-product
    AdminController->> AdminService : addRequestProduct()
    AdminService ->> userRepository : validate user email
    AdminService ->> RequestProductRepository : save RequestProduct with wait approval
    AdminService -->>  AdminController: return AddrequestProduct response
    ProductSearchController -->> request : response status code 200
```

### 상품 승인상태 업데이트
```mermaid
    sequenceDiagram
    autonumber

    request ->> AdminAuthorizationInterceptor : /api/admin/update-approval POST
    AdminAuthorizationInterceptor -->> request : if failed authorization status code 400 response
    AdminAuthorizationInterceptor ->> AdminController : Successful authorization
    AdminController->> AdminService : updateApprovalStatus()
    AdminService ->> RequestProductRepository : update approval status
    AdminService -->>  AdminController: return update approvalstatus
    AdminController ->> EmailSQSEventPublisher : pub change approval status
    AdminController -->> request : response status code 200
```

### 등록요청 상품 조회
```mermaid
    sequenceDiagram
    autonumber

    request ->> AdminAuthorizationInterceptor : /api/admin/add-request-product GET
    AdminAuthorizationInterceptor -->> request : if failed authorization status code 400 response
    AdminAuthorizationInterceptor ->> AdminController : Successful authorization
    AdminController->> AdminService : findByApprovalStatus() with wait Approval
    AdminService ->> RequestProductRepository : find by approval status
    RequestProductRepository -->> AdminService : return requestProduct
    AdminService -->>  AdminController: return requestProduct list
    AdminController -->> request : response status code 200
```
---
