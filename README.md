# Maersk Booking User Story: Cargo Container Booking-service – Full Stack (Java 17above + React + Spring WebFlux + MongoDB) 

## Developed By: Bandi Chandrasekhar Reddy | Mob: +91 9880318877 | Email: chandrabandir@gmail.com
Implemented the **Coding Task** user story: two endpoints to (1) check availability (proxy to external) and (2) create a booking and store in MongoDB. Built with Java 17, Spring Boot 3 (WebFlux), MongoDB (reactive), React, Docker, K8s, and CI starters.

## Endpoints (secured via API Key)
- `POST /api/bookings/availability` ➜ `{ "available": true|false }`
- `POST /api/bookings` ➜ `{ "bookingRef": "95700000x" }`

**Security:** API Key required on `/api/**` when enabled. Send header: `X-API-KEY: dev-key` (configurable in `application.yml` under `security.api-key.*`).  
**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

## How to run (Docker Compose)
```bash
docker compose up --build
```
Services:
- MongoDB on `localhost:27017`
- WireMock stubbing external endpoint at `POST /api/bookings/checkAvailable` returning `{"availableSpace":6}`
- Backend on `http://localhost:8080`
- Frontend on `http://localhost:3000`

The backend calls external `EXTERNAL_BASE_URL + /api/bookings/checkAvailable` with the request payload and interprets `availableSpace` per the spec in the **Coding Task**. If `availableSpace==0` ➜ `available=false`, otherwise `true`.

## Local dev (Backend)
```bash
cd backend
mvn spring-boot:run
```
**Config:** `src/main/resources/application.yml`  
- `MONGODB_URI` (default: `mongodb://mongo:27017/maersk_bookings`)  
- `EXTERNAL_BASE_URL` (default: `http://wiremock:8080`)  

## Run tests + coverage
```bash
cd backend
mvn verify   # generates Jacoco report at target/site/jacoco/index.html
```

## React UI
```bash
cd frontend
npm install
npm run dev # UI at http://localhost:3000
```
Environment:
- `VITE_API_BASE` (default `http://localhost:8080`)
- `VITE_API_KEY` (default `dev-key`)

## Security
- WebFlux `SecurityWebFilterChain` keeps paths open; a high-priority `WebFilter` enforces API key.
- Configure in `application.yml`:
```yaml
security:
  api-key:
    enabled: true
    header: X-API-KEY
    value: dev-key
```
Set `enabled: false` for local dev if you want to bypass.

## Kubernetes (starter)
Manifests under `k8s/`. Update images (`your-registry/...`) and apply:
```bash
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
```

## CI/CD Starters
- **GitHub Actions (AWS ECR push)**: `.github/workflows/aws-ci.yml`
- **Azure Pipelines**: `azure-pipelines.yml` for Maven build and Docker image publish (adjust service connections).

## Postman
Import `postman/Maersk-Booking.postman_collection.json`. Variables:
- `baseUrl` (default `http://localhost:8080`)
- `api_key` (default `dev-key`)

## Test URLs
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Availability (curl):
```bash
curl -H "X-API-KEY: dev-key" -H "Content-Type: application/json"   -d '{"containerType":"DRY","containerSize":20,"origin":"Southampton","destination":"Singapore","quantity":5}'   http://localhost:8080/api/bookings/availability
```
- Booking:
```bash
curl -H "X-API-KEY: dev-key" -H "Content-Type: application/json"   -d '{"containerType":"DRY","containerSize":20,"origin":"Southampton","destination":"Singapore","quantity":5,"timestamp":"2020-10-12T13:53:09Z"}'   http://localhost:8080/api/bookings
```
