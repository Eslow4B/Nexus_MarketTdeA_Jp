# NexusMarket

University project for the **Software Construction 2** course (TdeA): a digital marketplace that intermediates commercial transactions between buyers and sellers — users, catalog, distributed inventory, cart and orders, billing, shipments, returns, refunds, and administrative reports.

The project follows **Specification-Driven Design (SDD)**: the business specification is analyzed and documented first, and code is written only from that documentation.

## Repository Map

| Path | Content |
| --- | --- |
| `Especificación Funcional del Negocio - NexusMarket.pdf` | Business specification provided for the course (source of truth for requirements). |
| `ESPECIFICACION_FUNCIONAL.md` | Faithful transcription of the PDF, with the same section numbering. |
| `SDD/Domain/Business Decisions.md` | Decisions taken where the specification is silent, ambiguous, or contradictory (`DEC-01` … `DEC-29`). |
| `SDD/Domain/Domain Model.md` | Entities, relationships, invariants, and aggregates. |
| `SDD/Domain/Domain Value Objects.md` | Business catalogs (roles, statuses, movement types) and their lifecycles. |
| `SDD/Domain/Domain Services.md` | Index of the 37 domain services, decomposition criterion, orchestration, and conventions. |
| `SDD/services/` | One specification per domain service. |
| `SDD/Software Architecture/Software Architecture.md` | Hexagonal architecture, ports, authorization matrix, and business exceptions. |
| `nexusMarket/` | Spring Boot (Maven) project. |

Recommended reading order: Business Decisions → Domain Model → Domain Value Objects → Domain Services → the service files → Software Architecture.

## Architecture

Hexagonal architecture (Ports and Adapters) with Domain-Driven Design. The domain (`application.domain`) contains every business rule and depends on no framework; adapters (`application.adapters`) and infrastructure (`application.infrastructure`) depend on the domain, never the opposite.

## Progress

| Stage | Status |
| --- | --- |
| Domain model and value objects (code) | Done |
| Domain services (specification) | Done — 37 services |
| Ports (input and output) | Next stage |
| Domain services (code) | Pending |
| Adapters (REST, MySQL) and infrastructure | Pending |

## Tech Stack

Java 17 · Spring Boot 4.1.1 · Maven · MySQL · Lombok · JUnit 5 (H2 in-memory database for tests).

## Running

All Maven commands run from the `nexusMarket/` folder:

```bash
cd nexusMarket
./mvnw test            # Windows: mvnw.cmd test
```

The application connects to MySQL through the environment variables `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` (defaults: `jdbc:mysql://localhost:3306/nexusmarket`, `root`, empty password). Tests do not need MySQL.
