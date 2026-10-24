# CLAUDE.md

Guidance for AI assistants working in this repo.

## Project

`livechat` — a Spring Boot 2.2.5 (Java 8) REST + WebSocket demo. It combines JWT-based
auth/role management, an Indonesian administrative-region lookup (province → regency →
district), a simple task/todo API, and a STOMP over SockJS live chat. Despite the name, the
bulk of the code is auth + region + task CRUD; the chat is a small demo.

- Group/artifact: `com.example.ryanazrian:livechat`, main class `LivechatApplication`.
- Package root: `com.example.ryanazrian.livechat`.

## Stack

- **Build**: Maven via the included wrapper (`./mvnw`, `mvnw.cmd`). Local `.m2/` repo is
  committed/volume-mounted. `pom.xml` pins Spring Boot 2.2.5, Java 1.8.
- **Web/REST**: `spring-boot-starter-web`, Actuator.
- **Persistence**: Spring Data JPA → **MySQL** (`mysql-connector-java`). Dialect set to
  `MariaDBDialect` in `application.properties`, `ddl-auto=update`.
- **Cache**: Spring Data Redis + Jedis (`RedisConfig`). Used in only one place (see Caching).
- **Auth**: Spring Security + JWT (`io.jsonwebtoken:jjwt:0.9.1`), BCrypt passwords, stateless.
- **Realtime**: `spring-websocket` + `spring-messaging` (STOMP).
- **Other**: Lombok (declared but mostly unused — models are hand-written POJOs).

## Run / build

```bash
./mvnw spring-boot:run          # dev, expects MySQL + Redis on localhost
./mvnw clean package            # builds target/*.jar
docker compose up --build       # full stack (see below)
```

Default port `8080` (mapped to `9090` in docker-compose). Static demo chat page:
`http://localhost:8080/` (`src/main/resources/static/index.html`).

`docker-compose.yaml` services: `api_service` (9090→8080), `mysqldb` (3306, db `liveChat2`,
user/pass `root`/`root`), `phpmyadmin` (9292→80), `redis-spring` (6379). The API overrides
`spring.datasource.url` and `spring.redis.host` via env vars.

## Configuration (`src/main/resources/application.properties`)

- DB: `jdbc:mysql://localhost:3306/liveChat2`, root/root.
- JWT: `bezkoder.app.jwtSecret` (`beKoderSecretKey`), `bezkoder.app.jwtExpirationMs`
  (`86400000` = 24h). These are read by `JwtUtils` — keep the `bezkoder.app.*` prefix.
- Redis: `spring.redis.host`/`port` (defaults localhost:6379 in `RedisConfig`).

## Package layout

```
controllers/   REST + STOMP controllers (thin, talk directly to repositories/services)
model/         JPA entities + enums (User, Role, Province, Regency, District, Task, Message)
payload/       DTOs: chat/, region/, request/, response/
repository/    Spring Data JPA interfaces
security/      WebSecurityConfig + jwt/ (JwtUtils, AuthTokenFilter, AuthEntryPointJwt)
services/      business logic + RedisConfig + WebSocketConfig + UserDetails*
resources/     application.properties, static/ (demo chat page)
```

## Data model

- **User** (`users`, id Long): username/email unique; `@ManyToMany` `roles` via `user_roles`;
  `@ManyToOne` `province` via `user_provinces` join table. Password BCrypt-hashed.
- **Role** (`roles`): `name` is `ERole` enum stored as STRING: `ROLE_USER`, `ROLE_MODERATOR`,
  `ROLE_ADMIN`.
- **Task** (`task`): PK is `UUID` stored as `BINARY(16)`; `status` is `EStatus` enum as STRING
  (`TODO`/`IN_PROGRESS`/`DONE`); `@ManyToOne` owner `user` (JsonIgnore).
- **Province** (`provinces`) / **Regency** (`regencies`) / **District** (`districts`):
  Indonesian regions. Regency → Province is a **plain `province_id` column**, NOT a JPA relation
  (the `@ManyToOne` is commented out). District → Regency IS a `@ManyToOne`. The
  Province→Regencies `@OneToMany` is also commented out; relations are assembled manually in
  services/DTOs.

