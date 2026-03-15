# Movie Ticket Booking System — Testing Guide


## Architecture Overview

```
Client → API Gateway (8765)
              ↓
         Booking Service (8080)  ← Eureka (8761)
              ↓ [Kafka: payment-request topic]
         Payment Service (8081)
              ↓ [Stripe API]
              ↓ [Kafka: payment-response topic]
         Booking Service (updates booking status)
```

**Databases:** MySQL (`bookings` DB for booking-service, `payments` DB for payment-service)  
**Message Broker:** 3-node Kafka cluster + Zookeeper  
**Service Discovery:** Eureka

---

## Prerequisites

- Docker & Docker Compose installed
- Java 17 + Maven (only needed for local runs)
- `curl` or Postman for API testing

---

## Step 1 — Start Everything

```bash
git clone 
cd Movie-Ticket-Booking-System

# Build and start all services
docker compose up --build

# Or run in background:
docker compose up --build -d
```

Expected startup order (Docker handles this via depends_on):
1. Zookeeper → Kafka brokers → init-kafka (creates topics)
2. MySQL
3. Eureka Server
4. API Gateway
5. Payment Service
6. Booking Service
7. Notification Service

**Tip:** First build takes ~5–10 minutes for Maven downloads.

---

## Step 2 — Verify Services Are Up

### Check Eureka Dashboard
Open in browser: http://localhost:8761

You should see all registered services:
- `BOOKING-SERVICE`
- `PAYMENT-SERVICE`
- `API-GATEWAY`
- `NOTIFICATION-SERVICE`

### Check Health Endpoints
```bash
curl http://localhost:8765/actuator/health   # API Gateway
curl http://localhost:8080/actuator/health   # Booking Service (direct)
curl http://localhost:8081/actuator/health   # Payment Service (direct)
curl http://localhost:8761/actuator/health   # Eureka Server
```

All should return: `{"status":"UP"}`

---

## Step 3 — Happy Path Test (Full Booking Flow)

### 3.1 Create a Booking via API Gateway

```bash
curl -X POST http://localhost:8765/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-001",
    "movieId": 42,
    "seatsBooked": ["A1", "A2"],
    "showDate": "2025-12-25",
    "showTime": "18:30:00",
    "bookingAmount": 25.00
  }'
```

**Expected Response:**
```json
{
  "bookingId": "some-uuid-here",
  "userId": "user-001",
  "movieId": 42,
  "seatsBooked": ["A1", "A2"],
  "showDate": "2025-12-25",
  "showTime": "18:30:00",
  "bookingStatus": "PENDING",
  "bookingAmount": 25.00
}
```

The booking is saved as `PENDING` and a Kafka message is published to `payment-request`.

### 3.2 What Happens Asynchronously

1. **Payment Service** picks up the message from `payment-request` topic
2. Calls **Stripe** with `tok_visa` test token (auto-succeeds in test mode)
3. Sets status → `CONFIRMED`, saves PaymentEntity as `APPROVED`
4. Publishes result to `payment-response` Kafka topic
5. **Booking Service** listener updates the booking record to `CONFIRMED`

---

## Step 4 — Validate Database Records

### Connect to MySQL
```bash
docker exec -it mysql mysql -uroot -proot
```

### Check Booking Record
```sql
USE bookings;
SELECT booking_id, user_id, movie_id, booking_status, booking_amount FROM bookings;
```
After a few seconds the status should change from `PENDING` → `CONFIRMED`.

### Check Payment Record
```sql
USE payments;
SELECT payment_id, booking_id, payment_status, payment_amount, created_time FROM payments;
```
Should show `APPROVED` status.

---

## Step 5 — Validation Tests

### 5.1 Missing Required Fields (should return 400)
```bash
curl -X POST http://localhost:8765/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 42
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "code": "Bad Request",
  "errorMessage": [
    "UserId is mendatory and it cannot be blank",
    "user must have booked at least 1 seat",
    "show date Is mandatory",
    "show time Is mandatory",
    "booking amount is mandatory"
  ]
}
```

### 5.2 Negative Amount (should return 400)
```bash
curl -X POST http://localhost:8765/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-002",
    "movieId": 1,
    "seatsBooked": ["B1"],
    "showDate": "2025-12-25",
    "showTime": "20:00:00",
    "bookingAmount": -10.00
  }'
```
**Expected:** 400 — `amount must be greater than zero`

---

## Step 6 — Circuit Breaker Test

### 6.1 Stop the Booking Service
```bash
docker stop booking-service
```

### 6.2 Hit the Gateway
```bash
curl http://localhost:8765/bookings
```

**Expected Response (fallback):**
```
Booking Service is in maintenance mode. Please try after some time
```

### 6.3 Restore
```bash
docker start booking-service
```

---

## Step 7 — Direct Payment Service Test (REST fallback)

You can also call payment-service directly (bypasses Kafka):
```bash
curl -X POST http://localhost:8081/payments \
  -H "Content-Type: application/json" \
  -d '{
    "bookingId": "00000000-0000-0000-0000-000000000001",
    "userId": "user-001",
    "movieId": 1,
    "seatsBooked": ["C1"],
    "showDate": "2025-12-25",
    "showTime": "19:00:00",
    "bookingAmount": 15.00
  }'
```

---

## Step 8 — View Kafka Topics (Optional)

```bash
# List topics
docker exec kafka-broker-1 kafka-topics --bootstrap-server localhost:9092 --list

# Watch payment-request messages live
docker exec kafka-broker-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic payment-request \
  --from-beginning

# Watch payment-response messages live
docker exec kafka-broker-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic payment-response \
  --from-beginning
```

---

## Step 9 — Shutdown

```bash
docker compose down            # stop containers
docker compose down -v         # stop + delete volumes (wipes DB data)
```

---
