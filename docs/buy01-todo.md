# Buy01 — TODO Checklist (Split by Microservice: Douirat & Majnun)

Split by **service ownership**, not by layer — whoever owns a service builds it end-to-end: API, business logic, and the Angular screens that consume it. This matches the microservices nature of the project: each person can develop, run, and demo their slice independently.

---

## Douirat — User Service + Media Service (full stack)

### User Service (backend)
- [ ] `POST /auth/register` (role: CLIENT | SELLER)
- [ ] `POST /auth/login` → JWT/OAuth2 token
- [ ] `GET /me`, `PUT /me`
- [ ] BCrypt password hashing, never expose password in responses
- [ ] Duplicate-email validation → 400
- [ ] Global exception handler (no unhandled 5xx)
- [ ] Role-based access control enforcement: CLIENT vs SELLER

### Media Service (backend)
- [ ] `POST /media/images` (seller-only)
- [ ] MIME type validation (`image/*`) via content sniffing, not just extension
- [ ] Enforce ≤ 2MB size limit
- [ ] `GET /media/images/{id}` with caching headers
- [ ] `DELETE /media/images/{id}` (ownership check) — optional
- [ ] Store binaries in object storage, metadata in MongoDB
- [ ] Reject non-image payloads → 400
- [ ] Ownership checks (mediaOwnerId == auth.subject)
- [ ] Publish `IMAGE_UPLOADED` event to Kafka

### Angular Frontend (matching screens)
- [ ] Sign-in / Sign-up pages with role selection
- [ ] AuthGuard + RoleGuard on protected routes
- [ ] HTTP interceptor: attach JWT, handle 401/403
- [ ] Reactive Forms with inline validation errors (auth flows)
- [ ] Media management view (upload, client-side type/size validation, preview/remove)
- [ ] Seller avatar upload/update (delegates to Media Service)
- [ ] Toast/snackbar for upload failures, oversized files, auth errors

---

## Majnun — Product Service + Gateway/Discovery (full stack)

### Product Service (backend)
- [ ] `GET /products` (public), `GET /products/{id}` (public)
- [ ] `POST /products` (seller-only)
- [ ] `PUT /products/{id}` (seller-only, ownership check)
- [ ] `DELETE /products/{id}` (seller-only, ownership check)
- [ ] Validate required fields, price > 0
- [ ] Associate `imageUrls[]` with product (linking to Media Service output)
- [ ] Global exception handler + correct status codes (400/401/403/404)
- [ ] Ownership checks (sellerId == auth.subject)
- [ ] Publish `PRODUCT_CREATED` event to Kafka

### Gateway, Discovery & Infra (backend)
- [ ] Set up Eureka/Consul (or chosen) Discovery Server
- [ ] Set up API Gateway (Spring Cloud Gateway) with routing to all services
- [ ] Verify JWT at gateway, propagate identity downstream
- [ ] Configure CORS at gateway
- [ ] HTTPS end-to-end (Let's Encrypt or self-signed for dev)
- [ ] Rate limiting at gateway for auth/media endpoints — optional
- [ ] Add `/actuator/health` to every service
- [ ] Docker Compose file for all services + MongoDB (+ Kafka if used)
- [ ] Configure Kafka broker in Docker Compose; add at least one consumer (audit log / cache invalidation)
- [ ] Write comprehensive README with run scripts (`docker-compose up`, seed data, env vars)

### Angular Frontend (matching screens)
- [ ] Seller Dashboard: product CRUD + image attach/preview/remove (calls Product Service)
- [ ] Product Listing page (public grid/list)
- [ ] Responsive layout (Angular Material / Bootstrap) across all pages
- [ ] Toast/snackbar for forbidden actions, product validation errors

---

## Audit Preparation (by service ownership)

### Douirat
- [ ] **Authentication & Role Validation** — sign up as client and seller, confirm role-restricted actions blocked/allowed correctly
- [ ] **Media Upload & Association** — verify size/type constraints enforced, product-media linkage correct
- [ ] **Security Review (User/Media)** — hashed passwords, input validation, no sensitive data leaks

### Majnun
- [ ] **Initial Setup & Access** — verify `docker-compose up` runs cleanly, all endpoints reachable via Postman
- [ ] **User & Product CRUD** — verify CRUD ops and correct role-based access per operation
- [ ] **Frontend Interaction** — walk through sign-in/up, seller dashboard, product listing, media page for UX issues
- [ ] **Security Review (Gateway)** — HTTPS, CORS, JWT propagation

### Shared
- [ ] **Backend Code Quality** — check Spring/MongoDB annotation correctness across all three services
- [ ] **Frontend Code Quality** — check Angular structure: components/services/modules separation
- [ ] **Error Handling & Edge Cases** — test duplicate email, invalid media type, oversized media, unauthorized product edits

---

## Testing (by service ownership)

### Douirat
- [ ] Unit tests: User Service (auth, password hashing, validation)
- [ ] Unit tests: Media Service (MIME validation, size limit)
- [ ] Negative tests: duplicate email, oversized file, invalid file type
- [ ] Manual walkthrough: sign-up/sign-in, media upload, avatar update

### Majnun
- [ ] Unit tests: Product Service (CRUD, ownership checks)
- [ ] Integration tests: full register → login → create product → upload media → attach flow
- [ ] Negative tests: wrong role access, editing others' products
- [ ] Manual walkthrough: seller dashboard, public product listing

### Shared
- [ ] Postman collection covering all endpoints (public + protected)
- [ ] Verify all services report healthy on `/actuator/health`
- [ ] Verify no unhandled 5xx responses across all tested scenarios
