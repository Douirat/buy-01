# Buy01 — TODO Checklist

### 2.1 Environment & Setup
- [ ] Clone/init repo structure: `gateway`, `discovery`, `user-service`, `product-service`, `media-service`, `frontend`
- [ ] Set up Eureka/Consul (or chosen) Discovery Server
- [ ] Set up API Gateway (Spring Cloud Gateway) with routing to all services
- [ ] Configure CORS at gateway
- [ ] Add `/actuator/health` to every service
- [ ] Docker Compose file for all services + MongoDB (+ Kafka if used)
- [ ] Write comprehensive README with run scripts (`docker-compose up`, seed data, env vars)

### 2.2 User Service
- [ ] `POST /auth/register` (role: CLIENT | SELLER)
- [ ] `POST /auth/login` → JWT/OAuth2 token
- [ ] `GET /me`, `PUT /me`
- [ ] Seller avatar upload delegated to Media Service
- [ ] BCrypt password hashing, never expose password in responses
- [ ] Duplicate-email validation → 400
- [ ] Global exception handler (no unhandled 5xx)

### 2.3 Product Service
- [ ] `GET /products` (public)
- [ ] `GET /products/{id}` (public)
- [ ] `POST /products` (seller-only)
- [ ] `PUT /products/{id}` (seller-only, ownership check)
- [ ] `DELETE /products/{id}` (seller-only, ownership check)
- [ ] Validate required fields, price > 0
- [ ] Associate `imageUrls[]` with product
- [ ] Global exception handler + correct status codes (400/401/403/404)

### 2.4 Media Service
- [ ] `POST /media/images` (seller-only)
- [ ] MIME type validation (`image/*`) via content sniffing, not just extension
- [ ] Enforce ≤ 2MB size limit
- [ ] `GET /media/images/{id}` with caching headers
- [ ] `DELETE /media/images/{id}` (ownership check) — optional
- [ ] Store binaries in object storage, metadata in MongoDB
- [ ] Reject non-image payloads → 400

### 2.5 Kafka Events (optional but recommended)
- [ ] Configure Kafka broker in Docker Compose
- [ ] Publish `PRODUCT_CREATED` from Product Service
- [ ] Publish `IMAGE_UPLOADED` from Media Service
- [ ] Add at least one consumer (audit log / cache invalidation) to justify usage

### 2.6 Security
- [ ] JWT or OAuth2 implemented with Spring Security
- [ ] JWT verified at gateway, identity propagated downstream
- [ ] Role-based access control: CLIENT vs SELLER (ADMIN optional)
- [ ] Ownership checks in Product & Media services
- [ ] HTTPS end-to-end (Let's Encrypt or self-signed for dev)
- [ ] Rate limiting at gateway for auth/media endpoints — optional

### 2.7 Angular Frontend
- [ ] Sign-in / Sign-up pages with role selection
- [ ] AuthGuard + RoleGuard on protected routes
- [ ] HTTP interceptor: attach JWT, handle 401/403
- [ ] Reactive Forms with inline validation errors
- [ ] Seller Dashboard: product CRUD + image attach/preview/remove
- [ ] Product Listing page (public grid/list)
- [ ] Media management view (upload, client-side type/size validation)
- [ ] Toast/snackbar for upload failures, oversized files, forbidden actions
- [ ] Responsive layout (Angular Material / Bootstrap)

### 2.8 Audit Preparation
- [ ] **Initial Setup & Access** — verify `docker-compose up` runs cleanly, all endpoints reachable via Postman
- [ ] **User & Product CRUD** — verify CRUD ops and correct role-based access per operation
- [ ] **Authentication & Role Validation** — sign up as client and seller, confirm role-restricted actions are blocked/allowed correctly
- [ ] **Media Upload & Association** — verify size/type constraints enforced, product-media linkage correct
- [ ] **Frontend Interaction** — walk through sign-in/up, seller dashboard, product listing, media page for UX issues
- [ ] **Security Review** — confirm hashed passwords, input validation, HTTPS, no sensitive data leaks (e.g. password never in JSON responses)
- [ ] **Backend Code Quality** — check Spring/MongoDB annotation correctness (`@Service`, `@RestController`, `@Document`, `@Valid`, etc.)
- [ ] **Frontend Code Quality** — check Angular structure: components/services/modules separation, no logic in templates
- [ ] **Error Handling & Edge Cases** — test duplicate email registration, invalid media type, oversized media, unauthorized product edits

### 2.9 Testing
- [ ] Unit tests: User Service (auth, password hashing, validation)
- [ ] Unit tests: Product Service (CRUD, ownership checks)
- [ ] Unit tests: Media Service (MIME validation, size limit)
- [ ] Integration tests: full register → login → create product → upload media → attach flow
- [ ] Negative tests: duplicate email, wrong role access, oversized file, invalid file type, editing others' products
- [ ] Postman collection covering all endpoints (public + protected) with sample requests
- [ ] Manual Angular UI walkthrough for both CLIENT and SELLER roles
- [ ] Verify all services report healthy on `/actuator/health`
- [ ] Verify no unhandled 5xx responses across all tested scenarios
