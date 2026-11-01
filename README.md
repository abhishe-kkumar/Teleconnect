# TeleConnect – Telecom Plan Management System

A beginner-friendly Spring Boot website that demonstrates **Java OOP** through a telecom recharge portal. Users browse fictional prepaid and postpaid plans, compare them, simulate a recharge and view their history.

> Plans are fictional and inspired by common telecom offerings. Payments are **simulated**: no money moves and no real recharge happens. Data lives in Java `ArrayList`s, so **everything resets when the application restarts**.

## Problem statement
Telecom customers need to compare plans and recharge quickly. This project models that domain with clean Java classes so the OOP ideas are easy to see and explain.

## Features
- Home page, plan cards (prepaid + postpaid), side-by-side comparison of 2–3 plans
- Recharge form with server-side validation and simulated payment (with a "simulate failed payment" demo checkbox)
- Receipt page and recharge history (all, or filtered by mobile number)
- Simple admin page (no login): add a plan, activate/deactivate a plan

## Technology
Java 21, Spring Boot 3.3.5 (MVC), Thymeleaf, HTML/CSS/JavaScript, Maven, JUnit 5. No database.

## Prerequisites
JDK 21 (or any LTS 17+ after changing `java.version` in `pom.xml`) and Maven 3.9+.

## Run
```bash
mvn spring-boot:run          # then open http://localhost:8080
mvn test                     # run all tests
```
Or build a jar: `mvn clean package` then `java -jar target/teleconnect-1.0.0.jar`.

## Project structure
```text
TeleConnect/
├── pom.xml, README.md, .gitignore
├── docs/PROJECT_REPORT.md                 UML, class notes, demo script, viva Q&A
└── src/
    ├── main/java/com/teleconnect/
    │   ├── TeleConnectApplication.java
    │   ├── model/       TelecomPlan, PrepaidPlan, PostpaidPlan, Rechargeable,
    │   │                Customer, Recharge, RechargeException
    │   ├── service/     PlanService, RechargeService
    │   └── controller/  HomeController, PlanController, RechargeController
    ├── main/resources/
    │   ├── application.properties
    │   ├── templates/   fragments, index, plans, compare, recharge, success, history, admin
    │   └── static/      css/style.css, js/script.js
    └── test/java/com/teleconnect/  application, web (MockMvc) and unit tests
```
**Changes from the suggested structure:** added `RechargeException` (clear business errors), `fragments.html` (shared header/footer/plan card), `compare.html`, `success.html`, `admin.html`, and extra test classes. Admin routes live in `PlanController` to avoid a fourth controller.

## Routes
| Method | URL | Purpose |
|---|---|---|
| GET | `/` | Home page |
| GET | `/plans`, `/plans/compare?ids=P101&ids=P105` | List and compare plans |
| GET / POST | `/recharge` | Recharge form / submit (POST redirects to receipt) |
| GET | `/recharge/success/{transactionId}` | Receipt |
| GET | `/history?mobile=` | History |
| GET | `/admin` | Admin page |
| POST | `/admin/plans`, `/admin/plans/{id}/toggle` | Add plan, activate/deactivate |

## Example workflows
1. **Recharge:** Plans → click *Recharge* on a card → enter `9876543210` → *Pay and recharge* → receipt → *View history*.
2. **Compare:** Plans → tick two plans → *Compare selected*.
3. **Failure demo:** Recharge page → tick *Simulate a failed payment* → error shown, history stays empty.
4. **Admin:** Admin → fill the form → plan appears on Plans; *Deactivate* hides it from customers.

## Rules used
- Mobile number: exactly 10 digits, starting with 6, 7, 8 or 9 (Indian mobile format).
- Plan must exist and be active. Transaction IDs look like `TC2610261530450001` (timestamp + counter).

## OOP concepts and where they are used
| Concept | Where |
|---|---|
| Encapsulation | Private fields + getters in `TelecomPlan`, `Recharge`; `Customer.getHistory()` returns a read-only list |
| Abstraction | `TelecomPlan` is abstract with abstract `getPlanType()`, `getPriceLabel()`, `getValidityLabel()`, `displayBenefits()` |
| Inheritance | `PrepaidPlan` and `PostpaidPlan` extend `TelecomPlan` |
| Polymorphism | `PlanService` holds `List<TelecomPlan>`; templates call `plan.priceLabel` / `plan.displayBenefits()` and each subclass answers differently |
| Interface | `Rechargeable` implemented by `RechargeService` |
| Composition | `Customer` has a list of `Recharge`; `RechargeService` uses `PlanService` and `Customer` objects |
| Collections | `ArrayList` (plans, transactions, history), `HashMap` (customers by mobile) |
| Exception handling | `RechargeException` for business errors; `IllegalArgumentException` for invalid plan data; controllers show friendly messages |

## Limitations and future work
In-memory storage (resets on restart); no login for admin; payment is a simulation; no plan editing. Future: database (Spring Data JPA), Spring Security, real payment gateway, plan editing.

See [docs/PROJECT_REPORT.md](docs/PROJECT_REPORT.md) for UML diagrams, class notes, demo script and viva questions.
