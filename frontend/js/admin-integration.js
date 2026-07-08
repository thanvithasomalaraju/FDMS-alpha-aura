(function () {
  'use strict';

  /* --- Auth helper --- */
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

  function showToastText(message) {
    const t = document.querySelector('.toast');
    if (!t) { alert(message); return; }
    t.textContent = message;
    t.classList.add('active');
    setTimeout(()=>t.classList.remove('active'), 4000);
  }

  document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('loginBtn');
    if (loginBtn) {
      loginBtn.addEventListener('click', async () => {
        const email = (document.getElementById('loginEmail')?.value || '').trim();
        const password = (document.getElementById('loginPassword')?.value || '');
        if (!email || !password) { alert('Please provide email and password'); return; }
        try {
          const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
          });
          const json = await res.json().catch(()=>({ success:false, message:'Invalid response' }));
          if (res.ok && json.success) {
            authStore.setToken(json.data.token);
            document.getElementById('loginScreen')?.classList.add('hidden');
            document.getElementById('appShell')?.classList.remove('hidden');
            // load dashboard
            const dashRes = await fetchWithAuth('/api/admin/dashboard');
            const dashJson = await dashRes.json().catch(()=>null);
            if (dashJson && dashJson.success) {
              populateDashboard(dashJson.data);
            }
          } else {
            alert(json.message || 'Login failed');
          }
        } catch (err) {
          alert('Network error: ' + err.message);
        }
      });
    }

    // Nav behavior already in HTML/CSS; attach click delegation for admin actions
    document.getElementById('appShell')?.addEventListener('click', async (e) => {
      const el = e.target;
      // Approve registration button (example pattern): data-approve-id attribute on button
      const approveBtn = el.closest('[data-approve-id]');
      if (approveBtn) {
        const regId = approveBtn.getAttribute('data-approve-id');
        try {
          const res = await fetchWithAuth(`/api/admin/registrations/${regId}/approve`, { method: 'PUT' });
          const json = await res.json();
          if (res.ok && json.success) {
            showToastText('Registration approved');
            // remove/refresh row
          } else alert(json.message || 'Approve failed');
        } catch (err) { alert(err.message); }
      }
      // Block user example: data-user-block-id and data-status
      const blockBtn = el.closest('[data-user-block-id]');
      if (blockBtn) {
        const uid = blockBtn.getAttribute('data-user-block-id');
        const newStatus = blockBtn.getAttribute('data-status') || 'BLOCKED';
        try {
          const res = await fetchWithAuth(`/api/admin/users/${uid}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: newStatus })
          });
          const json = await res.json();
          if (res.ok && json.success) showToastText('User status updated'); else alert(json.message);
        } catch (err) { alert(err.message); }
      }
    });

    // small placeholder: implement UI population from dashboard data
    function populateDashboard(data) {
      // Example: set KPI values by selecting elements you create or add ids to
      // document.querySelector('.kpi .kpi-value').textContent = data.totalOrders || 0;
      console.log('dashboard data', data);
    }
  });
})();
