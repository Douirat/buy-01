# Buy01 — TODO Checklist (Split by Microservice: Douirat & Majnun)

Split by **service ownership** — whoever owns a service builds it end-to-end: API, business logic, and the Angular screens that consume it. But microservices always have a third category of work that belongs to *neither* service alone: the plumbing that lets independently-built services actually function as one system. That's called out separately below so it doesn't get silently dropped by either person assuming the other has it.

---

## 0. Microservices Integration & Coordination (do this together, early)

This is the work that doesn't belong to one service — it's the contract between them. Most "it works on my machine but not through the gateway" bugs trace back to skipping something here.

- [ ] **Agree on the JWT signing secret/key** and how it's distributed to every service's config (env var, not hardcoded) — needed before User Service and Media Service can be wired together
- [ ] **Agree on JWT claim shape**: field names for user ID, role, expiry — write it down so both sides implement it identically
- [ ] **Agree on a standard error response format** (status code + message + optional field errors) used by all three services, so the frontend interceptor only has to handle one shape
- [ ] **Agree on ID types/formats** passed between services (e.g. how `sellerId` on a Product and `id` on a User are represented — same type, same casing)
- [ ] Add Eureka client dependency + registration config to **all three** services (not just the ones Majnun owns)
- [ ] Confirm all three services appear correctly on the Eureka dashboard (`:8761`) before wiring the Gateway to them
- [ ] Define the Gateway's route table: which path prefix maps to which service (`/auth/**`, `/media/**`, `/products/**`)
- [ ] Agree on Docker Compose service names up front (`user-service`, `product-service`, `media-service`, `mongodb`, `kafka`) — every `application.yml` must reference these names, not `localhost`
- [ ] If using Kafka: agree on topic names and event payload schema (`PRODUCT_CREATED`, `IMAGE_UPLOADED`) before either producer/consumer is written
- [ ] Schedule at least one **joint integration session**: run all services + Gateway together via Compose and walk a full flow (register → login → create product → upload media → attach) end-to-end, not just via each service in isolation

---

## 1. Douirat — User Service + Media Service (full stack)

### User Service (backend)
- [ ] `POST /auth/register` (role: CLIENT | SELLER)
- [ ] `POST /auth/login` → JWT/OAuth2 token
- [ ] `GET /me`, `PUT /me`
- [ ] BCrypt password hashing, never expose password in responses
- [ ] Duplicate-email validation → 400
- [ ] Global exception handler (no unhandled 5xx)
- [ ] Role-based access control enforcement: CLIENT vs SELLER
- [ ] Eureka client registration (`spring-cloud-starter-netflix-eureka-client` + config)
- [ ] `application.yml` uses Docker service names, not `localhost` (MongoDB connection string)

### Media Service (backend)
- [ ] `POST /media/images` (seller-only)
- [ ] MIME type validation (`image/*`) via content sniffing, not just extension
- [ ] Enforce ≤ 2MB size limit
- [ ] `GET /media/images/{id}` with caching headers
- [ ] `DELETE /media/images/{id}` (ownership check) — optional
- [ ] Store binaries in object storage, metadata in MongoDB
- [ ] Reject non-image payloads → 400
- [ ] Ownership checks (mediaOwnerId == auth.subject) — read the user ID off the JWT claims, no call to User Service
- [ ] Publish `IMAGE_UPLOADED` event to Kafka (per agreed schema)
- [ ] Eureka client registration
- [ ] `application.yml` uses Docker service names, not `localhost`

### Angular Frontend (matching screens)
- [ ] Sign-in / Sign-up pages with role selection
- [ ] AuthGuard + RoleGuard on protected routes
- [ ] HTTP interceptor: attach JWT, handle 401/403
- [ ] Reactive Forms with inline validation errors (auth flows)
- [ ] Media management view (upload, client-side type/size validation, preview/remove)
- [ ] Seller avatar upload/update (delegates to Media Service)
- [ ] Toast/snackbar for upload failures, oversized files, auth errors
- [ ] Point frontend API calls at the **Gateway URL**, never directly at a service port

---

## 2. Majnun — Product Service + Gateway/Discovery (full stack)

