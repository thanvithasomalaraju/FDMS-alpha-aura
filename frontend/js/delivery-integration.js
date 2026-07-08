(function () {
  'use strict';

  // Delivery partner form integration
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

    const formData = new FormData();
    formData.append('fullName', name);
    formData.append('phone', `+91${phone}`);
    formData.append('source', 'Mad Food Delivery Partner');

    fileInputs.forEach((id) => {
      const inp = document.getElementById(id);
      if (inp && inp.files.length > 0) {
        formData.append(id, inp.files[0]);
      }
    });

    // Set UI submitting state
    setSubmitting(true);

    try {
      // NOTE: backend endpoint is a placeholder. Implement a Spring Boot endpoint:
      // POST /api/delivery/partners/apply  (multipart/form-data)
      const res = await window.ApiClient.postForm('/delivery/partners/apply', formData);

      // Expecting JSON response with { success: true, message: '...' }
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
