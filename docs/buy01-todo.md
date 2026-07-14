# Buy01 — TODO Checklist (Split: Douirat & Majnun)

Split so each person owns a coherent slice of both backend and frontend, not just alternating checkboxes — each half is a set of services/screens one person can reason about end-to-end.

---

## Backend

### Douirat — User Service, Media Service, Security
- [ ] `POST /auth/register` (role: CLIENT | SELLER)
- [ ] `POST /auth/login` → JWT/OAuth2 token
- [ ] `GET /me`, `PUT /me`
- [ ] BCrypt password hashing, never expose password in responses
- [ ] Duplicate-email validation → 400
- [ ] `POST /media/images` (seller-only)
- [ ] MIME type validation (`image/*`) via content sniffing, not just extension
- [ ] Enforce ≤ 2MB size limit
- [ ] `GET /media/images/{id}` with caching headers
- [ ] `DELETE /media/images/{id}` (ownership check) — optional
- [ ] Store binaries in object storage, metadata in MongoDB
- [ ] Reject non-image payloads → 400
- [ ] JWT/OAuth2 implemented with Spring Security, verified at gateway
- [ ] Role-based access control: CLIENT vs SELLER (ADMIN optional)
- [ ] HTTPS end-to-end (Let's Encrypt or self-signed for dev)

### Majnun — Product Service, Kafka, Environment & Setup
- [ ] Clone/init repo structure: `gateway`, `discovery`, `user-service`, `product-service`, `media-service`, `frontend`
- [ ] Set up Eureka/Consul (or chosen) Discovery Server
- [ ] Set up API Gateway (Spring Cloud Gateway) with routing to all services
- [ ] Configure CORS at gateway
- [ ] Add `/actuator/health` to every service
- [ ] Docker Compose file for all services + MongoDB (+ Kafka if used)
- [ ] `GET /products` (public), `GET /products/{id}` (public)
- [ ] `POST /products` (seller-only)
- [ ] `PUT /products/{id}` (seller-only, ownership check)
- [ ] `DELETE /products/{id}` (seller-only, ownership check)
- [ ] Validate required fields, price > 0
- [ ] Associate `imageUrls[]` with product
- [ ] Global exception handlers across services (no unhandled 5xx)
- [ ] Configure Kafka broker in Docker Compose
- [ ] Publish `PRODUCT_CREATED` from Product Service, `IMAGE_UPLOADED` from Media Service
- [ ] Add at least one consumer (audit log / cache invalidation)
- [ ] Ownership checks in Product & Media services, rate limiting at gateway — optional

---

## Frontend (Angular)

### Douirat — Auth & Seller Dashboard
- [ ] Sign-in / Sign-up pages with role selection
- [ ] AuthGuard + RoleGuard on protected routes
- [ ] HTTP interceptor: attach JWT, handle 401/403
- [ ] Reactive Forms with inline validation errors (auth flows)
- [ ] Seller Dashboard: product CRUD + image attach/preview/remove
- [ ] Toast/snackbar for forbidden actions (auth/seller flows)

### Majnun — Product Listing & Media Management
- [ ] Product Listing page (public grid/list)
- [ ] Media management view (upload, client-side type/size validation)
- [ ] Toast/snackbar for upload failures, oversized files
- [ ] Responsive layout (Angular Material / Bootstrap) across pages
- [ ] README with run scripts (`docker-compose up`, seed data, env vars)

---

## Audit Preparation (split by ownership area)

### Douirat
- [ ] **Authentication & Role Validation** — sign up as client and seller, confirm role-restricted actions are blocked/allowed correctly
- [ ] **Security Review** — confirm hashed passwords, input validation, HTTPS, no sensitive data leaks
- [ ] **Media Upload & Association** — verify size/type constraints enforced, product-media linkage correct

### Majnun
- [ ] **Initial Setup & Access** — verify `docker-compose up` runs cleanly, all endpoints reachable via Postman
- [ ] **User & Product CRUD** — verify CRUD ops and correct role-based access per operation
- [ ] **Frontend Interaction** — walk through sign-in/up, seller dashboard, product listing, media page for UX issues

### Shared
- [ ] **Backend Code Quality** — check Spring/MongoDB annotation correctness
- [ ] **Frontend Code Quality** — check Angular structure: components/services/modules separation
- [ ] **Error Handling & Edge Cases** — test duplicate email, invalid media type, oversized media, unauthorized product edits

---

## Testing (split by ownership area)

### Douirat
- [ ] Unit tests: User Service (auth, password hashing, validation)
- [ ] Unit tests: Media Service (MIME validation, size limit)
- [ ] Negative tests: duplicate email, oversized file, invalid file type

### Majnun
- [ ] Unit tests: Product Service (CRUD, ownership checks)
- [ ] Integration tests: full register → login → create product → upload media → attach flow
- [ ] Negative tests: wrong role access, editing others' products

### Shared
- [ ] Postman collection covering all endpoints (public + protected)
- [ ] Manual Angular UI walkthrough for both CLIENT and SELLER roles
- [ ] Verify all services report healthy on `/actuator/health`
- [ ] Verify no unhandled 5xx responses across all tested scenarios
