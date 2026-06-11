# EduGuard Backend (PHP)

A dependency-free PHP backend implementing the REST API that the EduGuard
Android client (`app/src/main/java/com/example/eduguard/data/remote/ApiService.java`)
expects. Uses PDO + SQLite and a hand-rolled HS256 JWT — no Composer, no
frameworks, just PHP 8.1+.

## Requirements

- PHP 8.1 or newer with the `pdo_sqlite` extension (bundled with most PHP builds).

## Quick start

```bash
cd backend

# 1. Create the SQLite database and seed demo accounts
php migrate.php

# 2. Start the server on port 5000 (the port the Android app targets)
php -S 0.0.0.0:5000 -t public
```

The app's `Constants.BASE_URL` must point at this server:

| Client                          | BASE_URL                     |
|---------------------------------|------------------------------|
| Android **emulator** → host     | `http://10.0.2.2:5000/`      |
| Physical device on same Wi-Fi   | `http://<your-LAN-IP>:5000/` |

`0.0.0.0` makes the server reachable from a physical device; `<your-LAN-IP>` is
your computer's address (e.g. `192.168.2.10`, which is what `Constants.java`
currently uses).

## Demo accounts

| Role    | Email                   | Password    |
|---------|-------------------------|-------------|
| Admin   | `admin@eduguard.test`   | `admin123`  |
| Mentor  | `mentor@eduguard.test`  | `mentor123` |
| Student | `student@eduguard.test` | `student123`|

The student is pre-assigned to the mentor and ships with a starter policy.

## API

All routes are relative to `BASE_URL`. Every endpoint except `auth/login`
requires an `Authorization: Bearer <token>` header.

| Method | Path                   | Role            | Description                          |
|--------|------------------------|-----------------|--------------------------------------|
| POST   | `auth/login`           | public          | `{email,password}` → `{token,role,userId,name}` |
| GET    | `users/me`             | any             | Current user profile                 |
| GET    | `users/students`       | admin, mentor   | All students (mentor: only assigned) |
| GET    | `users/mentors`        | admin           | All mentors                          |
| POST   | `users`                | admin           | Create a user                        |
| PATCH  | `users/{userId}`       | admin           | Update a user (partial)              |
| DELETE | `users/{userId}`       | admin           | Delete a user                        |
| GET    | `policies/{studentId}` | any             | Student policy (default if unset)    |
| POST   | `policies/{studentId}` | admin, mentor   | Create/replace a policy              |
| POST   | `requests`             | student         | Submit an extra-time/emergency request |
| GET    | `requests`             | any             | Requests (role-filtered)             |
| PATCH  | `requests/{id}`        | admin, mentor   | `{status}` → approve/reject          |
| POST   | `logs`                 | any             | Upload daily app-usage log           |
| GET    | `logs/{studentId}`     | any             | Usage logs for a student             |

JSON shapes match the client model classes (`_id`, `assignedMentor`,
`allowedApps`, `sleepMode`, etc.).

## Project layout

```
backend/
├── config/config.php          # JWT secret, TTL, DB path (env-overridable)
├── schema.sql                 # SQLite schema
├── migrate.php                # Create DB + seed demo data
├── routes.php                 # Route table (mirrors ApiService.java)
├── public/
│   ├── index.php              # Front controller (CORS, dispatch, error handling)
│   └── .htaccess              # Apache rewrite (not needed for `php -S`)
├── storage/                   # SQLite file lives here (gitignored)
└── src/
    ├── bootstrap.php          # PSR-4 autoloader (no Composer needed)
    ├── Core/                  # Router, Request, Response, Database, Jwt, HttpException
    ├── Middleware/            # AuthMiddleware (token + role guards)
    ├── Support/               # Id generator (Mongo-style 24-hex _id)
    ├── Repositories/          # DB access + API mapping per entity
    └── Controllers/           # One per resource (Auth, User, Policy, Request, Log)
```

## Configuration

Override defaults with environment variables:

```bash
EDUGUARD_JWT_SECRET="a-long-random-string" \
EDUGUARD_JWT_TTL=86400 \
EDUGUARD_DB_PATH=/var/lib/eduguard.sqlite \
php -S 0.0.0.0:5000 -t public
```

## ⚠️ Known client bug (policy endpoints)

In the Android client, `RequestRepository.fetchPolicy()` and
`PolicyRepository.updatePolicy()` call the Retrofit methods with the arguments
**swapped** — e.g. `api.getPolicy(studentId, bearer)` against the signature
`getPolicy(@Header token, @Path studentId)`. As written, the app sends the
student id as the `Authorization` header and `"Bearer <token>"` as the URL path
segment, so the policy calls will hit this backend with a broken path/header and
fail auth.

This backend implements the **correct** REST contract. To make the policy
screens work, fix the call sites in the app to pass `(bearer, studentId, ...)`
in the order the `ApiService` interface declares.
