# Business Decisions

## Introduction

The business specification (`Especificación Funcional del Negocio - NexusMarket.pdf`) is intentionally concise. While modeling the domain and the services, several points were found where the specification is **silent, ambiguous, or apparently contradictory**. Rather than resolving them silently inside the code, each one is recorded here as an explicit, traceable decision.

Every decision follows the same structure:

* **Specification** — what the business specification says (or does not say), quoted where possible.
* **Issue** — why it cannot be implemented literally.
* **Decision** — the rule adopted by NexusMarket.
* **Justification** — why this option was chosen over the alternatives.

`Domain Model.md`, `Domain Value Objects.md`, `Domain Services.md`, and each file in `SDD/services/` reference these decisions by code (`DEC-xx`) wherever they apply. A service or entity must never apply an interpretation that is not either stated in the specification or recorded here. When a new ambiguity is found, it is added here with the next free code; existing codes are never renumbered.

Section numbers (§) refer to the business specification PDF, transcribed in `ESPECIFICACION_FUNCIONAL.md` with the same numbering.

## Index

| Code | Decision | Area |
| --- | --- | --- |
| DEC-01 | Only authentication and buyer self-registration precede RG-01 | Access and roles |
| DEC-02 | The identifier of a user is their identity document | Access and roles |
| DEC-03 | Login by email or username, case-insensitive | Access and roles |
| DEC-04 | Processes missing from the Responsibility Matrix are assigned from the Participants table | Access and roles |
| DEC-05 | Participation of each role in "Gestión de Pedidos" | Access and roles |
| DEC-06 | Logistics Operators are not assigned to warehouses | Access and roles |
| DEC-07 | Effects of `ACTIVE` / `RESTRICTED` / `SUSPENDED` commercial status | Buyers and sellers |
| DEC-08 | A non-active seller's products are not available for sale | Buyers and sellers |
| DEC-09 | A seller is incorporated together with their first warehouse | Buyers and sellers |
| DEC-10 | Warehouse ownership is permanent | Buyers and sellers |
| DEC-11 | Products have a price | Catalog and inventory |
| DEC-12 | Products are registered as `PUBLISHED`; no draft status | Catalog and inventory |
| DEC-13 | Where a product's inventory may be stored | Catalog and inventory |
| DEC-14 | "Non-existent inventory" and the choice of warehouse | Catalog and inventory |
| DEC-15 | Scope of the "damaged" condition | Catalog and inventory |
| DEC-16 | Price capture and re-validation at confirmation | Orders |
| DEC-17 | One open purchase per buyer, one line per product | Orders |
| DEC-18 | Delivery address chosen at confirmation | Orders |
| DEC-19 | "Entregado / Finalizado" is a single status | Orders |
| DEC-20 | Cancellation without a "cancelled" status | Orders |
| DEC-21 | One shipment per origin warehouse | Orders |
| DEC-22 | Payment validated by an external signal | Orders |
| DEC-23 | Invoice amount without taxes or shipping | Orders |
| DEC-24 | Only physical products are returned; one return per order | Returns and refunds |
| DEC-25 | Stock at completion; refund opened `PENDING` and resolved by the Administrator | Returns and refunds |
| DEC-26 | An Administrator cannot change their own status | Additional |
| DEC-27 | `DISCONTINUED` is final | Additional |
| DEC-28 | Meaning of "entrega inmediata" for digital products | Additional |
| DEC-29 | Scope of the administrative reports (OBJ-12) | Additional |

---

# Access and Roles

## DEC-01 — Operations that precede authentication

* **Specification:** RG-01 — *"Toda operación debe ejecutarse por un usuario autenticado."* At the same time, §3.1 includes *"Registro de compradores"*, and Domain 3 states that only sellers are prevented from self-registering.
* **Issue:** Read literally, RG-01 makes it impossible for a buyer to register (they are not yet a user) and for anyone to authenticate (authenticating is itself an operation).
* **Decision:** Exactly two operations are exempt from RG-01: authentication (`AuthenticationService.authenticate`) and buyer self-registration (`BuyerRegistrationService.registerBuyer`). Every other operation requires an authenticated `User`.
* **Justification:** Both operations are the *entry points* that produce an authenticated user; RG-01 governs everything done afterwards. Domain 3 explicitly forbids self-registration only for sellers, which implies buyers do self-register.

