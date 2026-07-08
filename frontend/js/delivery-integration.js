(function () {
  'use strict';

  // Delivery partner form integration (now using S3 presigned uploads when files are present)
  const form = document.getElementById('recruitmentForm');
  const toast = document.getElementById('toast');
  const submitBtn = document.querySelector('.btn-submit');

  if (!form) {
    console.warn('Delivery integration: recruitmentForm not found on page');
    return;
  }

  function showToast(message, submessage) {
    if (!toast) return alert(message + (submessage ? '\n' + submessage : ''));
    const textEl = toast.querySelector('.toast-text');
    if (textEl) {
      textEl.innerHTML = `${message}<small>${submessage || ''}</small>`;
    }
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 6000);
  }

  function setSubmitting(isSubmitting) {
    if (!submitBtn) return;
    submitBtn.disabled = isSubmitting;
    if (isSubmitting) {
      submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...';
    } else {
      submitBtn.innerHTML = '<i class="fas fa-paper-plane"></i> Submit Application';
    }
  }

  form.addEventListener('submit', async function (e) {
    e.preventDefault();

    const name = document.getElementById('fullName').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const fileInputs = ['photo', 'license', 'rc', 'aadhar'];

    if (!/^[a-zA-Z\s]+$/.test(name)) {
      alert('❌ Please enter a valid name using only alphabets and spaces.');
      document.getElementById('fullName').focus();
      return;
    }

    if (phone.length !== 10) {
      alert('Please enter a valid 10-digit phone number.');
      document.getElementById('phone').focus();
      return;
    }

    let allFilesSelected = true;
    fileInputs.forEach((id) => {
      const inp = document.getElementById(id);
      if (!inp || inp.files.length === 0) {
        allFilesSelected = false;
        if (inp) {
          inp.style.borderColor = '#b23b4a';
          inp.style.boxShadow = '0 0 0 4px rgba(178,59,74,0.12)';
        }
      } else {
        if (inp) {
          inp.style.borderColor = '#e6d5c4';
          inp.style.boxShadow = 'none';
        }
      }
    });

    if (!allFilesSelected) {
      alert('Please upload all required documents:\n• Profile Photo\n• Driving License\n• RC Certificate\n• Aadhar Card');
      return;
    }

    // Prepare submission formData: we'll include keys (photoKey, licenseKey, ...) obtained via presigned uploads
    const formData = new FormData();
    formData.append('fullName', name);
    formData.append('phone', `+91${phone}`);
    formData.append('source', 'Mad Food Delivery Partner');

    setSubmitting(true);

    try {
      // Upload files directly to S3 using presign helper (presignAndUpload must be available on the page)
      // If presign fails or is not configured, fall back to including the MultipartFile in the request (backend supports both)
      for (const id of fileInputs) {
        const inp = document.getElementById(id);
        if (!inp || inp.files.length === 0) continue;
        const file = inp.files[0];
        try {
          if (typeof presignAndUpload === 'function') {
            const key = await presignAndUpload(file);
            // Append the key field expected by the backend
            formData.append(`${id}Key`, key);
          } else {
            // No presign helper available; append file directly
            formData.append(id, file);
          }
        } catch (uploadErr) {
          console.warn('Presign/upload failed for', id, uploadErr);
          // fallback: include the file directly in multipart
          formData.append(id, file);
        }
      }

      // Submit the form (backend accepts either keys or files)
      const res = await window.ApiClient.postForm('/delivery/partners/apply', formData);

      showToast('Application Submitted Successfully! 🎉', (res && res.message) || 'Mad Food team will review your application within 24 hours');

      // reset form UI previews
      fileInputs.forEach((id) => {
        const previewDiv = document.getElementById(`preview-${id}`);
        const img = document.getElementById(`img-${id}`);
        if (previewDiv) previewDiv.classList.remove('has-image');
        if (img) img.src = '';
        const inputEl = document.getElementById(id);
        if (inputEl) inputEl.value = '';
      });

    } catch (err) {
      console.error('Delivery application error', err);
      let message = 'Failed to submit application.';
      let details = '';
      if (err && err.body) {
        try {
          const body = typeof err.body === 'string' ? JSON.parse(err.body) : err.body;
          if (body && body.message) message = body.message;
          else details = JSON.stringify(body);
        } catch (parseErr) {
          details = err.body;
        }
      }
      showToast('Submission failed', message + (details ? ' — ' + details : ''));
    } finally {
      setSubmitting(false);
    }
  });

  // Remove error styling on file change
  ['photo', 'license', 'rc', 'aadhar'].forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;
    el.addEventListener('change', function () {
      this.style.borderColor = '#e6d5c4';
      this.style.boxShadow = 'none';
    });
  });

})();
