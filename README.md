# Buy01 — Building a Microservices Marketplace

> A Zone01 project where you design, build, and secure a real e-commerce platform — not a tutorial clone, but a system with independent services that have to talk to each other, fail gracefully, and hold up under an audit.

## What This Project Actually Puts You Through

Buy01 isn't "build a CRUD app." It's an exercise in **architecture decisions under constraints**. You're handed a marketplace with two kinds of users — clients who browse, sellers who manage inventory — and told to split the backend into independently deployable services instead of one monolith. That single decision is what shapes the entire experience:

- You can no longer just call a method and get an answer back — every cross-service interaction is now a network call that can fail, time out, or return partial data.
- Authentication isn't "check a session" — it's a JWT minted by one service, verified at a gateway, and trusted by two other services that never see the password.
- A file upload isn't "save to disk" — it's a validated, size-capped, MIME-sniffed binary that has to end up correctly linked to a product owned by a specific seller.

The project forces you to feel the difference between *building a feature* and *building a boundary*.

## The Journey, Phase by Phase

### 1. Laying the foundation
Before any business logic, you stand up **Discovery** and a **Gateway**. This is the part that feels like plumbing but isn't optional — every service you write afterward depends on being findable and reachable through one entry point. Getting CORS, routing, and `/actuator/health` right here saves you from chasing phantom bugs later.

### 2. Splitting the domain
Three services, three responsibilities:
- **User Service** owns identity — registration, login, roles, hashed passwords.
- **Product Service** owns the catalog — but knows nothing about how images are stored.
- **Media Service** owns files — but knows nothing about products.

Deciding *what belongs where* is the real design work. It's tempting to let Product Service just store image bytes directly — the project pushes back on that instinct on purpose.

### 3. Wiring authentication across boundaries
This is where the project gets genuinely hard. A JWT issued by User Service has to be verified at the Gateway and *trusted* downstream by Product and Media Service without either of them re-validating credentials. Getting role checks (`CLIENT` vs `SELLER`) and ownership checks (`sellerId == auth.subject`) right, consistently, across three codebases, is the core security lesson of the whole project.

### 4. Making uploads safe
Media handling looks simple until you implement it properly: client-side validation isn't enough, so you sniff content server-side, enforce a hard 2MB ceiling, and reject anything that isn't actually an image regardless of what its filename claims. Then you have to correctly link that stored media back to a product without leaking access to sellers who don't own it.

### 5. Closing the loop with events (optional but worth it)
Adding Kafka events like `PRODUCT_CREATED` and `IMAGE_UPLOADED` is where the project stops being "services that call each other" and starts being "services that react to each other." It's optional, but it's the difference between a system that merely works and one that's actually decoupled.

### 6. Building the Angular frontend
Guards that block routes before a request even fires, interceptors that silently attach tokens and catch 401s, reactive forms that validate before hitting the network — the frontend phase is where you realize how much of "good UX" is really just "handling failure well before the user notices."

### 7. Surviving the audit
The last phase isn't code — it's proving the system holds up. Someone else registers as a client and a seller, tries to edit a product they don't own, uploads a 5MB file, signs up with an email that already exists. Every one of those has to fail *cleanly*, with the right status code and a message a human can understand, not a stack trace.

## What You Walk Away With

- A working mental model of **service boundaries** and why they're drawn where they are, not just how to draw them
- Practical experience propagating identity across a distributed system instead of trusting a single session
- The habit of validating everything twice — once for the user's sake (frontend), once because you can't trust the frontend (backend)
- A concrete answer to "why microservices" that isn't just buzzwords — you'll have felt the coordination cost firsthand

## Tech Stack

| Layer | Tools |
|---|---|
| Backend | Spring Boot, Spring Security, Spring Cloud Gateway |
| Data | MongoDB, object storage for media |
| Messaging | Kafka (optional) |
| Frontend | Angular, Reactive Forms, Angular Material/Bootstrap |
| Auth | JWT / OAuth2, BCrypt |
| Ops | Docker Compose, Actuator health checks |

## Related Docs in This Repo

- [`buy01-sequence-diagrams.md`](./buy01-sequence-diagrams.md) — end-to-end flows for auth, product CRUD, media upload, and error handling
- [`buy01-todo.md`](./buy01-todo.md) — full task breakdown covering implementation, audit prep, and testing
