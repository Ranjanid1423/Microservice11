# MICROSERVICES — DAY 11
# ADVANCED COMMUNICATION & ARCHITECTURE

## Mission

The goal of Day 11 is to understand the decisions and failure
scenarios that occur when USER-SERVICE and ORDER-SERVICE operate
as a real distributed system.

---

# 1. SERVICE BOUNDARIES — WHO OWNS WHAT?

A microservice should own a clear business capability and the
data required to manage it.

Other services should communicate through APIs or messaging
instead of directly accessing another service's internals.

## USER-SERVICE

Owns:
- User identity data
- User profile data

Exposes:
- User APIs

## ORDER-SERVICE

Owns:
- Order data
- Order-item data

Exposes:
- Order APIs

### Practice 1

Write five responsibilities for USER-SERVICE and five
responsibilities for ORDER-SERVICE.

Identify any overlap and decide which service should own it.

### Practice 2

For every Order API, decide whether communication with
USER-SERVICE is required.

Do not call USER-SERVICE automatically if ORDER-SERVICE
already owns the required information.


# 2. DATABASE PER SERVICE — DATA OWNERSHIP

Each service owns its persistent data.

Other services should not directly query or modify another
service's database.

## Database Ownership

USER-SERVICE
    ↓
USER DB

ORDER-SERVICE
    ↓
ORDER DB

### Rule

ORDER-SERVICE must not directly access USER DB.

If ORDER-SERVICE needs user information, it should communicate
with USER-SERVICE through its API.

### Practice 3

Draw the database ownership diagram and identify which service
owns every entity/table.

### Practice 4

If ORDER-SERVICE needs:

- userId
- name
- email
- accountStatus

decide which information should be requested synchronously
and which information could be stored locally.

Explain the trade-off.

### Practice 5

Question:

"Can ORDER-SERVICE directly query USER DB because it is faster?"

Answer:

No.

USER-SERVICE owns USER DB.

ORDER-SERVICE should obtain user information through
USER-SERVICE APIs.

Direct database access creates tight coupling and breaks
service ownership and data isolation.


# 3. SYNCHRONOUS VS ASYNCHRONOUS COMMUNICATION

## Synchronous Communication

The caller waits for a response.

REST is a common example.

Flow:

ORDER-SERVICE
    ↓
USER-SERVICE
    ↓
Response
    ↓
ORDER-SERVICE continues

Example:

User verification can be synchronous because ORDER-SERVICE
may need to know whether the user is valid before continuing.

## Asynchronous Communication

The producer publishes an event/message and continues without
waiting for every consumer.

Flow:

ORDER-SERVICE
    ↓
Event / Message
    ↓
Consumer processes later

Examples:

- Post-order notification
- Analytics
- Audit events

## Practice 6 — Classification

User verification
→ SYNC

Post-order notification
→ ASYNC

Account-status check
→ SYNC

Analytics
→ ASYNC

Audit event
→ ASYNC

## Practice 7

### If a synchronous dependency is unavailable:

The current client request can be affected.

The caller may experience:

- Error
- Timeout
- Failure response

### If an asynchronous consumer is unavailable:

The producer may still complete its own operation.

The consumer can process the event later depending on
the messaging design.

## Practice 8 — OrderCreated Event

Example:

{
    "eventType": "OrderCreated",
    "orderId": 101,
    "userId": 1,
    "productName": "Laptop",
    "quantity": 2
}

Required information:

- eventType
- orderId
- userId
- productName
- quantity

Sensitive information should not be published.

Examples of sensitive information:

- Passwords
- Credit-card numbers
- Authentication tokens
- Payment credentials


# 4. DISTRIBUTED TRANSACTIONS — SAGA

A local database transaction is easier because one database
controls commit and rollback.

With microservices, different services can have independent
transactions and databases.

Example:

ORDER-SERVICE
    ↓
PAYMENT-SERVICE
    ↓
Payment succeeds
    ↓
ORDER-SERVICE update fails

The system then needs recovery or compensation.

## Saga

Saga breaks a distributed business operation into local
transactions.

If a later step fails, a compensating action can be executed
where the business process requires it.

## Practice 9 — Local Transactions

### ORDER-SERVICE

- Create order
- Save order
- Update order status

### PAYMENT-SERVICE

- Receive payment request
- Process payment
- Save payment result

## Practice 10 — Reconciliation

