Production checklist — S3 & presigned uploads additions

I added S3 presigned-upload support to the backend and a small frontend helper. Before promoting to staging/production, complete these steps:

1) Set the following environment variables on the server/runtime (examples):
   - AWS_REGION=us-east-1
   - S3_BUCKET=your-company-madfood-uploads
   - S3_PREFIX=delivery-apps   # optional
   - JWT_SECRET=<secure-32+char-secret>
   - Do not set AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY in git or code. Use environment secrets or instance IAM roles.

2) Create an IAM role with the least-privilege policy in backend/S3-IAM-POLICY.md. Attach it to your compute environment (ECS task role, EC2 instance profile, etc.).

3) Configure S3 CORS so browser-based PUT requests succeed (see backend/S3-IAM-POLICY.md for example CORS config).

4) Ensure the S3 bucket is private. Use presigned GETs for admin downloads rather than public object URLs.

5) Rotate and store JWT_SECRET securely in your CI/CD or secrets manager and ensure it's injected into the runtime environment.

6) Remove demo/admin user creation in non-development environments. The compose file enables dev users for convenience only.

7) Consider lifecycle rules for uploaded objects and an audit policy for who requested presigned URLs.

8) After all clients are migrated to presigned uploads and you have verified saved keys are valid, remove the LocalFileStorageService and the legacy GET /api/delivery/partners/files/{filename} endpoint to simplify the stack.

If you want, I can:
- Add an example AWS IAM policy document to the console for immediate copy/paste (already added as S3-IAM-POLICY.md).
- Add a small integration test that requests a presign URL and validates the returned fields (no actual S3 PUT) as part of CI.

What I will do next if you say "continue"
- Add the IAM policy to PRODUCTION-README.md (done) — if you want I will also add a small integration test and a GitHub Actions workflow to run it.
