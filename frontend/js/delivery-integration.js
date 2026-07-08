(function () {
  'use strict';

  /* --- Auth helper (token store + fetchWithAuth) --- */
  const AUTH_KEY = 'mf_token';
  window.authStore = {
    setToken(token) { localStorage.setItem(AUTH_KEY, token); },
    getToken() { return localStorage.getItem(AUTH_KEY); },
    clear() { localStorage.removeItem(AUTH_KEY); },
    isAuthenticated() { return !!localStorage.getItem(AUTH_KEY); }
  };
  window.fetchWithAuth = async (url, opts = {}) => {
    opts = Object.assign({}, opts);
    opts.headers = Object.assign({}, opts.headers || {});
    const token = authStore.getToken();
    if (token) opts.headers['Authorization'] = 'Bearer ' + token;
    const res = await fetch(url, opts);
    if (res.status === 401) {
      authStore.clear();
      alert('Session expired — please log in again.');
      location.reload();
      throw new Error('Unauthorized');
    }
    return res;
  };

  /* --- small toast helper (uses #toast if present) --- */
  function showToast(message) {
    const toast = document.getElementById('toast');
    if (!toast) { alert(message); return; }
    toast.querySelector('.toast-text')?.firstChild && (toast.querySelector('.toast-text').firstChild.textContent = message);
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 5000);
  }

  /* --- Delivery form wiring --- */
  document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('recruitmentForm');
    if (!form) return;

    // Build previews (keeps page UX)
    const FILES_CONFIG = [
      { id: 'photo',   label: 'Profile Photo' },
      { id: 'license', label: 'License'       },
      { id: 'rc',      label: 'RC'            },
      { id: 'aadhar',  label: 'Aadhar'        },
    ];
    const grid = document.getElementById('previewGrid');
    if (grid && grid.children.length === 0) {
      FILES_CONFIG.forEach((item) => {
        const div = document.createElement('div');
        div.className = 'preview-item';
        div.id = `preview-${item.id}`;
        div.innerHTML = `
          <span class="placeholder-icon"><i class="fas fa-cloud-upload-alt"></i></span>
          <img id="img-${item.id}" src="" alt="${item.label}" />
          <span class="file-label">${item.label}</span>
          <span class="badge-uploaded"><i class="fas fa-check-circle"></i> Uploaded</span>
        `;
        grid.appendChild(div);
      });
    }

    FILES_CONFIG.forEach(({ id }) => {
      const input = document.getElementById(id);
      const previewDiv = document.getElementById(`preview-${id}`);
      const img = document.getElementById(`img-${id}`);
      if (!input) return;
      input.addEventListener('change', function () {
        const file = this.files[0];
        if (!file) {
          previewDiv?.classList.remove('has-image');
          img && (img.src = '');
          return;
        }
        const reader = new FileReader();
        reader.onload = function (e) {
          if (img) img.src = e.target.result;
          previewDiv?.classList.add('has-image');
        };
        reader.readAsDataURL(file);
      });
    });

    // Client validation helpers
    const fullNameInput = document.getElementById('fullName');
    fullNameInput?.addEventListener('input', function () {
      this.value = this.value.replace(/[^a-zA-Z\s]/g, '');
      this.value = this.value.replace(/\b\w/g, c => c.toUpperCase());
    });
    const phoneInput = document.getElementById('phone');
    phoneInput?.addEventListener('input', function () {
      this.value = this.value.replace(/\D/g, '').slice(0, 10);
    });

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const name = (document.getElementById('fullName')?.value || '').trim();
      const phone = (document.getElementById('phone')?.value || '').trim();
      const requiredFiles = ['photo', 'license', 'rc', 'aadhar'];

      if (!/^[a-zA-Z\s]+$/.test(name)) {
        alert('❌ Please enter a valid name using only alphabets and spaces.');
        document.getElementById('fullName')?.focus();
        return;
      }
      if (phone.length !== 10) { alert('Please enter a valid 10-digit phone number.'); document.getElementById('phone')?.focus(); return; }

      let allFilesSelected = true;
      requiredFiles.forEach(id => {
        const inp = document.getElementById(id);
        if (!inp || inp.files.length === 0) {
          allFilesSelected = false;
          if (inp) { inp.style.borderColor = '#b23b4a'; inp.style.boxShadow = '0 0 0 4px rgba(178,59,74,0.12)'; }
        } else if (inp) {
          inp.style.borderColor = '#e6d5c4'; inp.style.boxShadow = 'none';
        }
      });
      if (!allFilesSelected) {
        alert('Please upload all required documents: Profile Photo, Driving License, RC, Aadhar');
        return;
      }

      // Build FormData
      const formData = new FormData();
      formData.append('fullName', name);
      formData.append('phone', `+91${phone}`);
      formData.append('source', 'Mad Food Delivery Partner');
      requiredFiles.forEach(id => {
        const inp = document.getElementById(id);
        if (inp && inp.files[0]) formData.append(id, inp.files[0]);
      });

      // UI feedback
      const btn = form.querySelector('.btn-submit');
      if (btn) { btn.disabled = true; btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Submitting...'; }

      try {
        const res = await fetch('/api/delivery/applications', { method: 'POST', body: formData });
        const json = await res.json().catch(()=>({ success:false, message:'Invalid server response' }));
        if (res.ok && json.success) {
          showToast(json.message || 'Application submitted successfully!');
          // Optionally clear the form here
          form.reset();
          document.querySelectorAll('.preview-item.has-image').forEach(el => el.classList.remove('has-image'));
        } else {
          alert(json.message || 'Submission failed');
        }
      } catch (err) {
        alert('Network error: ' + err.message);
      } finally {
        if (btn) { btn.disabled = false; btn.innerHTML = '<i class="fas fa-paper-plane"></i> Submit Application'; }
      }
    });

    // Remove error styling when file changes
    ['photo','license','rc','aadhar'].forEach(id => {
      const el = document.getElementById(id);
      el?.addEventListener('change', () => { el.style.borderColor = '#e6d5c4'; el.style.boxShadow = 'none'; });
    });
  });
})();
