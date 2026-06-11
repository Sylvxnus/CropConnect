# CropConnect — Solution Maintainability

> This document evidences how CropConnect is structured for long-term maintainability, community ownership, and ease of future development. It covers architecture decisions, naming conventions, commenting standards, and how individual modules can be updated independently.

---

## Table of Contents

1. [Three-Layer Architecture](#1-three-layer-architecture)
2. [Naming Conventions](#2-naming-conventions)
3. [Inline Commenting Standards](#3-inline-commenting-standards)
4. [Single Responsibility Principle](#4-single-responsibility-principle)
5. [Defined Interfaces — The API Contract](#5-defined-interfaces--the-api-contract)
6. [Session Management](#6-session-management)
7. [How to Add a New Feature](#7-how-to-add-a-new-feature)
8. [How to Change an Existing Feature](#8-how-to-change-an-existing-feature)
9. [Database Schema](#9-database-schema)
10. [Known Technical Decisions](#10-known-technical-decisions)

---

## 1. Three-Layer Architecture

CropConnect separates concerns across three layers on the backend. Each layer has one job and communicates only with the layer directly adjacent to it.

```
Android App
    │
    │  HTTP / JSON  (Retrofit)
    ▼
Controller  ──►  Service  ──►  Repository  ──►  PostgreSQL
(HTTP only)     (logic only)   (DB only)
```

| Layer | Class example | Responsibility |
|---|---|---|
| Controller | `ProductController.java` | Receives HTTP requests, returns HTTP responses. No business logic. |
| Service | `ProductService.java` | All business logic — validation, null checks, data transformation. No HTTP, no SQL. |
| Repository | `ProductRepository.java` | Database access only. Spring Data JPA — no manual SQL needed for standard queries. |

**Why this matters for maintainability:** changing the database schema only requires touching the repository and model. Changing business rules only requires touching the service. The controller never changes unless the API contract changes.

---

## 2. Naming Conventions

### Backend (Java / Spring Boot)

| Element | Convention | Example |
|---|---|---|
| Classes | PascalCase | `ProductController`, `FoodBankService` |
| Methods | camelCase, verb-first | `getProductsByFoodBank()`, `deleteProduct()` |
| Variables | camelCase | `fbId`, `productQuant` |
| Constants | UPPER_SNAKE_CASE | `KEY_FB_ID`, `KEY_USER_TYPE` |
| REST endpoints | lowercase, hyphen-separated | `/api/products`, `/api/foodbanks/login` |
| Database columns | snake_case | `fb_id`, `product_quant`, `last_updated` |

### Android (Java)

| Element | Convention | Example |
|---|---|---|
| Activities | PascalCase + Activity suffix | `ViewStock`, `AlterStock`, `FoodBankLoginActivity` |
| Adapters | PascalCase + Adapter suffix | `StockAdapter`, `DonationAdapter` |
| Layout files | snake_case, screen-first | `foodbank_view_stock.xml`, `item_stock_row.xml` |
| View IDs | camelCase, type prefix | `tvProductName`, `etSearch`, `btnSave`, `recyclerStock` |
| Model fields | camelCase matching DB column | `fbId`, `productName`, `productQuant` |

### Serialisation alignment

Android model fields use `@SerializedName` to explicitly map to backend JSON keys, keeping Android and backend naming independent:

```java
// FoodBankProduct.java (Android)
@SerializedName("productId")
private Long productId;   // Android convention: camelCase

@SerializedName("fbId")
private long fbId;        // Maps to fb_id in PostgreSQL
```

This means the Android model, backend model, and database column can each follow their own conventions without breaking serialisation.

---

## 3. Inline Commenting Standards

Comments explain **why**, not **what**. The code itself shows what — the comment explains the decision that isn't obvious from reading it.

### Example 1 — explaining a type decision

```java
// SessionManager.java

// Integer (nullable) not int (primitive) — null = new record, omitted from JSON.
// Sending prodId=0 causes Hibernate to attempt a merge on id=0
// instead of an insert, throwing ObjectOptimisticLockingFailureException.
public void saveProducerSession(Integer prodId, String prodName, String prodEmail) {
    editor.putInt(KEY_PROD_ID, prodId != null ? prodId : -1);
```

### Example 2 — explaining an architectural boundary

```java
// ProductController.java

// ── DELETE /api/products/{id} ──────────────────────────────────────────
// Returns 404 if the product doesn't exist, 204 if successfully deleted.
// No body returned on success — standard REST convention for DELETE.
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    boolean deleted = productService.deleteProduct(id);
    if (!deleted) {
        return ResponseEntity.notFound().build(); // 404
    }
    return ResponseEntity.noContent().build(); // 204
}
```

### Example 3 — explaining cross-team compatibility

```java
// FoodBank.java (Android model)

// ── Aliases for FoodBankLoginActivity ─────────────────────────────────
// FoodBankLoginActivity calls setFoodEmail() and setFoodPassword().
// These alias methods preserve the teammate's existing code unchanged
// while the model internally stores values under fbEmail / fbPassword.
public void setFoodEmail(String email)   { this.fbEmail = email; }
public void setFoodPassword(String pwd)  { this.fbPassword = pwd; }
```

### Example 4 — explaining an API base URL

```java
// ApiClient.java

// 10.0.2.2 routes to the host machine's localhost from inside the Android emulator.
// Change this to the server IP for a physical device or production deployment.
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

---

## 4. Single Responsibility Principle

Every class in CropConnect has one reason to change.

### Backend example — inventory feature

```java
// ProductRepository.java — knows about the database, nothing else
@Repository
public interface ProductRepository extends JpaRepository<FoodBankProduct, Long> {
    List<FoodBankProduct> findByFbId(Long fbId);
    List<FoodBankProduct> findByFbIdAndCategory(Long fbId, String category);
}

// ProductService.java — knows about business logic, nothing else
public FoodBankProduct updateProduct(Long productId, FoodBankProduct updated) {
    Optional<FoodBankProduct> existing = productRepository.findById(productId);
    if (existing.isEmpty()) return null;         // business rule: 404 if not found
    FoodBankProduct product = existing.get();
    product.setProductName(updated.getProductName());
    product.setProductQuant(updated.getProductQuant());
    return productRepository.save(product);
}

// ProductController.java — knows about HTTP, nothing else
@PutMapping("/{id}")
public ResponseEntity<FoodBankProduct> updateProduct(@PathVariable Long id,
                                                      @RequestBody FoodBankProduct updated) {
    FoodBankProduct result = productService.updateProduct(id, updated);
    if (result == null) return ResponseEntity.notFound().build(); // HTTP concern
    return ResponseEntity.ok(result);
}
```

**Consequence:** if PostgreSQL is swapped for MongoDB, only `ProductRepository` changes. If the API response format changes, only `ProductController` changes. If business validation rules change, only `ProductService` changes.

### Android example — inventory feature

```
ViewStock.java      — orchestrates the screen, handles user input
StockAdapter.java   — knows how to display a product card, nothing else
ApiService.java     — knows the API endpoints, nothing else
ApiClient.java      — knows how to build a Retrofit instance, nothing else
SessionManager.java — knows how to persist session data, nothing else
```

---

## 5. Defined Interfaces — The API Contract

`ApiService.java` is the single contract between the Android app and the Spring Boot backend. Every endpoint is declared here. Adding a feature means adding one annotated method — Android and backend update independently.

```java
// ApiService.java — the complete API contract

public interface ApiService {

    // FoodBank auth
    @POST("api/foodbanks/login")
    Call<FoodBank> loginFoodBank(@Body FoodBank loginRequest);

    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

    @GET("api/foodbanks/search")
    Call<List<FoodBank>> searchFoodBanks(@Query("query") String query);

    // Producer auth
    @POST("api/producers")
    Call<Producer> registerProducer(@Body Producer producer);

    @POST("api/producers/login")
    Call<Producer> loginProducer(@Body Producer loginRequest);

    // Inventory — foodbank stock management
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

**To add a new endpoint:** add one `@GET`/`@POST`/`@PUT`/`@DELETE` method here, add the corresponding `@GetMapping`/`@PostMapping` in the relevant Spring Boot controller. Nothing else changes.

---

## 6. Session Management

`SessionManager.java` is the single source of truth for the logged-in user's identity across all activities. It provides both static and instance access patterns to support the different calling styles used across the team.

```java
// Static form — called by login activities
SessionManager.saveFoodBankSession(context, foodBank);
SessionManager.saveProducerSession(context, producer);
SessionManager.getProdName(context);
SessionManager.getCredits(context);

// Instance form — called by screens that need the current user's ID
long fbId = new SessionManager(this).getFbId();
```

**Why two forms?** Login activities use the static form because they construct the session object and immediately navigate away — no need to hold a reference. Screens like `ViewStock` use the instance form because they need to query multiple fields from the same session.

---

## 7. How to Add a New Feature

This section is a step-by-step guide for a new developer adding a feature — for example, adding a `GET /api/products/low-stock?fbId=1` endpoint that returns products below a threshold quantity.

### Step 1 — Add the repository method (backend)

```java
// ProductRepository.java
List<FoodBankProduct> findByFbIdAndProductQuantLessThan(Long fbId, Integer threshold);
```

Spring Data JPA generates the SQL automatically from the method name. No SQL needed.

### Step 2 — Add the service method (backend)

```java
// ProductService.java
public List<FoodBankProduct> getLowStockProducts(Long fbId, Integer threshold) {
    return productRepository.findByFbIdAndProductQuantLessThan(fbId, threshold);
}
```

### Step 3 — Add the controller endpoint (backend)

```java
// ProductController.java
@GetMapping("/low-stock")
public ResponseEntity<List<FoodBankProduct>> getLowStock(@RequestParam Long fbId,
                                                          @RequestParam Integer threshold) {
    return ResponseEntity.ok(productService.getLowStockProducts(fbId, threshold));
}
```

### Step 4 — Add the Retrofit declaration (Android)

```java
// ApiService.java
@GET("api/products/low-stock")
Call<List<FoodBankProduct>> getLowStockProducts(@Query("fbId") long fbId,
                                                 @Query("threshold") int threshold);
```

### Step 5 — Call it from the activity (Android)

```java
// ViewStock.java or wherever needed
apiService.getLowStockProducts(fbId, 10).enqueue(new Callback<List<FoodBankProduct>>() {
    @Override
    public void onResponse(Call<List<FoodBankProduct>> call,
                           Response<List<FoodBankProduct>> response) {
        if (response.isSuccessful() && response.body() != null) {
            // handle low stock list
        }
    }
    @Override
    public void onFailure(Call<List<FoodBankProduct>> call, Throwable t) {
        // handle error
    }
});
```

**Total files changed: 4.** No existing code modified — only additions.

---

## 8. How to Change an Existing Feature

### Example — changing the quantity field from Integer to Double

1. **Database:** `ALTER TABLE food_bank_products ALTER COLUMN product_quant TYPE numeric(10,2);`
2. **Backend model:** change `private Integer productQuant` → `private Double productQuant` in `FoodBankProduct.java`
3. **Android model:** change `private int productQuant` → `private double productQuant` in the Android `FoodBankProduct.java`
4. **AlterStock.java:** update `Integer.parseInt()` → `Double.parseDouble()` on the quantity field

Nothing else changes. The controller, service, repository, adapter, and layout are all unaffected.

---

## 9. Database Schema

```sql
-- Food banks
CREATE TABLE food_bank (
    fb_id            BIGINT PRIMARY KEY,
    fb_name          VARCHAR(255),
    fb_email         VARCHAR(255),
    fb_phone         VARCHAR(255),
    fb_long          DOUBLE PRECISION,
    fb_lat           DOUBLE PRECISION,
    fb_password      VARCHAR(255),
    no_referral      BOOLEAN,
    is_open          BOOLEAN,
    has_fresh_produce BOOLEAN
);

-- Producers (allotment holders)
CREATE TABLE crop_producers (
    prod_id       SERIAL PRIMARY KEY,
    prod_name     VARCHAR(255),
    prod_email    VARCHAR(255),
    prod_password VARCHAR(255),
    prod_postcode VARCHAR(255)
);

-- Foodbank inventory
CREATE TABLE food_bank_products (
    product_id        BIGSERIAL PRIMARY KEY,
    fb_id             BIGINT NOT NULL REFERENCES food_bank(fb_id),
    product_name      VARCHAR(255) NOT NULL,
    product_quant     INTEGER NOT NULL,
    upcoming_donation INTEGER DEFAULT 0,
    category          VARCHAR(255),
    unit              VARCHAR(255),
    expiry_date       DATE,
    last_updated      TIMESTAMP
);

-- Donations
CREATE TABLE donations (
    donation_id  SERIAL PRIMARY KEY,
    prod_id      INTEGER REFERENCES crop_producers(prod_id),
    fb_id        BIGINT REFERENCES food_bank(fb_id),
    items        VARCHAR(255),
    weight_kg    INTEGER,
    status       VARCHAR(50),
    created_at   TIMESTAMP DEFAULT NOW()
);

-- Credit transactions
CREATE TABLE credits_transactions (
    transaction_id   SERIAL PRIMARY KEY,
    prod_id          INTEGER REFERENCES crop_producers(prod_id),
    donation_id      INTEGER REFERENCES donations(donation_id),
    credit_val       INTEGER,
    transaction_type VARCHAR(255),
    created_at       DATE
);
```

---

## 10. Known Technical Decisions

These are decisions that future developers should understand before making changes.

| Decision | Reason |
|---|---|
| `Integer` not `int` for model IDs | Primitive `int` defaults to `0`, which Hibernate interprets as an existing entity ID, causing `ObjectOptimisticLockingFailureException` on POST. Nullable `Integer` is omitted from JSON by Gson, so Hibernate correctly generates a new ID. |
| BCrypt for password hashing | Spring Security's `BCryptPasswordEncoder` is used for both FoodBank and Producer passwords. Plaintext passwords in the DB will fail login — run the `/api/foodbanks/hashtest` endpoint (dev only) to generate a valid hash. |
| `10.0.2.2` as base URL | This is the Android emulator's alias for the host machine's `localhost`. Change to the server IP for physical device testing or production. |
| `@SerializedName` on all model fields | Gson uses these for serialisation. The field name in Java can differ from the JSON key — this keeps Android and backend conventions independent. |
| Static + instance `SessionManager` | Login activities use static methods (fire-and-forget). Screens use instance methods (need to hold state across multiple calls). Both work against the same `SharedPreferences` file. |
| `FLAG_ACTIVITY_CLEAR_TOP` on dashboard nav | Prevents the back stack from accumulating dashboard → stock → dashboard → stock. Tapping Dashboard from Stock returns to the existing dashboard instance rather than creating a new one. |


