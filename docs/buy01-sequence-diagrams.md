# Buy01 — Sequence Diagrams

## 1. Sequence Diagrams

### 1.1 Seller Registration & Login

```mermaid
sequenceDiagram
    actor Seller
    participant FE as Angular (Auth Page)
    participant GW as Gateway
    participant US as User Service
    participant DB as MongoDB

    Seller->>FE: Fill sign-up form (role=SELLER)
    FE->>GW: POST /auth/register
    GW->>US: forward request
    US->>US: hash password (BCrypt)
    US->>DB: save user (role=SELLER)
    DB-->>US: user saved
    US-->>GW: 201 Created
    GW-->>FE: 201 Created
    FE-->>Seller: redirect to login

    Seller->>FE: Sign in (email, password)
    FE->>GW: POST /auth/login
    GW->>US: forward request
    US->>DB: find user + verify password
    DB-->>US: user found
    US->>US: generate JWT (role claim)
    US-->>GW: 200 OK + JWT
    GW-->>FE: 200 OK + JWT
    FE->>FE: store token, attach via interceptor
    FE-->>Seller: redirect to Seller Dashboard
```

### 1.2 Seller — Create Product

```mermaid
sequenceDiagram
    actor Seller
    participant FE as Angular (Seller Dashboard)
    participant GW as Gateway
    participant PS as Product Service
    participant DB as MongoDB
    participant KFK as Kafka (optional)

    Seller->>FE: Submit new product form
    FE->>FE: validate (price > 0, required fields)
    FE->>GW: POST /products (JWT in header)
    GW->>GW: verify JWT, propagate identity
    GW->>PS: forward request
    PS->>PS: check role == SELLER
    PS->>DB: insert product (sellerId = auth.subject)
    DB-->>PS: product saved
    PS->>KFK: publish PRODUCT_CREATED
    PS-->>GW: 201 Created
    GW-->>FE: 201 Created
    FE-->>Seller: show success toast
```

### 1.3 Seller — Upload & Attach Media

```mermaid
sequenceDiagram
    actor Seller
    participant FE as Angular (Media Page)
    participant GW as Gateway
    participant MS as Media Service
    participant PS as Product Service
    participant Store as Object Storage
    participant DB as MongoDB
    participant KFK as Kafka (optional)

    Seller->>FE: Select image file
    FE->>FE: check MIME type + size <= 2MB
    alt invalid file
        FE-->>Seller: show validation error (no request sent)
    else valid file
        FE->>GW: POST /media/images (multipart, JWT)
        GW->>MS: forward request
        MS->>MS: verify JWT + role == SELLER
        MS->>MS: sniff content, validate MIME + size
        alt invalid on server
            MS-->>GW: 400 Bad Request
            GW-->>FE: 400 Bad Request
            FE-->>Seller: show error toast
        else valid
            MS->>Store: store image binary
            Store-->>MS: stored, returns URL/id
            MS->>DB: save media metadata (ownerId, url)
            DB-->>MS: saved
            MS->>KFK: publish IMAGE_UPLOADED
            MS-->>GW: 201 Created + mediaId/url
            GW-->>FE: 201 Created
            FE->>GW: PUT /products/{id} (attach imageUrls[])
            GW->>PS: forward request
            PS->>PS: verify ownership (sellerId == auth.subject)
            PS->>DB: update product.imageUrls[]
            DB-->>PS: updated
            PS-->>GW: 200 OK
            GW-->>FE: 200 OK
            FE-->>Seller: show image preview, success
        end
    end
```

### 1.4 Client — Browse Products (Public)

```mermaid
sequenceDiagram
    actor Client
    participant FE as Angular (Product Listing)
    participant GW as Gateway
    participant PS as Product Service
    participant MS as Media Service
    participant DB as MongoDB

    Client->>FE: Open product listing page
    FE->>GW: GET /products (no auth required)
    GW->>PS: forward request
    PS->>DB: find all products
    DB-->>PS: products list
    PS-->>GW: 200 OK + products
    GW-->>FE: 200 OK + products
    loop for each product image
        FE->>GW: GET /media/images/{id}
        GW->>MS: forward request
        MS-->>GW: image bytes + cache headers
        GW-->>FE: image bytes
    end
    FE-->>Client: render product grid
```

### 1.5 Error Handling — Duplicate Email / Forbidden Action

```mermaid
sequenceDiagram
    actor User as Client/Seller
    participant FE as Angular
    participant GW as Gateway
    participant US as User Service
    participant PS as Product Service

    User->>FE: Register with existing email
    FE->>GW: POST /auth/register
    GW->>US: forward request
    US->>US: check email exists
    US-->>GW: 400 Bad Request (duplicate email)
    GW-->>FE: 400 Bad Request
    FE-->>User: inline form error

    User->>FE: Try to edit another seller's product
    FE->>GW: PUT /products/{id} (JWT)
    GW->>PS: forward request
    PS->>PS: ownership check fails
    PS-->>GW: 403/404
    GW-->>FE: 403/404
    FE-->>User: show forbidden/not-found toast
```
