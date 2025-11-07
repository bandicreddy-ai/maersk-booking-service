# Maersk Booking – Full Stack (React + Spring WebFlux + MongoDB) with Logging & Security

## Stack
- Backend: Java 17, Spring Boot 3 (WebFlux, Reactive Mongo), OpenAPI, validation
- Frontend: React + Vite
- Security: API key header (`X-API-KEY`), enabled by default
- External dependency: WireMock simulating `availableSpace`
- Docker & K8s manifests
- CI: GitHub Actions (AWS ECR), Azure Pipelines
- Logging: Logback (ISO timestamps), MDC correlation ID, rolling files

## Quick Start (local)
```bash
docker compose up --build
```
- Frontend: http://localhost:3000
- Backend:  http://localhost:8080 (Swagger at /swagger-ui.html)
- Mongo:    mongodb://localhost:27017/maersk_booking
- WireMock: http://localhost:9090
- API key:  header `X-API-KEY: dev-secret`

## WireMock stubs
Create "available" mapping:
```bash
curl -X POST http://localhost:9090/__admin/mappings   -H "Content-Type: application/json"   -d '{"request":{"method":"POST","url":"/api/bookings/checkAvailable"},"response":{"status":200,"headers":{"Content-Type":"application/json"},"body":"{\"availableSpace\": 6}"}}'
```
Change to unavailable:
```bash
curl -X POST http://localhost:9090/__admin/mappings   -H "Content-Type: application/json"   -d '{"request":{"method":"POST","url":"/api/bookings/checkAvailable"},"response":{"status":200,"headers":{"Content-Type":"application/json"},"body":"{\"availableSpace\": 0}"}}'
```

## Endpoints
- `POST /api/bookings/availability` → `{ "available": true|false }`
- `POST /api/bookings` → `{ "bookingRef": "957000001" }`

## Postman
Import `postman_collection.json` and send.

## Build backend only
```bash
cd backend
mvn clean package
java -jar target/booking-api-1.1.0.jar
```

## K8s
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/mongo.yaml
kubectl apply -f k8s/wiremock.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
```

## Notes
- Security **enabled** by default. Disable with `security.api-key.enabled=false` if needed.
- Logs are in `backend/logs/app.log` with daily rotation.
