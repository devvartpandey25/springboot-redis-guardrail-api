
# Grid07 Assignment - API Gateway

Spring Boot 3.x (Java 17) microservice with PostgreSQL + Redis.

## Guardrails
- Virality score stored in Redis
- Bot replies capped at 100/post
- Comment depth max 20
- Cooldown: 10 min interaction
- Notification batching via Redis list

## Run
docker-compose up
./mvnw spring-boot:run