## DEC-02 — Identity document and identifier

* **Specification:** Domain 1 defines an *"Identificador — Identifica de forma única al usuario"*; §11 requires *"El documento de identidad y correo electrónico deben ser únicos"*.
* **Issue:** The specification names two concepts (identifier, identity document) but never states whether they are the same attribute.
* **Decision:** They are the same attribute: `User.id` is the person's identity document number, provided at registration (never generated by the system), and is unique across the platform.
* **Justification:** Introducing two unique identifiers for the same person adds no business value and would require a second uniqueness rule not mentioned by the specification.

## DEC-03 — Access credential

* **Specification:** Domain 1 describes the email as the *"Medio principal de acceso y comunicación"*. Technical authentication mechanisms are out of scope (§3.2).
* **Issue:** The domain also defines `username`, which the specification does not mention.
* **Decision:** A user authenticates with a *login* value that may be either their `email` or their `username`, plus their password. Both are unique across the platform and compared case-insensitively (stored in lowercase).
* **Justification:** Respects the specification (email is a valid means of access) while keeping `username`, a common marketplace convention. Case-insensitive comparison prevents `Ana@x.com` and `ana@x.com` from being registered as different people.

## DEC-04 — Processes missing from the Responsibility Matrix

* **Specification:** The Responsibility Matrix (§12) lists only five processes and four roles. It does not include the Supervisor, nor the processes *"Administración de usuarios"*, *"Administración de bodegas"*, *"Gestión de envíos"*, *"Gestión de devoluciones"*, or *"Consulta de reportes administrativos"*, all of which are in scope (§3.1).
* **Issue:** RG-03 requires every operation to be restricted to a role, but the matrix does not assign these processes to anyone.
* **Decision:** The missing rows are assigned from the Participants table (§5), which describes each role's responsibility:

| Process | Assigned to | Source |
| --- | --- | --- |
| User administration | Administrator | OBJ-01 + Administrator is the only administrative profile |
| Warehouse administration | Administrator | §5: *"Administrador: Responsable de la administración de vendedores y bodegas"* |
| Shipment execution | Logistics Operator | §5: *"Operador Logístico: Encargado de la operación física de bodegas y despachos"* |
| Returns | Buyer (request) + Administrator (review, receipt, refund) | Same actors as *"Gestión Reembolsos"*, since a refund only exists as the outcome of a return |
| Administrative reports | Supervisor, read-only | §5: *"Supervisor: Perfil de consulta y seguimiento operativo"*; OBJ-12 |

* **Justification:** Each assignment is taken from the specification's own description of the role, not invented; the Participants table is the only other place where responsibilities are stated.

## DEC-05 — Seller and Logistics Operator in "Gestión de Pedidos"

* **Specification:** The matrix grants *"Gestión de Pedidos"* to Buyer, Seller, and Logistics Operator.
* **Issue:** The specification does not say what a seller or a logistics operator may do to an order. Allowing a seller to modify a buyer's order would contradict Domain 2 (*"El comprador nunca administrará información de otros compradores"*) in spirit, and allowing a seller to see other sellers' items would violate RG-03.
* **Decision:**
  * The **Buyer** drives the commercial lifecycle of their own orders (cart, confirmation, cancellation).
  * The **Seller** consults the orders that contain their products and sees **only their own items** within those orders — never other sellers' items or prices.
  * The **Logistics Operator** participates through the shipments of the order (creation is automatic; the operator advances them), and consults orders that contain physical products.
  * The **Administrator** does not manage orders (the matrix explicitly excludes them), but consults invoices, shipments, and returns as documented in each service.
* **Justification:** This gives every role in the matrix a real participation in order management while keeping RG-03 intact.

## DEC-06 — Logistics Operators are not assigned to warehouses