If payment succeeds but ORDER-SERVICE crashes before recording
the result, the system needs reliable information such as:

- Order ID
- Payment ID
- User ID
- Payment status
- Order status
- Transaction/reference ID
- Created timestamp
- Updated timestamp

Example:

{
    "orderId": 101,
    "paymentId": 1,
    "userId": 1,
    "paymentStatus": "SUCCESS",
    "orderStatus": "CREATED",
    "transactionReference": "TXN-101-001"
}

## Practice 11 — Compensation

Example:

ORDER-SERVICE
    ↓
Create Order
    ↓
PAYMENT-SERVICE
    ↓
Payment SUCCESS
    ↓
ORDER-SERVICE update fails

Recovery:

Payment SUCCESS
    ↓
Order update FAILED
    ↓
Reconcile Order and Payment
    ↓
Either:
- Mark Order as PAID
OR
- Reverse/refund payment if supported

Important:

Saga is not an automatic database rollback mechanism.

Compensation is business logic and must be deliberately
designed.


# 5. API GATEWAY + SERVICE DISCOVERY

Typical flow:

CLIENT
    ↓
API GATEWAY
    ↓
ORDER-SERVICE
    ↓
SERVICE DISCOVERY
    ↓
USER-SERVICE
    ↓
USER DB

ORDER-SERVICE
    ↓
ORDER DB

## API Gateway

Responsibility:

Route external client requests to the appropriate service.

Example:

Client
    ↓
API Gateway
    ↓
ORDER-SERVICE

## Service Discovery

Responsibility:

Help services find available instances of another service.

Example:

ORDER-SERVICE
    ↓
EUREKA
    ↓
USER-SERVICE

## Difference

API Gateway
→ Request routing / external entry point

Service Discovery
→ Service instance discovery

Internal service-to-service communication does not have to
travel through the Gateway.

## Practice 12

Trace:

POST /api/orders

Flow:

Client
    ↓
API Gateway
    ↓
Routing
    ↓
ORDER-SERVICE
    ↓
Service Discovery
    ↓
USER-SERVICE
    ↓
User validation
    ↓
ORDER-SERVICE business logic
    ↓
ORDER DB
    ↓
Response

Include:

- Routing
- Discovery
- Authentication
- Business logic

## Practice 13

Run multiple ORDER-SERVICE instances.

Stop one instance.

Observe how Service Discovery and Load Balancing behave.

## Practice 14

API Gateway:
Responsible for external request routing.

Service Discovery:
Responsible for finding available service instances.


# 6. OBSERVABILITY — DISTRIBUTED DEBUGGING

When one request crosses multiple services, developers need
a way to correlate the related logs.

## Correlation ID

Example:

ABC-123

Flow:

Gateway
ABC-123
    ↓
ORDER-SERVICE
ABC-123
    ↓
USER-SERVICE
ABC-123
    ↓
ORDER-SERVICE
ABC-123

The same correlation ID should appear in related logs.

## Practice 15

Add a correlation ID to a request.

Propagate it through:

Gateway
    ↓
ORDER-SERVICE
    ↓
USER-SERVICE

Verify that the same ID appears in logs.

## Practice 16

Create a failure.

Use only logs first to identify which service failed.

Form a hypothesis before checking the source code.

## Practice 17 — Sensitive Information

Do not put sensitive information into application logs.

Examples:

- Passwords
- Credit-card numbers
- Authentication tokens
- Payment credentials
- Other confidential user information


# 7. FAILURE SIMULATION LAB

The objective is not only to make the system work.

The objective is also to understand how the system behaves
when dependencies fail.

## Failure 1 — USER-SERVICE Down

Investigate:

- Order response
- Timeout
- Retry
- Circuit behavior

## Failure 2 — USER-SERVICE Slow

Investigate:

- Timeout
- Resource impact

## Failure 3 — ORDER-SERVICE Down

Investigate:

- Gateway response
- Routing behavior

## Failure 4 — Database Unavailable

Investigate:

- Application exception
- Safe failure behavior

## Failure 5 — Invalid User ID

Investigate:

- Validation/business error
- Whether an order is created

## Failure 6 — Repeated USER-SERVICE Failure

Investigate:

- Circuit state transitions

Possible circuit states:

CLOSED
    ↓
OPEN
    ↓
HALF-OPEN
    ↓
CLOSED

## Practice 18

For every failure document:

