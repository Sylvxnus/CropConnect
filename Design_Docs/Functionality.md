# Functionality

## Implementation of Design


## Usability


## Modularity
# CropConnect — Modularity

> CropConnect is built as a four-tier modular architecture. Each module has a single responsibility and communicates only through defined interfaces. Individual modules can be updated, replaced, or handed to new maintainers without affecting the rest of the system.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Android Frontend Modules](#2-android-frontend-modules)
3. [Spring Boot Backend Modules](#3-spring-boot-backend-modules)
4. [REST API — The Communication Boundary](#4-rest-api--the-communication-boundary)
5. [External Dependencies](#5-external-dependencies)
6. [Why This Supports Community Ownership](#6-why-this-supports-community-ownership)

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Android Frontend  (Java + XML)                          │
│                                                                             │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐               │
│  │  Grower Module  │ │ Recipient Module │ │Inventory Module │              │
│  │ ProducerLogin   │ │FoodBankLogin    │ │  ViewStock      │               │
│  │ ProducerRegister│ │FB_Dashboard     │ │  AlterStock     │               │
│  │ PROD_Dashboard  │ │FoodBankDonations│ │  StockAdapter   │               │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘               │
│                                                                             │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐               │
│  │Volunteer Module │ │  Guest / Maps   │ │  Credits System │               │
│  │ProcessDonations │ │Maps.java(OSMDrd)│ │ ProcessCredits  │               │
│  │ProcessCredits   │ │FoodBankDetails  │ │ CredsCalculator │               │
│  │WeatherAdvice    │ │AssistedAccess   │ │  Excel export   │               │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘               │
│                                                                             │
│  ┌──────────────────────┐  ┌────────────────┐  ┌──────────────────────┐    │
│  │  Seasonal Advisor    │  │   i18n Layer   │  │  Session / Network   │    │
│  │  WeatherAdvice       │  │ English / Urdu │  │  SessionManager      │    │
│  │  (Could Have) - - -  │  │values-ur/strs  │  │  ApiClient           │    │
│  └──────────────────────┘  └────────────────┘  │  ApiService          │    │
│                                                 └──────────────────────┘    │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
          ┌───────────────────▼────────────────────┐
          │           REST API Interface            │
          │   JSON over HTTP — sole communication   │
          │     boundary between frontend and       │
          │              backend                    │
          └───────────────────┬────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────────────┐
│           Spring Boot Backend  (Java 21 · Spring Boot 4.0.6 · :8080)       │
│                                                                             │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │
│  │ Auth Service │ │  Inventory   │ │  Donation    │ │  Credits     │      │
│  │FoodBankCtrl  │ │  Service     │ │  Service     │ │  Service     │      │
│  │ProducerCtrl  │ │ProductCtrl   │ │DonationCtrl  │ │CreditCtrl    │      │
│  │BCrypt+SecCfg │ │ProductSvc    │ │DonationSvc   │ │credits_trans │      │
│  │              │ │ProductRepo   │ │DonationRepo  │ │  table       │      │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘      │
│                                                                             │
│  ┌──────────────────────────────┐  ┌─────────────────────────────────┐     │
│  │      Distance Service        │  │       Seasonal Service          │     │
│  │    HaversineCalculator       │  │   Weather API integration       │     │
│  │  producer → foodbank (km)    │  │        (Could Have) - - -       │     │
│  └──────────────────────────────┘  └─────────────────────────────────┘     │
└─────────────────────────────┬───────────────────────────────────────────────┘
                              │
          ┌───────────────────▼────────────────────────────────────────────┐
          │                   PostgreSQL Database                           │
          │          cmpstudb-01.cmp.uea.ac.uk:5432                        │
          │  food_bank · food_bank_products · crop_producers               │
          │  donations · credits_transactions                               │
          └───────────────────┬────────────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────────────┐
│                        External Dependencies                                │
│                                                                             │
│  ┌────────────┐  ┌────────────┐  ┌─────────────┐  ┌──────────────────────┐ │
│  │OSMDroid    │  │ QR Service │  │ Postcode API│  │  Retrofit + Gson     │ │
│  │Map tiles + │  │  Credits   │  │Birmingham   │  │  HTTP client library │ │
│  │  markers   │  │ redemption │  │ validation  │  │                      │ │
│  └────────────┘  └────────────┘  └─────────────┘  └──────────────────────┘ │
│                                                                             │
│  ┌──────────────────────────────┐                                           │
│  │      OpenWeatherMap          │                                           │
│  │  Could Have — seasonal adv.  │                                           │
│  └──────────────────────────────┘                                           │
└─────────────────────────────────────────────────────────────────────────────┘

Legend:
  ━━━  Solid border  = Team scope (built)
  - -  Dashed border = Could Have (not yet built)
  ███  Dark bar      = REST API — sole communication boundary
```

---

## 2. Android Frontend Modules

| Module | Key files | Responsibility | Status |
|---|---|---|---|
| **Grower Module** | `ProducerLoginActivity` `ProducerRegisterActivity` `PROD_Dashboard` | Allotment holder auth, registration, and personal dashboard | ✅ Built |
| **Recipient Module** | `FoodBankLoginActivity` `FB_Dashboard` `FoodBankDonationsActivity` | Food bank staff auth and donations dashboard | ✅ Built |
| **Inventory Module** | `ViewStock` `AlterStock` `StockAdapter` | Food bank stock management — full CRUD against live DB | ✅ Built |
| **Volunteer Module** | `ProcessDonations` `ProcessCredits` `WeatherAdvice` | Donation processing, credit redemption, weather advice | ✅ Built |
| **Guest / Maps** | `Maps.java` `FoodBankDetailsEngine` `Assisted Access Mode` | OSMDroid map with food bank pins, filters, no-login guest access | ✅ Built |
| **Credits System** | `ProcessCredits` `CredsCalculator` `Excel export` | Producer credit tracking and bank-statement-style export |✅ Built|
| **Seasonal Advisor** | `WeatherAdvice` | Crop suggestions based on weather API (Could Have) | 🔄 Could Have |
| **i18n Layer** | `values-ur/strings.xml` | English / Urdu language toggle across all screens | ✅ Built |
| **Session / Network** | `SessionManager` `ApiClient` `ApiService` | Shared infrastructure — session persistence and HTTP client | ✅ Built |

### Module isolation

Each module is self-contained. `ViewStock` knows nothing about authentication. `Maps.java` knows nothing about credits. They communicate only through `SessionManager` (session state) and the REST API (data). Replacing any single module leaves all others unaffected.

---

## 3. Spring Boot Backend Modules

Every backend module follows the same three-layer pattern:

```
Controller  →  Service  →  Repository  →  PostgreSQL
(HTTP only)    (logic)      (DB only)
```

| Module | Files | Endpoints | Status |
|---|---|---|---|
| **Auth Service** | `FoodBankController` `ProducerController` `BCrypt + SecurityConfig` | `POST /api/foodbanks/login` `POST /api/producers` `POST /api/producers/login` | ✅ Built |
| **Inventory Service** | `ProductController` `ProductService` `ProductRepository` | `GET/POST/PUT/DELETE /api/products` | ✅ Built |
| **Donation Service** | `DonationController` `DonationService` `DonationRepository` | `POST /api/donations` `PUT /api/donations/{id}` | ✅ Built |
| **Credits Service** | `CreditController` `credits_transactions` table | `GET /api/credits?prodId=` | ✅ Built |
| **Distance Service** | `HaversineCalculator` | Utility — producer → food bank distance in km | ✅ Built |
| **Seasonal Service** | Weather API integration | Crop suggestions (Could Have) | 🔄 Could Have |

### Adding a new backend module — 5 steps, no existing files change

```
1. model/NewEntity.java        — JPA entity, maps to a DB table
2. repository/NewRepository.java — extends JpaRepository, Spring generates SQL
3. service/NewService.java     — business logic only, no HTTP
4. controller/NewController.java — HTTP only, delegates to service
5. ApiService.java (Android)   — add one @GET/@POST method
```

---

## 4. REST API — The Communication Boundary

`ApiService.java` is the single contract between Android and Spring Boot. Every endpoint is declared here. Neither side knows how the other is implemented — only what this interface says.

```java
// ApiService.java — complete API contract (Retrofit interface)

public interface ApiService {

    // ── FoodBank ───────────────────────────────────────────────────────
    @POST("api/foodbanks/login")
    Call<FoodBank> loginFoodBank(@Body FoodBank loginRequest);

    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

    @GET("api/foodbanks/search")
    Call<List<FoodBank>> searchFoodBanks(@Query("query") String query);

    // ── Producer ───────────────────────────────────────────────────────
    @POST("api/producers")
    Call<Producer> registerProducer(@Body Producer producer);

    @POST("api/producers/login")
    Call<Producer> loginProducer(@Body Producer loginRequest);

    // ── Inventory ──────────────────────────────────────────────────────
    @GET("api/products")
    Call<List<FoodBankProduct>> getProducts(@Query("fbId") long fbId);

    @POST("api/products")
    Call<FoodBankProduct> createProduct(@Body FoodBankProduct product);

    @PUT("api/products/{id}")
    Call<FoodBankProduct> updateProduct(@Path("id") long productId,
                                        @Body FoodBankProduct product);

    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") long productId);
}
```

**Adding a new endpoint = one new annotated method here + one new `@Mapping` in the Spring Boot controller. Nothing else changes.**

---

## 5. External Dependencies

All external services are isolated behind their own call sites and never referenced from business logic directly.

| Dependency | Used by | Purpose | Swap point |
|---|---|---|---|
| **OSMDroid Maps** | `Maps.java` only | Map tiles and markers | `Maps.java` — change here only |
| **Retrofit + Gson** | `ApiClient.java` + `ApiService.java` | HTTP client and JSON parsing | `ApiClient.java` — change base URL or client config here only |
| **MPAndroidChart** | `FB_Dashboard` `PROD_Dashboard` | Pie and bar charts | Dashboard activities only |
| **OpenWeatherMap** | `WeatherAdvice.java` (Could Have) | Seasonal crop suggestions | `WeatherAdvice.java` only |
| **QR Service** | `ProcessCredits.java` (planned) | Credit redemption QR codes | Credits module only |
| **Postcode API** | Registration flow | Birmingham-area postcode validation | Registration activity only |
| **Spring Security + BCrypt** | `SecurityConfig` `FoodBankService` | Password hashing and request filtering | Security layer only — no business logic touches it |

---

## 6. Why This Supports Community Ownership

CropConnect is designed for the EWB brief's requirement of **community ownership** — Ladywood residents and local organisations should be able to maintain and extend the platform themselves.

**Single-responsibility modules** mean a community developer maintaining the food bank map doesn't need to understand how the credit system works. They own `Maps.java` and its layout — nothing else.

**The REST API as sole boundary** means Android and backend can evolve independently. A community organisation could replace the Spring Boot backend with any other implementation and the Android app would work unchanged, as long as the API contract is honoured.

**Isolated external dependencies** mean if OSMDroid stops being maintained or OpenWeatherMap changes its pricing, the swap is a single-file change with no ripple effects across the codebase.

**Dashed-border Could Have modules** (Seasonal Advisor, QR Service, OpenWeatherMap) are explicitly scoped as optional. They can be added post-prototype by any developer without touching any existing module.


## Well Defined Interfaces

### User Interfaces

The system provides dedicated interfaces for producers, food banks and guests, ensuring that each user can access the functionality relevant to their role.

- **Producers** can register, log in and submit surplus produce donations to food banks.
- **Food Banks** can manage their stock and process incoming donations.
- **Guests** can access the food bank map and locate nearby services without requiring an account.

All interfaces are designed to provide a consistent user experience, allowing users to navigate between features efficiently. The application also includes multilingual support and assisted access features to improve accessibility for users with varying levels of digital literacy.

---

### System Interfaces

The system utilises several interfaces to support communication between different components. The Android mobile application acts as the primary interface through which users interact with the platform. User requests, such as account management, produce listings and collection requests, are processed through the Spring Boot application layer before being stored within the PostgreSQL database.

The application also integrates with external services where required. OpenStreetMap is used to provide location-based functionality, allowing users to view nearby food banks and collection points directly within the application. These interfaces ensure that data can be exchanged efficiently between system components whilst maintaining a clear separation between the user interface, application logic and data storage layers.

---

### Interface Design Considerations

#### Accessibility and Usability

The interfaces were designed with accessibility and usability in mind to ensure that the application can be used by a wide range of users. Multilingual support has been incorporated to provide both English and Urdu interfaces, improving accessibility for users within the local community.

#### Assisted Access

An Assisted Access mode is included to support users with lower levels of digital literacy through:

- Simplified navigation
- Larger interface elements
- Reduced interface complexity

#### Consistency

A consistent interface design has been maintained throughout the application to improve usability and reduce the learning curve for new users. These considerations help ensure that users can access key functionality efficiently regardless of their technical experience.