* **Specification:** The Logistics Operator is *"Encargado de la operación física de bodegas y despachos"*; no attribute links an operator to a specific warehouse.
* **Issue:** Scoping an operator to "their" warehouses would require an attribute the specification does not define.
* **Decision:** Any Logistics Operator may operate the inventory of any warehouse and advance any shipment.
* **Justification:** Avoids inventing an attribute. If the business later assigns operators to warehouses, it becomes a new `User` specialization, without affecting the rest of the model.

---

# Buyers and Sellers

## DEC-07 — Meaning of the buyer's commercial status

* **Specification:** Domain 2 defines *"Estado comercial — Condición del comprador para realizar compras"* as mandatory, but never lists its values or their effects.
* **Issue:** Without defined effects, the attribute cannot be enforced consistently by the Cart and Order services.
* **Decision:** Three values with precise, non-overlapping effects:

| Code | Manage cart | Confirm new orders | Orders already confirmed |
| --- | :---: | :---: | --- |
| ACTIVE | ✔ | ✔ | Continue normally |
| RESTRICTED | ✔ | ✘ | Continue normally |
| SUSPENDED | ✘ | ✘ | Continue normally |

  Only the Administrator changes it (`UserManagementService.changeBuyerCommercialStatus`).
* **Justification:** `RESTRICTED` lets the buyer keep preparing a purchase while a pending situation is resolved; `SUSPENDED` blocks every new purchasing action. Neither affects orders already confirmed, consistent with the "no retroactive effect" principle applied to products.

## DEC-08 — A seller's status affects the availability of their products

* **Specification:** Silent on what happens to a seller's products when the seller is deactivated or blocked.
* **Issue:** A blocked seller cannot operate, but their `PUBLISHED` products would remain purchasable.
* **Decision:** A product is *available for sale* only when its status is `PUBLISHED` **and** its seller's `UserStatus` is `ACTIVE`. Products of a non-active seller are hidden from the public catalog and cannot be added to a cart, but their own `ProductStatus` is not changed, and orders already confirmed are not affected.
* **Justification:** Avoids selling products nobody will fulfill, without rewriting the seller's catalog: when the seller is reactivated, their products become available again automatically.

## DEC-09 — Incorporation of a seller includes their first warehouse

* **Specification:** §6.1, step 1: *"El Administrador registra al vendedor y su primera bodega."*
* **Decision:** Registering a seller and registering their first warehouse is a single, atomic operation (`SellerRegistrationService.registerSeller`). Additional warehouses are registered later through `WarehouseManagementService`.
* **Justification:** Literal reading of the business flow; it guarantees that every seller can hold inventory from the moment they are incorporated.

## DEC-10 — Warehouse ownership is permanent

* **Specification:** Domain 4 distinguishes Marketplace warehouses and Seller warehouses; it does not mention transfers.
* **Decision:** The `owner` of a warehouse is set at registration and never changes; only `name` and `address` can be updated.
* **Justification:** Changing the owner would invalidate the inventory already stored there (see DEC-13).

---

# Catalog and Inventory

## DEC-11 — Product price

* **Specification:** The catalog attributes (Domain 5) are *type*, *variants*, and *status*; no price is defined, yet the system must invoice purchases (OBJ-09) and issue refunds (OBJ-11).
* **Issue:** Without a price there is no source for `OrderItem.unitPrice`, `Invoice.totalAmount`, or `Refund.amount`.
* **Decision:** `Product` has a mandatory `price` (`BigDecimal`, greater than zero), set by the seller and updatable. A price change never affects orders already confirmed (see DEC-16).
* **Justification:** Billing and refunds are in scope, so a price is an unavoidable implicit attribute.

## DEC-12 — Product registration and publication

* **Specification:** §6.1 describes *Catálogo* (step 2), *Inventario* (step 3), and *Publicación* (step 4) as separate steps, but `Estado` only has three values: *Publicado, Suspendido, Descontinuado*.
* **Issue:** There is no "draft" status to represent a product that is registered but not yet published.
* **Decision:** A product is registered directly as `PUBLISHED`; the seller may immediately change it to `SUSPENDED` if it is not ready to be shown. No fourth status is introduced. A published physical product without stock is visible but cannot be reserved (the check happens at order confirmation). The product *type* (physical or digital) cannot change after registration.
* **Justification:** Respects the three statuses defined by the specification; steps 2–4 describe the order in which the business happens, not three different statuses. The type is a specialization (`PhysicalProduct` / `DigitalProduct`), not an attribute, so it cannot mutate.

