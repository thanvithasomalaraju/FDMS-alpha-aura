// Example frontend helper for presigned upload flow (uses fetch). Place on your delivery form page.

async function presignAndUpload(file) {
  // Request a presigned URL from the backend
  const res = await fetch('/api/storage/presign', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ filename: file.name, contentType: file.type })
  });
  const json = await res.json();
  if (!res.ok || !json.success) throw new Error('Failed to get presigned URL');

  const uploadUrl = json.uploadUrl;
  const key = json.key;

  // Upload the file directly to S3 with PUT
  const upRes = await fetch(uploadUrl, {
    method: 'PUT',
    headers: { 'Content-Type': file.type },
    body: file
  });
  if (!upRes.ok) throw new Error('Upload failed: ' + upRes.status);

  return key; // save this key with the application (send to backend apply as a form field)
}

// Example usage: when submitting the delivery application form, for each file input call presignAndUpload(file)
// Then POST the rest of the form to /api/delivery/partners/apply with fields like photoKey, licenseKey, etc. You should
// update the apply endpoint to accept keys instead of MultipartFile if you fully adopt presigned flow. The current
// backend still accepts MultipartFile uploads, so this helper is optional until backend is updated.
