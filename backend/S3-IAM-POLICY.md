S3 migration and IAM guidance

This document contains recommended IAM policy, S3 CORS config, and environment variable examples for running the presigned upload flow used by the delivery backend.

Summary
- Backend issues presigned PUT URLs for uploads and presigned GET URLs for admin downloads.
- The backend uses the AWS SDK's default credential provider chain; prefer an instance role in production.
- Ensure the S3 bucket is private and use short-lived presigned URLs for access.

Required environment variables (example)
- AWS_REGION=us-east-1
- S3_BUCKET=your-company-madfood-uploads
- S3_PREFIX=delivery-apps    # optional, e.g. a folder/prefix inside the bucket

Credentials
- Recommended: assign an IAM role to the instance/task with the policy below.
- Alternative for local testing: set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in the environment (DO NOT commit these to source control).

Least-privilege IAM policy (example)
- Replace {your-bucket} and {prefix} with your values. This policy grants only put/get/delete permissions for objects under the configured prefix.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::{your-bucket}/{prefix}/*"
    }
  ]
}
```

If you use presigned PUTs that set ACLs (the code sets private by default), include PutObjectAcl if needed.

S3 bucket CORS (example)
- To allow browser PUTs from your frontend origin(s), add a CORS configuration like this to your bucket. Replace Origins with your frontend origins.

```xml
<CORSConfiguration>
  <CORSRule>
    <AllowedOrigin>https://your-frontend.example.com</AllowedOrigin>
    <AllowedMethod>PUT</AllowedMethod>
    <AllowedMethod>GET</AllowedMethod>
    <AllowedHeader>*</AllowedHeader>
    <MaxAgeSeconds>3000</MaxAgeSeconds>
  </CORSRule>
</CORSConfiguration>
```

Notes and best practices
- Use short presigned durations (the backend issues 15-minute URLs by default).
- Do not allow public read on the bucket. Keep objects private and use presigned GET for admin access.
- For production, prefer an IAM role attached to the compute instance (ECS task role, EC2 instance role, etc.) so you do not store long-term credentials on the host.
- Monitor S3 usage and enable object lifecycle rules to remove old uploads if appropriate.
- Consider signing uploads with additional server-side validation (e.g., store an application record with expected keys prior to presigning) to prevent orphaned objects.

Rollback / fallback
- The backend currently supports a local filesystem fallback (LocalFileStorageService) while you migrate clients. Once all clients use presigned uploads, the local fallback and legacy file-serving endpoints can be removed.