## DEC-13 — Where a product's inventory may be stored

* **Specification:** Domain 6: the inventory *"debe estar vinculado obligatoriamente a un producto y una bodega específica"*. Domain 4 distinguishes Marketplace and Seller warehouses.
* **Issue:** Nothing prevents a seller's product from being stored in another seller's warehouse.
* **Decision:** A product's inventory may only be stored in a **Marketplace-owned** warehouse or in a warehouse **owned by the product's own seller**. This is an invariant of `Inventory` itself. The `Inventory` record for a product/warehouse pair is created by its first `INBOUND` movement (§6.1 step 3, *"Se registran existencias iniciales"*).
* **Justification:** A seller must never manage stock located in another seller's facilities (RG-03). Creating the record on first inbound avoids an extra "create empty inventory" operation the specification does not describe.

## DEC-14 — "Non-existent inventory" and the choice of warehouse

* **Specification:** §11: *"No se puede reservar inventario inexistente o marcado como Dañado."* The inventory is *distributed* (OBJ-06).
* **Issue:** "Non-existent" may mean "no record" or "zero units"; and when a product is stored in several warehouses, the specification does not say which one supplies an order.
* **Decision:**
  * Inventory is non-existent for an order item when there is **no record, or no record with enough available units**. Both cases raise `InsufficientInventoryException`.
  * Each order item is reserved **from a single inventory record** (it is not split across warehouses). Among the `AVAILABLE` records with enough units, the one with the highest `availableQuantity` is chosen.
  * If the only records with enough units are `DAMAGED`, `DamagedInventoryReservationException` is raised.
* **Justification:** A single source per item keeps shipment, sale-outbound, and return traceability simple (one item → one warehouse). Choosing the largest stock is deterministic and reduces the chance of exhausting small warehouses.

## DEC-15 — Scope of the "damaged" condition

* **Specification:** Inventory can be *"marcado como Dañado"*.
* **Decision:** `InventoryCondition` applies to a whole inventory record. When only some units are damaged, they are removed from `availableQuantity` with a negative `ADJUSTMENT` (with its reason recorded) instead of marking the whole record `DAMAGED`.
* **Justification:** Keeps the model with one condition per record, as the specification describes, without blocking healthy units.

---

# Orders

## DEC-16 — Unit price capture and re-validation at confirmation

* **Specification:** The cart is a *"Selección provisional de productos"*; the product rule forbids adding suspended/discontinued products to new orders.
* **Issue:** Between adding a product to the cart and confirming it, the product's price or status may change.
* **Decision:** `OrderItem.unitPrice` is captured when the item is added to the cart and **refreshed at confirmation** (`CART → PENDING_PAYMENT`), when each product's availability (DEC-08) is also verified again. From `PENDING_PAYMENT` onward, prices are frozen.
* **Justification:** The cart is provisional and not a commercial commitment; the order becomes a commitment at confirmation, so that is the moment the buyer's price must be fixed.

## DEC-17 — One open purchase per buyer, one line per product

* **Issue:** If a buyer could open a new cart while another order is `PENDING_PAYMENT`, cancelling that order (which returns it to `CART`, DEC-20) would leave the buyer with two carts.
* **Decision:** A buyer has at most one *open* order — in `CART` or `PENDING_PAYMENT` — at a time; they must pay or cancel a pending order before starting a new cart. Adding a product that is already in the cart increases the quantity of the existing line instead of creating a second one.
* **Justification:** Guarantees a single cart per buyer in every scenario and keeps an order's items unambiguous (a product appears once), which returns and invoicing rely on.

## DEC-18 — Delivery address