Request
    ↓
Service
    ↓
Dependency
    ↓
Failure
    ↓
Response
    ↓
Logs
    ↓
Recovery

## Practice 19

Restart the failed service.

Document:

- Does the client retry?
- Does the circuit recover automatically?
- Does the request work again?


# 8. FINAL ARCHITECTURE CHALLENGE

Use only:

- USER-SERVICE
- ORDER-SERVICE

## Architecture

CLIENT
    ↓
API GATEWAY
    ↓
ORDER-SERVICE
    ↓
ORDER DB

ORDER-SERVICE
    ↓
USER-SERVICE
    ↓
USER DB

## Deliverables

### 1. Ownership

Define exact responsibility of:

USER-SERVICE
ORDER-SERVICE

### 2. APIs

Document:

- Order APIs
- Required User API calls

### 3. Data

Define:

- Database ownership
- Local copies of data

### 4. Communication

Choose:

- SYNC
- ASYNC

where appropriate.

### 5. Resilience

Define:

- Timeout
- Retry
- Circuit breaker behavior

### 6. Security

Define:

- Authentication responsibility
- Authorization responsibility

### 7. Observability

Define:

- Correlation ID
- Logging strategy

### 8. Failure

Define what happens when USER-SERVICE is unavailable.

### 9. Recovery

Define how the system recovers when USER-SERVICE returns.

### 10. Implementation

Implement one complete:

ORDER → USER

flow.


# 9. FINAL IMPLEMENTATION REQUIREMENTS

The project should include:

1. Controller → Service → Repository layering
2. DTO request/response
3. Validation
4. Global exception handling
5. Order → User communication
6. Service discovery/load balancing where applicable
7. Timeout/resilience handling
8. Transactional Order persistence
9. Authentication/authorization
10. Correlation-aware logging
11. Tests for happy paths
12. Tests for failure paths

## Mandatory Break Test

Stop USER-SERVICE while creating an order.

Explain exactly what happens at every layer.

Then restart USER-SERVICE and prove recovery.


# 10. REVIEW QUESTIONS

1. Why should each microservice have clear ownership of a
   business capability?

2. Why should ORDER-SERVICE not directly query USER DB?

3. When is synchronous communication a better choice?

4. Give two cases where asynchronous communication could
   be useful.

5. Why are distributed transactions harder than local
   transactions?

6. What is the basic idea behind Saga?

7. Does Saga automatically roll back every database?

8. What is the difference between API Gateway and Service
   Discovery?

9. Why is a correlation ID useful?

10. What should happen when USER-SERVICE is unavailable?

11. What happens to a circuit breaker after the dependency
    recovers?

12. Which service owns user data and which owns order data?

13. How would you handle data that ORDER-SERVICE needs
    frequently from USER-SERVICE?

14. Why should logs avoid sensitive information?

15. How would you prove that your system recovers after
    a dependency failure?


# 11. ARCHITECTURE REVIEW

Draw the real project architecture using the latest project
and architecture decisions.

Discuss:

- Service responsibilities
- Database ownership
- Service-to-service calls
- Synchronous operations
- Asynchronous operations
- Authentication
- Authorization
- Audit events
- Request tracing
- Failure behavior
- Recovery
- Decisions requiring architecture confirmation


# 12. DAY 11 COMPLETION CRITERIA

## Individual

I should be able to explain:

- Service ownership
- Database isolation
- Sync vs Async communication
- Distributed transaction challenges
- API Gateway vs Service Discovery
- Distributed debugging

## Team

The team should be able to:

- Draw the current architecture
- Implement a complete User dependency flow
- Deliberately break the dependency
- Diagnose the failure
- Explain recovery

## Project Readiness

The system is ready for more realistic project stories when
the team can explain not only how the system works, but also
what happens when a dependency fails.


# DAY 11 FINAL SUMMARY

USER-SERVICE
→ Owns user data

ORDER-SERVICE
→ Owns order data

API GATEWAY
→ Routes external requests

EUREKA / SERVICE DISCOVERY
→ Finds service instances

SYNC
→ Caller waits for response

ASYNC
→ Producer publishes event and continues

SAGA
→ Local transactions + deliberate compensation

CORRELATION ID
→ Connects logs across services

RESILIENCE
→ Timeout + Retry + Circuit Breaker

FAILURE TESTING
→ Stop services, observe behavior, diagnose and recover