# livechat-server (PEMDI chat-service, port 3005)

This repo is the standalone Spring Boot "livechat" demo **and** the PEMDI `chat-service`. When
built into PEMDI it runs with `SPRING_PROFILES_ACTIVE=pemdi`. Everything below applies to that
role; the original demo (MySQL + native JWT auth + `/chat` STOMP demo) is preserved unchanged.

## Two parallel auth systems (do not merge them)

- **Native livechat auth** — `security/WebSecurityConfig` + `security/jwt/*` + `/api/auth/**`,
  `/api/task/**`, `/api/region/**`, and the unauthenticated STOMP demo `/chat`, `/chatTopic`.
  HS512 + `beKoderSecretKey`. Untouched by the PEMDI work.
- **PEMDI chat auth** — `security/pemdi/*`. Secures **`/api/chat/**`** (REST) and **`/ws-chat`**
  (STOMP). Verifies the PEMDI access token (HS256, shared `JWT_ACCESS_SECRET`, `type:"access"`)
  then calls `auth-service POST /auth/introspect` (`x-introspect-key=INTROSPECT_SECRET`,
  10s cache, **fail-closed 503**) — same contract as gateway/arsitektur.

`PemdiSecurityConfig` is `@Order(1)` on `/api/chat/**` (+ `@Order(2)` permitting the `/ws-chat`
handshake); native config is lowest precedence and keeps owning everything else.

## WebSocket is a direct connection (gateway bypass)

The NestJS gateway cannot proxy WS upgrades, so the **browser connects straight to this service**
(`/ws-chat`, SockJS, port 3005 published — the one deliberate exception to "gateway = sole entry").
Auth therefore happens per **STOMP CONNECT** in `PemdiStompChannelInterceptor`:
- CONNECT: token from STOMP `Authorization` header or `?token=` query → verify+introspect → set Principal.
- SUBSCRIBE: `/topic/chat/global` = any valid session; `/topic/chat/dm/{roomKey}` = participants only
  (enforced here AND in `ChatService`; STOMP does not authorize subscriptions on its own).
- The interceptor only enforces auth on sessions marked by `PemdiChatHandshakeInterceptor` (the
  `/ws-chat` endpoint), so the native `/chat` demo stays unauthenticated.

## Rooms & persistence
- Rooms: `global` (any authenticated user) and `dm:<smallerId>:<largerId>` (symmetric key).
- Send: `/app/chat.send {roomKey|toUserId, content}` → `ChatService.send` → persist Postgres →
  `LPUSH`+`LTRIM chat:recent:<room>` (cap `CHAT_HISTORY_CACHE_SIZE`) → broadcast via SimpMessagingTemplate.
- History/rooms/DM REST: `ChatController` under `/api/chat/**` (reached via gateway `chat-proxy`,
  forwards `Authorization`). Redis recent-window first, Postgres backfill on miss.
- Entity `ChatMessage` → table `chat_message` in the **`pemdi_chat`** Postgres DB.

## Database (profile switch — MySQL never deleted, never dialled under pemdi)
- `application.properties` = standalone MySQL (`MariaDBDialect`). **Leave alone.**
- `application-pemdi.properties` = Postgres `pemdi_chat`, `ddl-auto=update`, Redis + PEMDI keys.
  Selected by `SPRING_PROFILES_ACTIVE=pemdi`. Reverting to standalone = drop that env var.
- `pemdi_chat` is created by `docker/postgres-core/init/00-create-chat-db.sh` (runs on first volume).

## Health
`GET /actuator/health` (permitAll). Used by the compose healthcheck.

## Build / run
```bash
# standalone demo (MySQL):   ./mvnw spring-boot:run
# PEMDI service (Postgres):  SPRING_PROFILES_ACTIVE=pemdi ./mvnw spring-boot:run
# via PEMDI compose:         docker compose ... up -d chat-service   (built from ./livechat-server)
# NOTE: build needs JDK 8 (lombok fails on newer JDKs). Local: export JAVA_HOME=.../corretto-1.8.0_412
```

## Gotchas
- **STOMP simple broker is in-memory → run 1 replica only.** Multi-instance needs Redis pub/sub
  or a RabbitMQ STOMP relay (`enableStompBrokerRelay`). Not built.
- Chat history is durable (Postgres) + a capped Redis recent-window; Redis is best-effort (cache
  errors are swallowed, Postgres is source of truth).
- `pemdi.jwt.access-secret` and `INTROSPECT_SECRET` must match auth-service/gateway or all chat
  routes return 401/503.
- The repo's own README/CLAUDE.md still describe the standalone demo; this file is the PEMDI truth.
