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

  function showToast(msg) {
    const t = document.querySelector('.toast');
    if (!t) { alert(msg); return; }
    t.textContent = msg;
    t.classList.add('active');
    setTimeout(()=>t.classList.remove('active'), 4000);
  }

  document.addEventListener('DOMContentLoaded', () => {
    // Add-food handler: expects a form or modal that collects name/desc/price/isVeg/available and file input with id 'foodImage'
    const addFoodBtn = document.querySelector('[data-action="add-food"], .add-food-btn, .btn-add-food');
    if (addFoodBtn) {
      addFoodBtn.addEventListener('click', async () => {
        // The page may have a modal; find inputs by name or id. Replace selectors to match your modal inputs.
        const name = document.querySelector('#foodName')?.value || prompt('Food name:');
        const desc = document.querySelector('#foodDesc')?.value || prompt('Description:');
        const price = document.querySelector('#foodPrice')?.value || prompt('Price:');
        const isVeg = !!document.querySelector('#foodIsVeg')?.checked;
        const avail = !!document.querySelector('#foodAvailable')?.checked;
        const fileInput = document.querySelector('#foodImage');

        if (!name || !price) { alert('Name and price are required'); return; }
        const fd = new FormData();
        fd.append('name', name);
        fd.append('description', desc || '');
        fd.append('price', price);
        fd.append('isVeg', isVeg ? 'true' : 'false');
        fd.append('available', avail ? 'true' : 'false');
        if (fileInput && fileInput.files[0]) fd.append('image', fileInput.files[0]);

        // restaurant id: try to read from page (data attribute on sidebar or global)
        const rid = document.body.getAttribute('data-restaurant-id') || window.RESTAURANT_ID;
        if (!rid) { alert('Restaurant ID not found on page'); return; }

        try {
          const res = await fetchWithAuth(`/api/restaurants/${rid}/foods`, { method: 'POST', body: fd });
          const json = await res.json();
          if (res.ok && json.success) {
            showToast('Food added');
            // optionally refresh menu by requesting GET /api/restaurants/{rid}/menu
          } else alert(json.message || 'Add food failed');
        } catch (err) { alert('Network error: ' + err.message); }
      });
    }

    // Order status updates: delegation for buttons with data-order-id and data-new-status attributes
    document.getElementById('app-container')?.addEventListener('click', async (e) => {
      const btn = e.target.closest('[data-order-id][data-new-status]');
      if (!btn) return;
      const orderId = btn.getAttribute('data-order-id');
      const newStatus = btn.getAttribute('data-new-status');
      const rid = document.body.getAttribute('data-restaurant-id') || window.RESTAURANT_ID;
      if (!orderId || !newStatus || !rid) return;
      try {
        const res = await fetchWithAuth(`/api/restaurants/${rid}/orders/${orderId}/status`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ status: newStatus })
        });
        const json = await res.json();
        if (res.ok && json.success) {
          showToast('Order status updated');
          // update table row UI accordingly
        } else alert(json.message || 'Update failed');
      } catch (err) { alert(err.message); }
    });

    // Optionally auto-load menu
    async function loadMenu() {
      const rid = document.body.getAttribute('data-restaurant-id') || window.RESTAURANT_ID;
      if (!rid) return;
      try {
        const res = await fetch(`/api/restaurants/${rid}/menu`);
        const json = await res.json();
        if (res.ok && json.success) {
          // render json.data.items into .menu-grid (implement DOM updates)
          console.log('menu', json.data);
        }
      } catch (err) { console.warn('menu load failed'); }
    }
    loadMenu();
  });
})();