* **Specification:** Domain 2 gives the buyer a *primary address* (mandatory) and *additional addresses* (optional) *"para entregas"*, but the order has no address attribute.
* **Decision:** When confirming an order containing physical products, the buyer chooses its `deliveryAddress` among their primary or additional addresses. It is stored in the `Order` as a snapshot, so later changes to the buyer's addresses do not affect it. Digital-only orders have no delivery address.
* **Justification:** A physical shipment is impossible without knowing where to deliver it, and the specification already provides the possible addresses.

## DEC-19 — "Entregado / Finalizado" is a single status

* **Specification:** Domain 7 lists *"Entregado / Finalizado"* as one step; §6.1 step 8 says *"El pedido se marca como finalizado tras la entrega confirmada"*; §11 says a finalized order cannot be modified.
* **Issue:** It could be read as two statuses, and it seems to contradict returns (a finalized order cannot change).
* **Decision:** `DELIVERED` is the single terminal status and means finalized. A return never modifies the order: it is a separate entity (`ReturnRequest`) that references it, and the order remains `DELIVERED` forever.
* **Justification:** The specification writes both words in the same step; and representing returns as separate entities satisfies both rules at once.

## DEC-20 — Cancellation without a "cancelled" status

* **Specification:** The order lifecycle has exactly five statuses; none of them is "cancelled".
* **Decision:** An order can only be cancelled while in `PENDING_PAYMENT`: it returns to `CART` and its reservations are released (`ADJUSTMENT` movements). Cancellation is triggered by the buyer or by a rejected-payment signal. From `PAID` onward, an order cannot be cancelled; the post-sale path is a return.
* **Justification:** Respects the five statuses defined by the specification without inventing a sixth.

## DEC-21 — Shipments of an order stored in several warehouses

* **Specification:** Domain 7: *"Despachado: Salida física de la bodega"*; *"Pagado: Inicio de procesos de alistamiento"*.
* **Issue:** Because of DEC-14, the physical items of one order may come from different warehouses, and a shipment has a single origin warehouse. Also, "shipped" must mean that the goods physically left, not that a shipment was created.
* **Decision:**
  * When a physical order reaches `PAID`, one `Shipment` is created **per origin warehouse**, in `PREPARING` (this is the *alistamiento*).
  * The order moves to `SHIPPED` when **all** its shipments are `IN_TRANSIT` or later (all goods left their warehouses).
  * The order moves to `DELIVERED` when **all** its shipments are `DELIVERED`.
* **Justification:** Matches the literal meaning of *"Salida física de la bodega"* and supports distributed inventory without splitting items.

## DEC-22 — Payment validation

* **Specification:** §6.1 step 6: *"Se valida el pago"*; payment technologies are out of scope (§3.2).
* **Decision:** Payment is confirmed or rejected by an external signal received by `PaymentConfirmationService`; NexusMarket does not model payment methods. A rejected payment triggers the cancellation of DEC-20.
* **Justification:** The specification requires validating payment but excludes the technology used to do it.

## DEC-23 — Invoice amount

* **Specification:** Billing is *"Información comercial asociada a las ventas"*; no taxes, discounts, or shipping costs are defined.
* **Decision:** `Invoice.totalAmount` is the sum of `unitPrice × quantity` of the order's items. There is exactly one invoice per order, generated when it reaches `PAID`.
* **Justification:** Any tax or shipping rule would be invented; the model can be extended if the business defines them.

---

# Returns and Refunds

## DEC-24 — What can be returned

* **Specification:** Returns and refunds are in scope (§3.1), with no further rules.
* **Issue:** Digital products are delivered immediately and cannot be physically received back; the specification does not say whether partial returns or several returns per order are allowed.
* **Decision:**
  * Only `PhysicalProduct` items can be returned.
  * A return may include some or all of the physical items of the order, each with a quantity between 1 and the purchased quantity.
  * An order has at most one `ReturnRequest`.
  * Only `DELIVERED` orders can be returned. No time limit is applied, since the specification defines none.
* **Justification:** A digital product has no physical receipt to confirm and no stock to reincorporate. One return per order keeps the refund and stock traceability one-to-one.

## DEC-25 — Stock reincorporation and refund decision