### Product Service (backend)
- [ ] `GET /products` (public), `GET /products/{id}` (public)
- [ ] `POST /products` (seller-only)
- [ ] `PUT /products/{id}` (seller-only, ownership check)
- [ ] `DELETE /products/{id}` (seller-only, ownership check)
- [ ] Validate required fields, price > 0
- [ ] Associate `imageUrls[]` with product (store references only, never binary data)
- [ ] Global exception handler + correct status codes (400/401/403/404)
- [ ] Ownership checks (sellerId == auth.subject) — from JWT claims, no call to User Service
- [ ] Publish `PRODUCT_CREATED` event to Kafka (per agreed schema)
- [ ] Eureka client registration
- [ ] `application.yml` uses Docker service names, not `localhost`

### Gateway, Discovery & Infra (backend)
- [ ] Set up Eureka Discovery Server
- [ ] Set up API Gateway (Spring Cloud Gateway) with routes to all three services (per agreed route table)
- [ ] Verify JWT signature at gateway, propagate identity downstream (headers/claims)
- [ ] Configure CORS at gateway
- [ ] HTTPS end-to-end (Let's Encrypt or self-signed for dev)
- [ ] Rate limiting at gateway for auth/media endpoints — optional
- [ ] Add `/actuator/health` to every service and confirm Gateway can reach each one
- [ ] Docker Compose file for all services + MongoDB (+ Kafka if used), with a shared network and consistent service names
- [ ] Configure Kafka broker in Docker Compose; add at least one consumer (audit log / cache invalidation)
- [ ] Write comprehensive README with run scripts (`docker-compose up`, seed data, env vars)

### Angular Frontend (matching screens)
- [ ] Seller Dashboard: product CRUD + image attach/preview/remove (calls Product Service via Gateway)
- [ ] Product Listing page (public grid/list)
- [ ] Responsive layout (Angular Material / Bootstrap) across all pages
- [ ] Toast/snackbar for forbidden actions, product validation errors
- [ ] Point frontend API calls at the **Gateway URL**, never directly at a service port

---

## 3. Audit Preparation (by service ownership)

### Douirat
- [ ] **Authentication & Role Validation** — sign up as client and seller, confirm role-restricted actions blocked/allowed correctly
- [ ] **Media Upload & Association** — verify size/type constraints enforced, product-media linkage correct
- [ ] **Security Review (User/Media)** — hashed passwords, input validation, no sensitive data leaks

### Majnun
- [ ] **Initial Setup & Access** — verify `docker-compose up` runs cleanly, all endpoints reachable via Postman **through the Gateway**
- [ ] **User & Product CRUD** — verify CRUD ops and correct role-based access per operation
- [ ] **Frontend Interaction** — walk through sign-in/up, seller dashboard, product listing, media page for UX issues
- [ ] **Security Review (Gateway)** — HTTPS, CORS, JWT propagation

### Shared
- [ ] **Backend Code Quality** — check Spring/MongoDB annotation correctness across all three services
- [ ] **Frontend Code Quality** — check Angular structure: components/services/modules separation
- [ ] **Error Handling & Edge Cases** — test duplicate email, invalid media type, oversized media, unauthorized product edits
- [ ] **Service discovery sanity check** — kill and restart one service mid-demo, confirm the Gateway recovers routing to it without a manual restart of anything else

---

## 4. Testing (by service ownership)

### Douirat
- [ ] Unit tests: User Service (auth, password hashing, validation)
- [ ] Unit tests: Media Service (MIME validation, size limit)
- [ ] Negative tests: duplicate email, oversized file, invalid file type
- [ ] Manual walkthrough: sign-up/sign-in, media upload, avatar update

### Majnun
- [ ] Unit tests: Product Service (CRUD, ownership checks)
- [ ] Negative tests: wrong role access, editing others' products
- [ ] Manual walkthrough: seller dashboard, public product listing

### Shared
- [ ] **Integration test**: full register → login → create product → upload media → attach flow, run **through the Gateway**, not by hitting services directly
- [ ] Postman collection covering all endpoints (public + protected), pointed at the Gateway
- [ ] Verify all services report healthy on `/actuator/health`
- [ ] Verify no unhandled 5xx responses across all tested scenarios
- [ ] Contract check: confirm JWT claims produced by User Service are read correctly by Product Service and Media Service (this is the #1 place integration silently breaks)
