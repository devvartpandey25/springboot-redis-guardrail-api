 🚀 Springboot-redis-guardrail-api
 
 📌 Overview
This project is a Spring Boot microservice for handling posts, comments, and user/bot interactions. Redis is used to manage concurrency, enforce limits, and keep the system stateless.

🛠️ Tech Stack

* Java 17
* Spring Boot
* PostgreSQL
* Redis

🔥 Approach

PostgreSQL acts as the primary database, while Redis is used for real-time operations such as counters, guardrails, and validation checks.

🔒 Thread Safety & Atomic Locks 

To handle concurrent requests safely, all shared state is managed in Redis instead of application memory.

Redis provides atomic operations like `INCR` and `SETNX`, which ensures that multiple requests can safely update shared values without conflicts.

* Horizontal Cap:
  A counter (`post:{id}:bot_count`) is incremented using `INCR` for every bot reply.
  If the value exceeds 100, the request is rejected and the counter is rolled back.
  Since `INCR` is atomic, the limit is enforced correctly even under high concurrency.

* Cooldown Control:
  Implemented using `SETNX` with a 10-minute TTL (`cooldown:bot_{botId}:human_{userId}`).
  This ensures only one interaction is allowed within the time window.

* Virality Score:
  Updated using atomic increments, so no updates are lost even when multiple interactions happen simultaneously.

Overall, using Redis for shared state keeps the application stateless and avoids the need for explicit locking mechanisms.

🔔 Notifications

To avoid notification spam:

* If no recent notification → send immediately
* Otherwise → store in Redis for batching

 ⏱ Scheduled Tasks

A scheduled job runs every 5 minutes to process and summarize pending notifications.

▶️ How to Run

1. Start services:
   docker-compose up -d
   
2. Run the Spring Boot application
3. Test APIs using Postman
