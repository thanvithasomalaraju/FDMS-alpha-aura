# Production hardening checklist and notes

This file lists the small set of production hardening steps applied and remaining tasks before deploying to production.

What this branch (Dimple) already does:
- Requires authentication for non-auth endpoints; delivery application POST is public by design.
- File access endpoint restricted to ROLE_ADMIN.
- Demo users created on startup for convenience (demo/demo123, admin/admin123) — remove or secure before production.
- Rate limiting filter (in-memory) added for basic protection against simple abuse patterns.
- Security headers filter adds X-Frame-Options, X-Content-Type-Options, CSP and Referrer-Policy.
- CORS filter reads allowed origins from environment variable APP_ALLOWED_ORIGINS.
- Multipart upload size limits set in application.properties (5MB per file).
- Basic validation and global exception handler provide structured JSON errors.

Remaining recommended steps before production:
1. Replace JWT secret with a secure secret (set JWT_SECRET env var). Keep it at least 32+ random characters.
2. Replace in-memory rate limiter with a distributed solution (API gateway, Cloud Armor, or Redis-backed token bucket) for multi-instance deployments.
3. Move file storage to S3/GCS and use signed URLs; remove direct file serving from the application.
4. Remove or rotate the dev demo/admin credentials and add a proper admin provisioning process.
5. Configure HTTPS termination (load balancer or reverse proxy) and strict TLS settings.
6. Add monitoring/alerting (Prometheus/Grafana, Sentry for exceptions) and structured request tracing.
7. Add additional input sanitization and strict validation for free-form fields.
8. Harden CORS to only allow known origins and restrict methods if not required.
9. Add CSP rules tailored to your frontend (the current CSP is permissive to avoid breaking dev scripts).
10. Add automated backup and DB migration strategy when switching to a persistent DB.

How to run with environment values (example Docker Compose):
- Set JWT_SECRET, APP_ALLOWED_ORIGINS, SPRING_DATASOURCE_URL (if using MySQL), and FILE_UPLOAD_DIR.

