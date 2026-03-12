# FastDel — Project Rules & Architecture Guide

## 1. Project Overview

**FastDel** is a courier route optimization application. It consists of three components sharing a single backend:
- **Web App (Admin Panel)** — address & barcode management
- **Mobile App (Courier App)** — barcode scanning, route creation & delivery tracking
- **Backend API** — single Spring Boot service serving both clients

The core flow:
1. Admin enters recipient address info on the web → system generates QR/barcode
2. Courier scans barcodes via mobile app → selects packages → taps "Create Route"
3. Backend calculates the optimal route using real-time Google Maps Traffic API
4. Courier follows the route; marks each delivery as delivered or failed
5. Failed deliveries are automatically prioritized in the next session; failed twice → returned

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.0.3, Maven |
| Database | PostgreSQL (via DBeaver) |
| Web Frontend | Angular CLI, TypeScript, HTML, CSS |
| Mobile Frontend | Angular + Ionic |
| Maps & Traffic | Google Maps API (Directions API, Routes API) |
| Auth | JWT (JSON Web Token) |
| Containerization | Docker / Docker Compose |
| Monorepo Structure | Single repo, multiple modules |

---

## 3. Repository Structure (Monorepo)

```
fastdel/
├── backend/                  # Spring Boot (Maven)
│   └── src/
│       └── main/java/com/fastdel/
│           ├── config/       # Security, JWT, Google API config
│           ├── controller/   # REST Controllers
│           ├── service/      # Business logic
│           ├── repository/   # JPA Repositories
│           ├── model/        # Entities
│           ├── dto/          # Request/Response DTOs
│           └── exception/    # Global exception handling
├── web/                      # Angular Admin Panel
├── mobile/                   # Ionic + Angular Courier App
├── docker-compose.yml        # Orchestrates all services
└── rules.md                  # This file
```

---

## 4. Architecture Rules

### 4.1 Backend — Layered Architecture

Strict **Controller → Service → Repository** layering:

- **Controller**: Only handles HTTP requests/responses. No business logic.
- **Service**: All business logic lives here. Calls repositories and external APIs.
- **Repository**: Only JPA/database operations. No logic.
- **DTO**: Always use DTOs for API input/output. Never expose Entity objects directly.
- **Model/Entity**: JPA entities only. No service/controller logic.

```
HTTP Request
    ↓
Controller (validates input, delegates)
    ↓
Service (business logic, orchestration)
    ↓
Repository (database access via JPA)
    ↓
PostgreSQL
```

### 4.2 Modularity Rule — External APIs

All external service integrations (Google Maps, future alternatives) must be abstracted behind an **interface**. This allows swapping providers without touching business logic.

```java
// Always code to the interface:
public interface MapService {
    RouteResult calculateRoute(List<Location> stops);
    TrafficData getTrafficData(Location origin, Location destination);
}

// Current implementation:
public class GoogleMapService implements MapService { ... }

// Future swap (e.g., HERE Maps) only requires:
public class HereMapService implements MapService { ... }
// + one config change — nothing else changes
```

Apply the same pattern for: barcode generation, notification services, any future integrations.

### 4.3 Authentication & Authorization

- **JWT-based auth** for all roles
- **No self-registration** — zero public sign-up endpoints exist anywhere
- Only the **Admin** can create accounts (courier & staff) from the web panel
- Role-based access control (RBAC):

| Role | Platform | Permissions |
|---|---|---|
| `ADMIN` | Web panel | Full access — user management (create/delete courier & staff accounts), view all packages & deliveries |
| `STAFF` | Web panel | Package management — create, edit, delete packages & their barcodes (one address = one package) |
| `COURIER` | Mobile app only | Scan barcodes, create route, update delivery status |

- JWT token must include role claim
- Backend enforces role checks at controller level via Spring Security
- Staff logs into web panel with admin-assigned credentials
- Courier logs into mobile app with admin-assigned credentials

---

## 5. Database Rules (PostgreSQL)

- All entities use **UUID** as primary key (not auto-increment integer)
- All tables use **snake_case** naming
- Timestamps: every table has `created_at` and `updated_at`
- Soft deletes preferred over hard deletes (add `deleted_at` column)
- Migrations managed manually via SQL scripts (or Flyway if added later)

### Core Entities (initial)

```
users           — id, name, email, password_hash, role, created_at
packages        — id, recipient_name, recipient_address, lat, lng, barcode, qr_code, status, fail_count, created_at
deliveries      — id, courier_id, date, status, created_at
delivery_items  — id, delivery_id, package_id, order_index, status, updated_at
routes          — id, delivery_id, waypoints_json, created_at
```

