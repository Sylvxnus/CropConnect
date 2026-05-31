# Security Considerations for CropConnect System

This outlines the security considerations for our CropConnect system, covering risks,
likelihood and impact, and the mitigations we will put in place across the full stack
from database to our Android device.

---

## 1. Password Storage

**Risk:** If our PostgreSQL database on the UEA network is breached and passwords are
stored in plaintext, every producer and food bank account would be immediately
compromised and exploited.

**Likelihood:** Medium

**Impact:** Critical

**Mitigation:** All passwords will be hashed with either bcrypt or Argon2 (Aidan to
decide) before we store them. The plain text password is never stored, logged or
transmitted after the initial hash. Verification compares a new hash of the input from
the user with the one stored in the database.

---

## 2. SQL Injection

**Risk:** A malicious user enters SQL code into login fields that attempt to manipulate
or destroy the PostgreSQL database. One of the most common but damaging attack on
database-backed applications.

**Likelihood:** High without mitigation

**Impact:** Critical

**Mitigation:** All PostgreSQL queries in the C++ backend use prepared statements via
libpq. User input is never concatenated directly into the SQL strings under any
circumstances.

---

## 3. JWT Authentication Tokens

**Risk:** Without session tokens, any API requests could be made by anyone, meaning one
producer could log donations or redeem credits on behalf of another.

**Likelihood:** High without mitigation

**Impact:** High

**Mitigation:** On login the C++ backend will generate a JWT token returned to the
Android app. Every API request post login will then contain this JWT token in its Auth
header. The backend of the program will then validate the token before processing any
request. (We need to evaluate how long to have the tokens valid for)

---

## 4. QR Code Fraud

**Risk:** A producer screenshots a QR code and shares it with others allowing multiple
redemptions of the same credits.

**Likelihood:** Medium

**Impact:** High

**Mitigation:** Each QR code is only to be used once. (We need to discuss how best to
do this, as when the QR code is to be scanned, there needs to be something to reference
it against, my thought is that we should implement an admin panel for allotment
coordinators, so that they can either scan the QR code or enter an alphanumeric code)

---

## 5. Input Validation on Backend

**Likelihood:** Medium

**Impact:** High

**Mitigation:** All input validated on the C++ backend before any database operation,
for example:

Validated Fields Examples:
- donation_amount must be larger than 0
- fb_id must exist
- prod_id must exist

---

## 6. Rate Limiting

**Risk:** Someone floods the API with thousands of donation requests to farm credits
artificially or causes a denial of service (DoS) on the UEA server.

**Likelihood:** Low

**Impact:** Medium

**Mitigation:** Crow backend implements rate limiting of maximum 10 requests per minute
per IP address. Requests exceeding this threshold are rejected with HTTP 429. Flagged
in server logs for review.



**A Risk Matrix Showing the Likelihood and Impact of these Security Issues Graphically**
<img width="1163" height="760" alt="image" src="https://github.com/user-attachments/assets/5138ffb6-f85c-46fb-9991-e88ee26a8672" />