* **Specification:** Silent on the order of these steps.
* **Decision:**
  1. The Administrator **reviews** the request (`APPROVED` / `REJECTED`): a commercial decision.
  2. When the product is physically received, the Administrator **completes** the return (`COMPLETED`), and the units are reincorporated with a `RETURN` movement into the same inventory record they left from.
  3. Completing the return automatically **opens its refund** in `PENDING`, for the sum of `unitPrice × returned quantity`.
  4. The Administrator then **resolves the refund**: `PROCESSED` (funds returned) or `REJECTED` (for example, the received product does not correspond to what was sold). A rejected refund does not revert the stock, because the units are physically in the warehouse.
* **Justification:** Inventory must always reflect physical reality, while the refund is a separate financial decision, consistent with `Refund` being its own entity with its own status. Opening the refund in `PENDING` gives every value of `RefundStatus` a real meaning and makes pending refunds visible to the Administrator and the Buyer.

---

# Additional Decisions

## DEC-26 — An Administrator cannot change their own status

* **Specification:** Silent.
* **Issue:** An administrator who deactivates or blocks themselves could leave the platform without anyone able to administer users, sellers, and warehouses.
* **Decision:** `UserManagementService.changeUserStatus` rejects a request where the target user is the calling Administrator. Another Administrator must do it.
* **Justification:** A minimal safeguard that does not restrict any legitimate operation.

## DEC-27 — `DISCONTINUED` is final

* **Specification:** *"Descontinuado"* is one of the three product statuses; its meaning is not detailed.
* **Issue:** If a discontinued product could be republished, `DISCONTINUED` would be indistinguishable from `SUSPENDED`.
* **Decision:** `SUSPENDED` is temporary and reversible; `DISCONTINUED` is permanent: a discontinued product cannot change status nor be updated, but it remains in the orders, invoices, and returns that already reference it.
* **Justification:** Gives each of the three statuses a distinct meaning, consistent with the catalog descriptions (*"temporarily hidden"* vs. *"permanently removed from commercialization"*).

## DEC-28 — Meaning of "entrega inmediata" for digital products

* **Specification:** Domain 5: digital products have *"entrega inmediata tras pago"*. No attribute describes what is delivered, and delivery technologies are out of scope (§3.2).
* **Issue:** "Immediate delivery" has no mechanism in the model: there is no download link, license key, or delivery entity.
* **Decision:** A digital item is **delivered** when its order reaches `PAID`: from that moment, the item is marked as available to the buyer when they consult the order (`OrderConsultationService`). How the buyer technically accesses the content (download, license, streaming) is an implementation technology and is not modeled. No additional entity or status is introduced.
* **Justification:** Satisfies the functional rule (the buyer obtains the product as soon as the payment is validated, without waiting for any shipment) without inventing attributes or technology the specification explicitly excludes.

## DEC-29 — Scope of the administrative reports (OBJ-12)

* **Specification:** OBJ-12: *"Consolidar información administrativa para consulta"*; the Supervisor is a *"Perfil de consulta y seguimiento operativo"* (§5). The reports themselves are not listed.
* **Issue:** Without a defined scope, the reports could be anything; with too narrow a scope, the Supervisor cannot follow the operation.
* **Decision:** One read-only report per business area whose data is managed by the platform, aligned with the processes in scope (§3.1):

| Report | Covers | Service |
| --- | --- | --- |
| Users | Users by role and status; buyers by commercial status | `UserReportingService` |
| Catalog | Products by status, type, and seller | `CatalogReportingService` |
| Inventory | Stock by warehouse, product, and condition; movements by type | `InventoryReportingService` |
| Orders and billing | Orders by status; invoiced amount | `OrderReportingService` |
| Logistics | Shipments by status and origin warehouse; pending dispatches | `ShipmentReportingService` |
| Returns and refunds | Returns by status; refunds by status and amount | `ReturnRefundReportingService` |

  Every report can be restricted to a date range where the data has a date. Billing is reported together with orders because an invoice always corresponds to exactly one order.
* **Justification:** Covers every process of §3.1 that produces operational data (*"seguimiento operativo"* explicitly includes logistics), keeps one report per aggregate as the Services decomposition criterion requires, and never exposes more than the Supervisor needs (read-only, aggregated).