### Package Status Flow (5-stage)

```
PENDING → IN_TRANSIT → OUT_FOR_DELIVERY → DELIVERED
                                        ↘ FAILED (fail_count + 1)
                                              ↓ (if fail_count >= 2)
                                           RETURNED
```

- `fail_count = 1` → reprioritized in next delivery session
- `fail_count >= 2` → status set to `RETURNED`, no further delivery attempts

---

## 6. Barcode & QR Code Rules

- **Both** QR Code and 1D Barcode (Code128/EAN) are supported
- Barcodes are **fully generated and stored server-side** (backend) — no client-side generation
- Each package entry (one address = one barcode) generates both a QR code and a 1D barcode
- Barcode value = package UUID (or a derived unique string)
- Generated barcode images are stored/served by the backend (as file or base64)
- Mobile app uses device camera to scan either format
- Scanning resolves to a package record via backend API

### Barcode Creation Flow
```
Staff logs in (web panel)
    ↓
Enters recipient address info (one form = one package)
    ↓
Submits → Backend generates QR + 1D barcode, saves to DB
    ↓
Staff sees barcode on screen → can print / download
```

---

## 7. Route Optimization Rules

- Courier manually selects packages to include in their session (by scanning)
- After scanning all packages, courier taps **"Create Route"**
- Backend calls **Google Maps Directions API** with traffic optimization
- Waypoints are ordered by Google's traffic-aware optimization
- Route result is stored in `routes` table as JSON waypoints
- If a package cannot be delivered:
  - Courier marks it as **FAILED** in the app
  - `fail_count` increments by 1
  - On next session: these packages appear at the top of the scan list (prioritized)
  - At `fail_count >= 2`: package auto-transitions to **RETURNED**

---

## 8. Google Maps API Integration

Use the following Google APIs:
- **Directions API** — route calculation with `optimize:true` for waypoints
- **Routes API** (preferred, newer) — traffic-aware routing
- **Maps JavaScript API** — web panel map display
- **Maps SDK for Android/iOS** (via Ionic Capacitor) — mobile map display

### Config Rule
All API keys and endpoints go in environment-specific config files only:
- Backend: `application.properties` / `application-docker.properties`
- Web: `environment.ts` / `environment.prod.ts`
- Mobile: `environment.ts` / `environment.prod.ts`
- **Never hardcode API keys in source code**

---

## 9. Docker Setup Rules

```yaml
# docker-compose.yml services:
services:
  postgres:     # PostgreSQL database
  backend:      # Spring Boot API (port 8080)
  web:          # Angular admin panel (port 4200 or 80)
  mobile:       # Ionic dev server (port 8100) — dev only
```

- All services communicate via Docker internal network
- Environment variables injected via `.env` file (not committed to repo)
- `.env.example` committed to repo as template

---

## 10. API Design Rules

- All endpoints prefixed with `/api/v1/`
- RESTful conventions strictly followed
- JSON request/response only
- Standard error response format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Package not found",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

- Auth endpoints: `/api/v1/auth/login`
- Admin endpoints: `/api/v1/admin/**` — ADMIN role only
- Package endpoints: `/api/v1/packages/**`
- Route endpoints: `/api/v1/routes/**`
- Delivery endpoints: `/api/v1/deliveries/**`

---

## 11. Extensibility Notes (Future-Proofing)

The following features are **not in scope now** but the architecture must not block them:

- **Customer tracking portal** — package status lookup by tracking number
- **Multi-courier support** — route splitting across couriers
- **Push notifications** — delivery status updates
- **Offline mode** — mobile app caching when no internet
- **Alternative map provider** — swap Google Maps via `MapService` interface

---

## 12. What NOT to Do

- ❌ No public registration endpoints
- ❌ No business logic in Controllers
- ❌ No Entity objects returned directly from API (always use DTOs)
- ❌ No hardcoded API keys or secrets
- ❌ No direct Google Maps calls from frontend — always go through backend
- ❌ No integer primary keys — use UUID
- ❌ Do not break the `MapService` abstraction layer

---

## 13. Initial Spring Boot Dependencies (pom.xml)

```xml
<!-- Start with these, add more as needed -->
<dependencies>
  <dependency>spring-boot-starter-web</dependency>
  <dependency>spring-boot-starter-data-jpa</dependency>
  <dependency>postgresql</dependency>
  <!-- To be added: spring-boot-starter-security, jjwt, zxing (barcode) -->
</dependencies>
```

---

*This document is the single source of truth for FastDel's architecture decisions. All AI-assisted development (Antigravity or otherwise) must follow these rules.*