## API endpoints

Public (`/api/auth/**`, `/api/test/**`):
- `POST /api/auth/signup` — create user. Body `SignupRequest` (username, email, password,
  optional `role` set of `"admin"|"mod"|default user`, `provinceId`). Requires the target
  Province row to exist.
- `POST /api/auth/signin` — returns `JwtResponse` (token, id, username, email, roles).
- `GET /api/test/{all|user|mod|admin}` — role demo endpoints.

Authenticated (Bearer token; most `@PreAuthorize` any of USER/MODERATOR/ADMIN):
- `GET /api/auth/getAllUser` — all users except caller.
- `GET /api/user/details` — caller's own profile (parses Bearer token).
- `GET /api/task/allTask`, `GET /api/task/taskById?id=<uuid>`
- `POST /api/task/addTask` — owner derived from Bearer token; status defaults TODO.
- `PATCH /api/task/editTask` — partial update (title/description/status).
- `GET /api/region/allProvince`, `/allProvincePagination?offset&pageSize&sortBy`
- `GET /api/region/allRegency`, `/provinceById/{id}`, `/regencyByProvinceId/{provinceId}`
- `GET /api/region/provinceRegency`, `/provinceRegency/{id}`

## WebSocket / chat

`WebSocketConfig`: app prefix `/app`; simple brokers `/topic` and `/topic2`; STOMP endpoints
`/chat` and `/chatTopic` (both also `.withSockJS()`).

`MessageController`:
- `/app/chat` → broadcast `/topic/messages`
- `/app/topic2/{room}` → broadcast `/topic2/{room}`

`index.html` demo connects to `/chatTopic`, subscribes `/topic2/1`, sends `/app/topic2/1`.
Note: `static/app.js` is an **orphan** (references `/gs-guide-websocket`, `/app/hello`,
jQuery/`$`) — not wired to this backend. Chat messages are **not persisted** (in-memory
broadcast only).

## Security flow

Stateless. `AuthTokenFilter` reads `Authorization: Bearer <jwt>`, validates via `JwtUtils`,
loads `UserDetailsImpl` (built from `User.roles` → `SimpleGrantedAuthority`), sets the
SecurityContext. `WebSecurityConfig` permits `/api/auth/**` and `/api/test/**`, requires auth
otherwise, disables CSRF, uses `BCryptPasswordEncoder`. Several controllers also manually
re-parse the Bearer token with `JwtUtils` (e.g. addTask, user/details) instead of relying on
the SecurityContext.

## Caching (Redis)

Only `ProvinceService.getProvinceById(id)` uses Redis: key `GET_PROVINCE_BY_ID<id>`, TTL 5 min,
JSON-serialized `RedisTemplate<String,Object>`. Cached values deserialize to `LinkedHashMap`,
so the code manually rebuilds `ProvinceResponse`. All other services hit MySQL directly.

## Gotchas for AI edits

- **Roles are not seeded.** `signup` throws `RuntimeException("Role is not found")` unless
  `ROLE_USER`/`ROLE_MODERATOR`/`ROLE_ADMIN` rows already exist in `roles`. Same for Provinces
  on signup. Provide seed SQL / data loader.
- `ddl-auto=update` creates schema but **not** region data; `provinces`/`regencies`/`districts`
  need manual import for region endpoints to return anything.
- Region relations are intentionally flattened (province_id as a column) — don't assume
  `province.getRegencies()` exists; it's commented out.
- Old Spring Security API (`WebSecurityConfigurerAdapter`, `antMatchers`) — do NOT "modernize"
  to the 2.7+/3.x `SecurityFilterChain` style unless also bumping Spring Boot.
- Java 8 only: no `var`, no newer JDK APIs.
- No real tests beyond `LivechatApplicationTests.contextLoads` (needs MySQL+Redis to load
  context). Run `./mvnw test`.
- CORS is wide open (`@CrossOrigin(origins = "*")`) on most controllers.
